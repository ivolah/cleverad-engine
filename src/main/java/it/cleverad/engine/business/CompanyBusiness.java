package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.Company;
import it.cleverad.engine.persistence.repository.CompanyRepository;
import it.cleverad.engine.web.dto.CompanyDTO;
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

@Slf4j
@Component
@Transactional
public class CompanyBusiness {

    @Autowired
    private CompanyRepository repository;

    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public CompanyDTO create(BaseCreateRequest request) {
        Company map = mapper.map(request, Company.class);
        return CompanyDTO.from(repository.save(map));
    }

    // GET BY ID
    public CompanyDTO findById(Long id) {
        Company Company = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Company", id));
        return CompanyDTO.from(Company);
    }

    // DELETE BY ID
    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (ConstraintViolationException ex) {
            throw ex;
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    // SEARCH PAGINATED
    public Page<CompanyDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<Company> page = repository.findAll(getSpecification(request), pageable);

        return page.map(CompanyDTO::from);
    }

    // UPDATE
    public CompanyDTO update(Long id, Filter filter) {
        Company Company = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Company", id));
        CompanyDTO CompanyDTOfrom = CompanyDTO.from(Company);

        mapper.map(filter, CompanyDTOfrom);

        Company mappedEntity = mapper.map(Company, Company.class);
        mappedEntity.setLastModificationDate(LocalDateTime.now());
        mapper.map(CompanyDTOfrom, mappedEntity);

        return CompanyDTO.from(repository.save(mappedEntity));
    }

    /**
     * ============================================================================================================
     **/
    private Specification<Company> getSpecification(Filter request) {
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
