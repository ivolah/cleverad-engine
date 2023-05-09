package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.Affiliate;
import it.cleverad.engine.persistence.repository.service.AffiliateRepository;
import it.cleverad.engine.persistence.repository.service.DictionaryRepository;
import it.cleverad.engine.service.MailService;
import it.cleverad.engine.web.dto.AffiliateDTO;
import it.cleverad.engine.web.dto.DictionaryDTO;
import it.cleverad.engine.web.dto.UserDTO;
import it.cleverad.engine.web.dto.WalletDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import it.cleverad.engine.web.exception.PostgresCleveradException;
import it.cleverad.engine.web.exception.PostgresDeleteCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@Transactional
public class AffiliateBusiness {

    @Autowired
    private AffiliateRepository repository;
    @Autowired
    private WalletBusiness walletBusiness;
    @Autowired
    private ChannelBusiness channelBusiness;
    @Autowired
    private DictionaryBusiness dictionaryBusiness;
    @Autowired
    private DictionaryRepository dictionaryRepository;
    @Autowired
    private UserBusiness userBusiness;
    @Autowired
    private MailService mailService;

    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // GET BY ID
    public AffiliateDTO findById(Long id) {
        Affiliate affiliate = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Affiliate", id));
        return AffiliateDTO.from(affiliate);
    }

