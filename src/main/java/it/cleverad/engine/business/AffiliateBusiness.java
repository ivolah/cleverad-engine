package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.persistence.model.service.Affiliate;
import it.cleverad.engine.persistence.repository.service.AffiliateRepository;
import it.cleverad.engine.persistence.repository.service.DictionaryRepository;
import it.cleverad.engine.persistence.repository.service.FileAdvertiserRepository;
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
import lombok.ToString;
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
    private FileAdvertiserRepository fileAdvertiserRepository;

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
    private JwtUserDetailsService jwtUserDetailsService;
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
            if (dd.getTotalElements() > 0) {
                WalletDTO walletDTO = dd.stream().findFirst().get();
                if (walletDTO.getTotal() > 0) {
                    // invio messaggio specifico su lfatto che c'è un wallet con contenuto
                    throw new PostgresCleveradException("Impossibile cancellare affiliato " + walletDTO.getNome() + " perchè associato ad un wallet.");
                } else {
                    channelBusiness.deleteByIdAffiliate(id);
                    userBusiness.deleteByIdAffiliate(id);
                    repository.deleteById(id);
                    if (dd.getTotalElements() > 0) {
                        walletBusiness.delete(walletDTO.getId());
                    }
                }
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

    // UPADTE
    public AffiliateDTO update(Filter filter) {
        Affiliate affiliate = repository.findById(jwtUserDetailsService.getAffiliateID()).orElseThrow(() -> new ElementCleveradException("Affiliate", jwtUserDetailsService.getAffiliateID()));

        affiliate.setCountry(filter.getCountry());
        affiliate.setPhoneNumber(filter.getPhoneNumber());
        affiliate.setPrimaryMail(filter.getPrimaryMail());
        affiliate.setZipCode(filter.getZipCode());
        affiliate.setCity(filter.getCity());
        affiliate.setStreetNumber(filter.getStreetNumber());
        affiliate.setStreet(filter.getStreet());
        affiliate.setVatNumber(filter.getVatNumber());
        affiliate.setName(filter.getName());
        affiliate.setPaypal(filter.getPaypal());
        affiliate.setIban(filter.getIban());
        affiliate.setBank(filter.getBank());
        affiliate.setSwift(filter.getSwift());
        affiliate.setStatus(true);

        return AffiliateDTO.from(repository.save(affiliate));
    }

    // UPDATE
    public AffiliateDTO update(Long id, Filter filter) {

        Affiliate affiliate = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Affiliate", id));
        filter.setId(id);
        mapper.map(filter, affiliate);
        log.info("filter  " + id);

        affiliate.setLastModificationDate(LocalDateTime.now());
        log.info("filter 3.... " + id);
        affiliate.setDictionaryStatusType(dictionaryRepository.findById(filter.statusId).orElseThrow(() -> new ElementCleveradException("Status", filter.statusId)));
        log.info("filter 4 " + id);
        affiliate.setDictionaryCompanyType(dictionaryRepository.findById(filter.companytypeId).orElseThrow(() -> new ElementCleveradException("Company Type", filter.companytypeId)));
        log.info("filter 5 " + id);
        AffiliateDTO dto =  AffiliateDTO.from(repository.save(affiliate));

        MailService.BaseCreateRequest mailRequest = new MailService.BaseCreateRequest();
        Long statusID = filter.statusId;
        log.info("status  " + statusID);
        if (statusID == 6 && !affiliate.getDictionaryStatusType().getId().equals(statusID)) {
            // invio mail approvato
            if (affiliate.getBrandbuddies()) mailRequest.setTemplateId(21L);
            else mailRequest.setTemplateId(7L);
            mailRequest.setAffiliateId(id);
            mailService.invio(mailRequest);
        } else if (statusID == 7 && !affiliate.getDictionaryStatusType().getId().equals(statusID)) {
            // invio mail non approvato
            if (affiliate.getBrandbuddies()) mailRequest.setTemplateId(22L);
            else mailRequest.setTemplateId(8L);
            mailRequest.setAffiliateId(id);
            mailService.invio(mailRequest);
        }
        log.info("Concluso ::  " + id);

//        Boolean status = affiliate.getStatus();
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

        return dto;
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
        if (request.companytypeId != null && request.companytypeId != 0)
            map.setDictionaryCompanyType(dictionaryRepository.findById(request.companytypeId).orElseThrow(() -> new ElementCleveradException("Company Type", request.companytypeId)));

        if (request.getBrandbuddies() == null)
            map.setBrandbuddies(false);

        if (request.getStatus() == null)
            map.setStatus(false);

        AffiliateDTO dto = AffiliateDTO.from(repository.save(map));

        log.info("CREATO AFFILAITE :: " + dto.getId());

        // creo wallet associato
        WalletBusiness.BaseCreateRequest wal = new WalletBusiness.BaseCreateRequest();
        wal.setAffiliateId(dto.getId());
        wal.setNome("Wallet " + dto.getName());
        wal.setPayed(0.0);
        wal.setResidual(0.0);
        wal.setTotal(0.0);
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
        channelRequest.setBusinessTypeId(request.businessTypeId);
        channelRequest.setStatus(true);
        channelRequest.setRegistrazione(true);
        channelBusiness.create(channelRequest);

        // crea user associato
        UserBusiness.BaseCreateRequest nuovoUser = new UserBusiness.BaseCreateRequest();
        nuovoUser.setAffiliateId(dto.getId());
        nuovoUser.setStatus(false);

        if (request.firstName == null)
            nuovoUser.setName("Aggiorna");
        else
            nuovoUser.setName(request.firstName);

        if (request.lastName == null)
            nuovoUser.setSurname("Aggiorna");
        else
            nuovoUser.setSurname(request.lastName);

        nuovoUser.setEmail(request.primaryMail);
        nuovoUser.setRoleId(4L);
        nuovoUser.setUsername(UUID.randomUUID().toString());
        nuovoUser.setPassword("piciulin");
        UserDTO userDto = userBusiness.create(nuovoUser);

        // crea utente shadow
        UserBusiness.BaseCreateRequest uenteOmbra = new UserBusiness.BaseCreateRequest();
        uenteOmbra.setAffiliateId(dto.getId());
        uenteOmbra.setStatus(true);
        uenteOmbra.setName("Cleverad " + dto.getId());
        uenteOmbra.setEmail(dto.getId() + "_ombra@cleverad.it");
        uenteOmbra.setSurname("Ombra");
        uenteOmbra.setRoleId(4L);
        uenteOmbra.setUsername("cleverad" + dto.getId());
        uenteOmbra.setPassword("2!3_ClEvEr_2!3");
        userBusiness.create(uenteOmbra);

        MailService.BaseCreateRequest mailRequest = new MailService.BaseCreateRequest();

        // invio mail USER
        mailRequest = new MailService.BaseCreateRequest();
        mailRequest.setAffiliateId(dto.getId());
        mailRequest.setUserId(userDto.getId());
        if (request.brandbuddies != null && request.brandbuddies) mailRequest.setTemplateId(20L);
        else mailRequest.setTemplateId(2L);
        mailService.invio(mailRequest);

        // invio mail Affiliate
//        mailRequest.setTemplateId(6L);
//        mailRequest.setAffiliateId(dto.getId());
//        mailRequest.setEmail(request.primaryMail);
//        //mailRequest.setUserId();
//        mailService.invio(mailRequest);

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
                predicates.add(cb.like(cb.upper(root.get("name")), "%" + request.getName().toUpperCase() + "%"));
            }
            if (request.getVatNumber() != null) {
                predicates.add(cb.like(cb.upper(root.get("vatNumber")), "%" + request.getVatNumber().toUpperCase() + "%"));
            }
            if (request.getStreet() != null) {
                predicates.add(cb.like(cb.upper(root.get("street")), "%" + request.getStreet().toUpperCase() + "%"));
            }
            if (request.getStreetNumber() != null) {
                predicates.add(cb.like(cb.upper(root.get("streetNumber")), "%" + request.getStreetNumber().toUpperCase() + "%"));
            }

            if (request.getCity() != null) {
                predicates.add(cb.like(cb.upper(root.get("city")), "%" + request.getCity().toUpperCase() + "%"));
            }
            if (request.getZipCode() != null) {
                predicates.add(cb.equal(root.get("zipCode"), request.getZipCode() + "%"));
            }
            if (request.getPrimaryMail() != null) {
                predicates.add(cb.like(cb.upper(root.get("primaryMail")), "%" + request.getPrimaryMail().toUpperCase() + "%"));
            }
            if (request.getSecondaryMail() != null) {
                predicates.add(cb.like(cb.upper(root.get("secondaryMail")), "%" + request.getSecondaryMail().toUpperCase() + "%"));
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
            if (request.getBrandbuddies() != null) {
                predicates.add(cb.equal(root.get("brandbuddies"), request.getBrandbuddies()));
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
    @ToString
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
        private Long companytypeId;

        private Boolean cb;
        private Boolean brandbuddies;

        private String channelName;
        private String channelUrl;
        private String channelDimension;
        private String channelCountry;
        private String channelCategories;
        private Long channelOwnerId;
        private Long channelTypeId;
        private Long businessTypeId;

        private Long brandbuddiesPlatformId;
        private String brandbuddiesPlatformName;
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
        private Long companytypeId;

        private Boolean cb;
        private Boolean brandbuddies;
    }

}