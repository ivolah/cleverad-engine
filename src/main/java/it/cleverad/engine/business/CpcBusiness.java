package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.Cpc;
import it.cleverad.engine.persistence.repository.CpcRepository;
import it.cleverad.engine.web.dto.CpcDTO;
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
public class CpcBusiness {

    @Autowired
    private CpcRepository repository;

    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public CpcDTO create(BaseCreateRequest request) {
        Cpc map = mapper.map(request, Cpc.class);
        map.setDate(LocalDateTime.now());
        map.setStatus(false);
        return CpcDTO.from(repository.save(map));
    }

    // GET BY ID
    public CpcDTO findById(Long id) {
        try {
            Cpc channel = repository.findById(id).orElseThrow(Exception::new);
            return CpcDTO.from(channel);
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
            throw new PostgresCleveradException("Impossibile cancellare cpc ");
        }
    }

    // SEARCH PAGINATED
    public Page<CpcDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<Cpc> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CpcDTO::from);
    }

    // UPDATE
    public CpcDTO update(Long id, Filter filter) {
        try {
            Cpc channel = repository.findById(id).orElseThrow(Exception::new);
            CpcDTO campaignDTOfrom = CpcDTO.from(channel);

            mapper.map(filter, campaignDTOfrom);

            Cpc mappedEntity = mapper.map(channel, Cpc.class);
            mapper.map(campaignDTOfrom, mappedEntity);

            return CpcDTO.from(repository.save(mappedEntity));
        } catch (Exception e) {
            log.error("Errore in update", e);
            return null;
        }
    }


    /**
     * ============================================================================================================
     **/
    private Specification<Cpc> getSpecification(Filter request) {
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
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;

        private String refferal;

        private Boolean status;

        private Instant dateFrom;
        private Instant dateTo;
    }

}