    // DELETE BY ID
    public void delete(Long id) {
        try {
            Page<WalletDTO> dd = walletBusiness.findByIdAffilaite(id);
            WalletDTO dto = dd.stream().findFirst().get();
            if (dto.getTotal() > 0) {
                // invio messaggio specifico su lfatto che c'è un wallet con contenuto
                throw new PostgresCleveradException("Impossibile cancellare affiliato " + dto.getNome() + " perchè associato ad un wallet.");
            } else {
                walletBusiness.delete(dto.getId());
                repository.deleteById(id);
            }
        } catch (ConstraintViolationException ex) {
            throw ex;
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    // SEARCH PAGINATED
    public Page<AffiliateDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        Page<Affiliate> page = repository.findAll(getSpecification(request), pageable);
        return page.map(AffiliateDTO::from);
    }

    // UPDATE
    public AffiliateDTO update(Long id, Filter filter) {
        Affiliate affiliate = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Affiliate", id));

        AffiliateDTO affiliateDTOfrom = AffiliateDTO.from(affiliate);
        mapper.map(filter, affiliateDTOfrom);

        Affiliate mappedEntity = mapper.map(affiliate, Affiliate.class);
        mappedEntity.setLastModificationDate(LocalDateTime.now());
        mappedEntity.setDictionaryStatusType(dictionaryRepository.findById(filter.statusId).orElseThrow(() -> new ElementCleveradException("Status", filter.statusId)));
        mapper.map(affiliateDTOfrom, mappedEntity);

        Boolean status = affiliate.getStatus();
        MailService.BaseCreateRequest mailRequest = new MailService.BaseCreateRequest();
//        if (!status && filter.status) {
//            // invio mail approvato
//            mailRequest.setTemplateId(7L);
//            mailRequest.setAffiliateId(id);
//            mailService.invio(mailRequest);
//        } else if (!status && !filter.status) {
//            // invio mail non approvato
//            mailRequest.setTemplateId(8L);
//            mailRequest.setAffiliateId(id);
//            mailService.invio(mailRequest);
//        }

        Long statusID = filter.statusId;
        if (statusID == 6) {
            // invio mail approvato
            mailRequest.setTemplateId(7L);
            mailRequest.setAffiliateId(id);
            mailService.invio(mailRequest);
        } else if (statusID == 7) {
            // invio mail non approvato
            mailRequest.setTemplateId(8L);
            mailRequest.setAffiliateId(id);
            mailService.invio(mailRequest);
        }

        return AffiliateDTO.from(repository.save(mappedEntity));
    }

    //  GET TIPI
    public Page<DictionaryDTO> getTypeCompany() {
        return dictionaryBusiness.getTypeCompany();
    }

    public Page<DictionaryDTO> getChannelTypeAffiliate() {
        return dictionaryBusiness.getChannelTypeAffiliate();
    }

    public List<String> listEmails(Long affiliateId) {
        List<String> lista = new ArrayList<>();
        Affiliate affiliate = repository.findById(affiliateId).orElseThrow(() -> new ElementCleveradException("Affiliate", affiliateId));
        lista.add(affiliate.getPrimaryMail());
        lista.add(affiliate.getSecondaryMail());
        return lista;
    }

    // CREATE
    public AffiliateDTO create(BaseCreateRequest request) {
        Affiliate map = mapper.map(request, Affiliate.class);
        request.statusId = 5L;
        map.setDictionaryStatusType(dictionaryRepository.findById(request.statusId).orElseThrow(() -> new ElementCleveradException("Status", request.statusId)));
        AffiliateDTO dto = AffiliateDTO.from(repository.save(map));

        // invio mail Affiliate
        MailService.BaseCreateRequest mailRequest = new MailService.BaseCreateRequest();
//        mailRequest.setTemplateId(6L);
//        mailRequest.setAffiliateId(dto.getId());
//        mailRequest.setEmail(request.primaryMail);
//        //mailRequest.setUserId();
//        mailService.invio(mailRequest);

        // creo wallet associato
        WalletBusiness.BaseCreateRequest wal = new WalletBusiness.BaseCreateRequest();
        wal.setAffiliateId(dto.getId());
        wal.setNome("Wallet " + dto.getName());
        wal.setPayed(0.0);
        wal.setResidual(0.0);
        wal.setTotal(0.0);
        wal.setStatus(true);
        walletBusiness.create(wal);

        // creo channel
        ChannelBusiness.BaseCreateRequest channelRequest = new ChannelBusiness.BaseCreateRequest();
        channelRequest.setAffiliateId(dto.getId());
        channelRequest.setName(request.channelName);
        channelRequest.setUrl(request.channelUrl);
        channelRequest.setDimension(request.channelDimension);
        channelRequest.setCountry(request.channelCountry);
        channelRequest.setOwnerId(request.channelOwnerId);
        channelRequest.setCategories(request.channelCategories);
        channelRequest.setDictionaryId(12L);
        channelRequest.setTypeId(request.channelTypeId);
        channelRequest.setStatus(true);
        channelRequest.setRegistrazione(true);
        channelBusiness.create(channelRequest);

        // creo user associato
        UserBusiness.BaseCreateRequest nuovoUser = new UserBusiness.BaseCreateRequest();
        nuovoUser.setAffiliateId(dto.getId());
        nuovoUser.setStatus(false);
        nuovoUser.setName(request.firstName);
        nuovoUser.setEmail(request.primaryMail);
        nuovoUser.setSurname(request.getLastName());
        nuovoUser.setRoleId(4L);
        String uuid = UUID.randomUUID().toString();
        nuovoUser.setUsername(uuid);
        nuovoUser.setPassword("piciulin");
        UserDTO userDto = userBusiness.create(nuovoUser);

        //cre utente shadow
        UserBusiness.BaseCreateRequest uenteOmbra = new UserBusiness.BaseCreateRequest();
        uenteOmbra.setAffiliateId(dto.getId());
        uenteOmbra.setStatus(true);
        uenteOmbra.setName("Cleverad "+ dto.getId());
        uenteOmbra.setEmail(dto.getId() + "_ombra@cleverad.it");
        uenteOmbra.setSurname("Ombra");
        uenteOmbra.setRoleId(4L);
        uenteOmbra.setUsername("cleverad" + dto.getId());
        uenteOmbra.setPassword("2!3_ClEvEr_2!3");
        userBusiness.create(uenteOmbra);

        // invio mail USER
        mailRequest = new MailService.BaseCreateRequest();
        mailRequest.setTemplateId(2L);
        mailRequest.setAffiliateId(dto.getId());
        mailRequest.setUserId(userDto.getId());
        mailService.invio(mailRequest);

        return dto;
    }

    /**
     * ============================================================================================================
     **/
    private Specification<Affiliate> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getName() != null) {
                predicates.add(cb.like(root.get("name"), request.getName()));
            }
            if (request.getVatNumber() != null) {
                predicates.add(cb.like(root.get("vatNumber"), request.getVatNumber()));
            }
            if (request.getStreet() != null) {
                predicates.add(cb.like(root.get("street"), request.getStreet()));
            }
            if (request.getStreetNumber() != null) {
                predicates.add(cb.like(root.get("streetNumber"), request.getStreetNumber()));
            }

