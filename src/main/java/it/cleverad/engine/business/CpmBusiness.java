package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.Cpm;
import it.cleverad.engine.persistence.repository.CpmRepository;
import it.cleverad.engine.web.dto.CpmDTO;
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
import java.util.Base64;
import java.util.List;

@Slf4j
@Component
@Transactional
public class CpmBusiness {

    @Autowired
    private CpmRepository repository;

    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public CpmDTO create(BaseCreateRequest request) {
        Cpm map = mapper.map(request, Cpm.class);
        byte[] decoder = Base64.getDecoder().decode(request.getImageCode());
        String imageCode = new String(decoder);
        String[] splits = imageCode.split("-");
        map.setImageId(Long.valueOf(splits[0]));
        map.setMediaId(Long.valueOf(splits[1]));
        return CpmDTO.from(repository.save(map));
    }

    // GET BY ID
    public CpmDTO findById(Long id) {
        Cpm channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Cpm", id));
        return CpmDTO.from(channel);
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
    public Page<CpmDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<Cpm> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CpmDTO::from);
    }

    // UPDATE
    public CpmDTO update(Long id, Filter filter) {
        try {
            Cpm channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Cpm", id));
            CpmDTO campaignDTOfrom = CpmDTO.from(channel);

            mapper.map(filter, campaignDTOfrom);

            Cpm mappedEntity = mapper.map(channel, Cpm.class);
            mapper.map(campaignDTOfrom, mappedEntity);

            return CpmDTO.from(repository.save(mappedEntity));
        } catch (Exception e) {
            log.error("Errore in update", e);
            return null;
        }
    }


    /**
     * ============================================================================================================
     **/
    private Specification<Cpm> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getRead() != null) {
                predicates.add(cb.equal(root.get("read"), request.getRead()));
            }
            if (request.getTimeStampFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("timeStamp"), LocalDateTime.ofInstant(request.getTimeStampFrom(), ZoneOffset.UTC)));
            }
            if (request.getTimeStampTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("timeStamp"), LocalDateTime.ofInstant(request.getTimeStampTo().plus(1, ChronoUnit.DAYS), ZoneOffset.UTC)));
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
        private String imageCode;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;

        private Long campaignId;
        private Long imageId;
        private Long mediaId;

        private Boolean read;
        private Instant timeStampFrom;
        private Instant timeStampTo;
    }

}

