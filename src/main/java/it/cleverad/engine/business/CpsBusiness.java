package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.tracking.Cps;
import it.cleverad.engine.persistence.repository.tracking.CpsRepository;
import it.cleverad.engine.web.dto.CpsDTO;
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
import org.springframework.format.annotation.DateTimeFormat;
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
public class CpsBusiness {

    @Autowired
    private CpsRepository repository;

    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public CpsDTO create(BaseCreateRequest request) {
        Cps map = mapper.map(request, Cps.class);
        map.setDate(LocalDateTime.now());
        map.setRead(false);
        return CpsDTO.from(repository.save(map));
    }

    // GET BY ID
    public CpsDTO findById(Long id) {
        Cps channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Cps", id));
        return CpsDTO.from(channel);
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
    public Page<CpsDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        Page<Cps> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CpsDTO::from);
    }

    // UPDATE
    public CpsDTO update(Long id, Filter filter) {
        Cps channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Cps", id));
        CpsDTO campaignDTOfrom = CpsDTO.from(channel);

        mapper.map(filter, campaignDTOfrom);

        Cps mappedEntity = mapper.map(channel, Cps.class);
        mapper.map(campaignDTOfrom, mappedEntity);

        return CpsDTO.from(repository.save(mappedEntity));
    }

    public Page<CpsDTO> getUnread() {
        Pageable pageable = PageRequest.of(0, 1000, Sort.by(Sort.Order.desc("id")));
        Filter request = new Filter();
        request.setRead(false);
        Page<Cps> page = repository.findAll(getSpecification(request), pageable);
        log.trace("UNREAD {}", page.getTotalElements());
        return page.map(CpsDTO::from);
    }

    public void setRead(long id) {
        Cps media = repository.findById(id).get();
        media.setRead(true);
        repository.save(media);
    }

    /**
     * ============================================================================================================
     **/
    private Specification<Cps> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getRefferal() != null) {
                predicates.add(cb.equal(root.get("refferal"), request.getRefferal()));
            }
            if (request.getRead() != null) {
                predicates.add(cb.equal(root.get("read"), request.getRead()));
            }
            if (request.getIp() != null) {
                predicates.add(cb.equal(root.get("ip"), request.getIp()));
            }
            if (request.getDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), request.getDateFrom().atStartOfDay()));
            }
            if (request.getDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), request.getDateTo().plus(1, ChronoUnit.DAYS).atStartOfDay()));
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
        private String ip;
        private String agent;
        private String data;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private String refferal;
        private String cid;
        private String ip;
        private String agent;
        private String data;
        private Boolean read;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateFrom;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateTo;
    }

}