            if (request.getCity() != null) {
                predicates.add(cb.like(root.get("city"), request.getCity()));
            }
            if (request.getZipCode() != null) {
                predicates.add(cb.equal(root.get("zipCode"), request.getZipCode()));
            }
            if (request.getPrimaryMail() != null) {
                predicates.add(cb.like(root.get("primaryMail"), request.getPrimaryMail()));
            }
            if (request.getSecondaryMail() != null) {
                predicates.add(cb.like(root.get("secondaryMail"), request.getSecondaryMail()));
            }
            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }
            if (request.getCreationDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("creationDate"), LocalDateTime.ofInstant(request.getCreationDateFrom(), ZoneOffset.UTC)));
            }
            if (request.getCreationDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("creationDate"), LocalDateTime.ofInstant(request.getCreationDateTo().plus(1, ChronoUnit.DAYS), ZoneOffset.UTC)));
            }

            if (request.getLastModificationDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("lastModificationDate"), LocalDateTime.ofInstant(request.getLastModificationDateFrom(), ZoneOffset.UTC)));
            }
            if (request.getLastModificationDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("lastModificationDate"), LocalDateTime.ofInstant(request.getLastModificationDateTo().plus(1, ChronoUnit.DAYS), ZoneOffset.UTC)));
            }

            completePredicate = cb.and(predicates.toArray(new Predicate[0]));

            return completePredicate;
        };
    }

    /**
     * ============================================================================================================
     **/

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BaseCreateRequest {

        private String name;
        private String vatNumber;
        private String street;
        private String streetNumber;
        private String city;
        private String zipCode;
        private String primaryMail;
        private String secondaryMail;
        private Boolean status;
        private Long statusId;
        private String country;
        private String phonePrefix;
        private String phoneNumber;
        private String note;
        private String bank;
        private String iban;
        private String swift;
        private String paypal;

        private String province;

        private String firstName;
        private String lastName;
        private String nomeSitoSocial;
        private String urlSitoSocial;
        private Long companytypeId;
        private Long categorytypeId;
        private String contenutoSito;

        private Boolean cb;

        private String channelName;
        private String channelUrl;
        private String channelDimension;
        private String channelCountry;
        private String channelCategories;
        private Long channelOwnerId;
        private Long channelTypeId;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;

        private String name;
        private String vatNumber;
        private String street;
        private String streetNumber;
        private String city;
        private String zipCode;
        private String primaryMail;
        private String secondaryMail;
        private Boolean status;
        private Long statusId;
        private String phonePrefix;
        private String phoneNumber;
        private String note;
        private String bank;
        private String iban;
        private String swift;
        private String paypal;
        private Instant creationDateFrom;
        private Instant creationDateTo;
        private Instant lastModificationDateFrom;
        private Instant lastModificationDateTo;
        private String province;
        private String country;

        private String firstName;
        private String lastName;
        private String nomeSitoSocial;
        private String urlSitoSocial;
        private Long companytypeId;
        private Long categorytypeId;
        private String contenutoSito;

        private Boolean cb;
    }

}
