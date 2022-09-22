package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.Commission;
import it.cleverad.engine.persistence.repository.CommissionRepository;
import it.cleverad.engine.web.dto.CommissionDTO;
import it.cleverad.engine.web.exception.PostgresCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
public class CommissionBusiness {

    @Autowired
    private CommissionRepository repository;

    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public CommissionDTO create(BaseCreateRequest request) {
        Commission map = mapper.map(request, Commission.class);
        map.setCreationDate(LocalDateTime.now());
        map.setLastModificationDate(LocalDateTime.now());
        return CommissionDTO.from(repository.save(map));
    }

    // GET BY ID
    public CommissionDTO findById(Long id) {
        try {
            Commission commission = repository.findById(id).orElseThrow(Exception::new);
            return  CommissionDTO.from(commission);
        } catch (Exception e) {
            log.error("Errore in findById", e);
            return null;
        }
    }

    // DELETE BY ID
    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ee) {
            log.warn("Impossibile cancellare commissione.");
            throw new PostgresCleveradException("Impossibile cancellare commissione perchè già utilizzata in una campagna");
        }
    }

    // SEARCH PAGINATED
    public Page<CommissionDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<Commission> page = repository.findAll(getSpecification(request), pageable);

        return page.map(CommissionDTO::from);
    }

    // UPDATE
    public CommissionDTO update(Long id, Filter filter) {
        try {
            Commission ommission = repository.findById(id).orElseThrow(Exception::new);
            CommissionDTO campaignDTOfrom = CommissionDTO.from(ommission);

            mapper.map(filter,campaignDTOfrom );

            Commission mappedEntity = mapper.map(ommission, Commission.class);
            mapper.map(campaignDTOfrom, mappedEntity);

            return CommissionDTO.from(repository.save(mappedEntity));
        } catch (Exception e) {
            log.error("Errore in update", e);
            return null;
        }
    }


    /**
     * ============================================================================================================
     **/
    private Specification<Commission> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }

            if (request.getName() != null) {
                predicates.add(cb.equal(root.get("name"), request.getName()));
            }

            if (request.getValue() != null) {
                predicates.add(cb.equal(root.get("value"), request.getValue()));
            }
            if (request.getDescription() != null) {
                predicates.add(cb.equal(root.get("description"), request.getDescription()));
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
        private String value;
        private String description;
        private Boolean status;
        private String idType;
        private LocalDate dueDate;
        private Long campaignId;

        private LocalDateTime creationDate;
        private LocalDateTime lastModificationDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {

        private Long id;

        private String name;
        private String value;
        private String description;
        private Boolean status;
        private String idType;
        private LocalDate dueDate;
        private Long campaignId;

        private Instant creationDateFrom;
        private Instant creationDateTo;
        private Instant lastModificationDateFrom;
        private Instant lastModificationDateTo;

    }

}

