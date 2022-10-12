package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.Affiliate;
import it.cleverad.engine.persistence.repository.AffiliateRepository;
import it.cleverad.engine.web.dto.AffiliateDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional
public class AffiliateBusiness {

    @Autowired
    private AffiliateRepository repository;

    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public AffiliateDTO create(BaseCreateRequest request) {
        Affiliate map = mapper.map(request, Affiliate.class);
        map.setCreationDate(LocalDate.now());
        map.setLastModificationDate(LocalDate.now());
        return AffiliateDTO.from(repository.save(map));
    }

    // GET BY ID
    public AffiliateDTO findById(Long id) {
        try {
            Affiliate affiliate = repository.findById(id).orElseThrow(Exception::new);
            return AffiliateDTO.from(affiliate);
        } catch (Exception e) {
            log.error("Errore in findById", e);
            return null;
        }
    }

    // DELETE BY ID
    public void delete(Long id) {
        repository.deleteById(id);
    }

    // SEARCH PAGINATED
    public Page<AffiliateDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<Affiliate> page = repository.findAll(getSpecification(request), pageable);

        return page.map(AffiliateDTO::from);
    }

    // UPDATE
    public AffiliateDTO update(Long id, Filter filter) {
        try {
            Affiliate affiliate = repository.findById(id).orElseThrow(Exception::new);
            AffiliateDTO affiliateDTOfrom = AffiliateDTO.from(affiliate);

            mapper.map(filter, affiliateDTOfrom);

            Affiliate mappedEntity = mapper.map(affiliate, Affiliate.class);
            mappedEntity.setLastModificationDate(LocalDate.now());
            mapper.map(affiliateDTOfrom, mappedEntity);

            return AffiliateDTO.from(repository.save(mappedEntity));
        } catch (Exception e) {
            log.error("Errore in update", e);
            return null;
        }
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
                predicates.add(cb.equal(root.get("name"), request.getName()));
            }
            if (request.getVatNumber() != null) {
                predicates.add(cb.equal(root.get("vatNumber"), request.getVatNumber()));
            }
            if (request.getStreet() != null) {
                predicates.add(cb.equal(root.get("street"), request.getStreet()));
            }
            if (request.getStreetNumber() != null) {
                predicates.add(cb.equal(root.get("streetNumber"), request.getStreetNumber()));
            }

            if (request.getCity() != null) {
                predicates.add(cb.equal(root.get("city"), request.getCity()));
            }
            if (request.getZipCode() != null) {
                predicates.add(cb.equal(root.get("zipCode"), request.getZipCode()));
            }
            if (request.getPrimaryMail() != null) {
                predicates.add(cb.equal(root.get("primaryMail"), request.getPrimaryMail()));
            }
            if (request.getSecondaryMail() != null) {
                predicates.add(cb.equal(root.get("secondaryMail"), request.getSecondaryMail()));
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
        private Instant creationDateFrom;
        private Instant creationDateTo;
        private Instant lastModificationDateFrom;
        private Instant lastModificationDateTo;
    }

}
