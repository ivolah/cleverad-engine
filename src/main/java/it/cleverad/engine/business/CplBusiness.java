package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.Cpl;
import it.cleverad.engine.persistence.repository.CplRepository;
import it.cleverad.engine.web.dto.CplDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional
public class CplBusiness {

    @Autowired
    private CplRepository repository;

    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public CplDTO create(BaseCreateRequest request) {
        Cpl map = mapper.map(request, Cpl.class);
        map.setDate(LocalDateTime.now());
        map.setStatus(false);
        return CplDTO.from(repository.save(map));
    }

    // GET BY ID
    public CplDTO findById(Long id) {
            Cpl channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException(id));
            return CplDTO.from(channel);
    }

    // DELETE BY ID
    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ee) {
            log.warn("Impossibile cancellare commissione.");
            throw new PostgresCleveradException("Impossibile cancellare cpl");
        }
    }

    // SEARCH PAGINATED
    public Page<CplDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<Cpl> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CplDTO::from);
    }

    // UPDATE
    public CplDTO update(Long id, Filter filter) {
        try {
            Cpl channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException(id));
            CplDTO campaignDTOfrom = CplDTO.from(channel);

            mapper.map(filter, campaignDTOfrom);

            Cpl mappedEntity = mapper.map(channel, Cpl.class);
            mapper.map(campaignDTOfrom, mappedEntity);

            return CplDTO.from(repository.save(mappedEntity));
        } catch (Exception e) {
            log.error("Errore in update", e);
            return null;
        }
    }


    /**
     * ============================================================================================================
     **/
    private Specification<Cpl> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }
            if (request.getDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), LocalDateTime.ofInstant(request.getDateFrom(), ZoneOffset.UTC)));
            }
            if (request.getDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), LocalDateTime.ofInstant(request.getDateTo().plus(1, ChronoUnit.DAYS), ZoneOffset.UTC)));
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
        private String refferal;
        private String data;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;

        private String refferal;
        private String data;

        private Boolean status;
        private Instant dateFrom;
        private Instant dateTo;
    }

}

