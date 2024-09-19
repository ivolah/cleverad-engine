package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.Advertiser;
import it.cleverad.engine.persistence.repository.service.AdvertiserRepository;
import it.cleverad.engine.persistence.repository.service.DictionaryRepository;
import it.cleverad.engine.web.dto.AdvertiserDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
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
public class AdvertiserBusiness {

    @Autowired
    private AdvertiserRepository repository;
    @Autowired
    private RepresentativeBusiness representativeBusiness;
    @Autowired
    private Mapper mapper;
    @Autowired
    private DictionaryRepository dictionaryRepository;
    @Autowired
    private UserBusiness userBusiness;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public AdvertiserDTO create(BaseCreateRequest request) {
        Advertiser map = mapper.map(request, Advertiser.class);
        map.setCreationDate(LocalDateTime.now());
        map.setLastModificationDate(LocalDateTime.now());
        map.setStatus(true);
        map.setDictionaryTermType(dictionaryRepository.findById(request.termId).orElseThrow(() -> new ElementCleveradException("TERM", request.termId)));
        map.setDictionaryVatType(dictionaryRepository.findById(request.vatId).orElseThrow(() -> new ElementCleveradException("VAT", request.vatId)));
        AdvertiserDTO dto = AdvertiserDTO.from(repository.save(map));


        // crea user associato ad Advertiser
        UserBusiness.BaseCreateRequest operatoreAdvertiser = new UserBusiness.BaseCreateRequest();
        operatoreAdvertiser.setAdvertiserId(dto.getId());
        operatoreAdvertiser.setStatus(false);
        operatoreAdvertiser.setName("Operatore");
        if (request.getName() == null)
            operatoreAdvertiser.setSurname("Aggiorna");
        else
            operatoreAdvertiser.setSurname(request.getName());
        operatoreAdvertiser.setEmail(request.primaryMail);
        operatoreAdvertiser.setRoleId(555L);
        operatoreAdvertiser.setRole("Advertiser");
        operatoreAdvertiser.setUsername(UUID.randomUUID());
        operatoreAdvertiser.setPassword("piciulin");
        userBusiness.create(operatoreAdvertiser);

        // crea utente shadow
        UserBusiness.BaseCreateRequest opertatoreOmbra = new UserBusiness.BaseCreateRequest();
        opertatoreOmbra.setAdvertiserId(dto.getId());
        opertatoreOmbra.setStatus(true);
        opertatoreOmbra.setName("Cleverad " + dto.getId());
        opertatoreOmbra.setEmail(dto.getId() + "_ombra@cleverad.it");
        opertatoreOmbra.setSurname("Ombra");
        opertatoreOmbra.setRoleId(555L);
        opertatoreOmbra.setRole("AdvertiserOmbra");
        opertatoreOmbra.setUsername("cleverad" + dto.getId());
        opertatoreOmbra.setPassword("2!3_ClEvEr_2!3");
        userBusiness.create(opertatoreOmbra);

        // invio mail ad opertatore Advertiser
/*       MailService.BaseCreateRequest mailRequest = new MailService.BaseCreateRequest();
        mailRequest = new MailService.BaseCreateRequest();
        mailRequest.setAffiliateId(dto.getId());
        mailRequest.setUserId(userDto.getId());
        else mailRequest.setTemplateId(XXXXXL);
        mailService.invio(mailRequest);
 */

        return dto;
    }

    // GET BY ID
    public AdvertiserDTO findById(Long id) {
        Advertiser advertiser = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Advertiser", id));
        return AdvertiserDTO.from(advertiser);
    }

    // DELETE BY ID
    public void delete(Long id) {
        try {
            representativeBusiness.findByIdAdvertiser(id).forEach(representativeDTO -> representativeBusiness.delete(representativeDTO.getId()));
            userBusiness.deleteByIdAdvertiser(id);
            repository.deleteById(id);
        } catch (ConstraintViolationException ex) {
            throw ex;
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    // SEARCH PAGINATED
    public Page<AdvertiserDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("name")));
        request.setStatus(true);
        Page<Advertiser> page = repository.findAll(getSpecification(request), pageable);
        return page.map(AdvertiserDTO::from);
    }

    // UPDATE
    public AdvertiserDTO update(Long id, Filter filter) {
        Advertiser advertiser = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Advertiser", id));
        mapper.map(filter, advertiser);
        advertiser.setId(id);
        advertiser.setLastModificationDate(LocalDateTime.now());
        advertiser.setDictionaryTermType(dictionaryRepository.findById(filter.termId).orElseThrow(() -> new ElementCleveradException("TERM", filter.termId)));
        advertiser.setDictionaryVatType(dictionaryRepository.findById(filter.vatId).orElseThrow(() -> new ElementCleveradException("VAT", filter.vatId)));
        return AdvertiserDTO.from(repository.save(advertiser));
    }

    public AdvertiserDTO disable(Long id) {
        Advertiser entity = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Channel", id));
        entity.setStatus(false);
        return AdvertiserDTO.from(repository.save(entity));
    }

    public AdvertiserDTO enable(Long id) {
        Advertiser entity = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Channel", id));
        entity.setStatus(true);
        return AdvertiserDTO.from(repository.save(entity));
    }

    /**
     * ============================================================================================================
     **/
    private Specification<Advertiser> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getName() != null) {
                predicates.add(cb.like(cb.upper(root.get("name")), "%" + request.getName().toUpperCase() + "%"));
            }
            if (request.getVatNumber() != null) {
                predicates.add(cb.like(root.get("vatNumber"), "%" + request.getVatNumber() + "%"));
            }
            if (request.getStreet() != null) {
                predicates.add(cb.like(cb.upper(root.get("street")), "%" + request.getStreet().toUpperCase() + "%"));
            }
            if (request.getStreetNumber() != null) {
                predicates.add(cb.equal(root.get("streetNumber"), request.getStreetNumber()));
            }

            if (request.getCity() != null) {
                predicates.add(cb.like(cb.upper(root.get("city")), "%" + request.getCity().toUpperCase() + "%"));
            }
            if (request.getZipCode() != null) {
                predicates.add(cb.like(root.get("zipCode"), "%" + request.getZipCode() + "%"));
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
        private String country;
        private Long termId;
        private Long vatId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private String name;
        private String vatNumber;
        private String street;
        private String country;
        private String streetNumber;
        private String city;
        private String zipCode;
        private String primaryMail;
        private String secondaryMail;
        private Boolean status;
        private Instant creationDateFrom;
        private Instant creationDateTo;
        private Instant lastModificationDateFrom;
        private Instant lastModificationDateTo;
        private Long termId;
        private Long vatId;
    }

}