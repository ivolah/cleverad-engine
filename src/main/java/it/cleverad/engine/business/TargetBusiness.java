package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.Media;
import it.cleverad.engine.persistence.model.service.Target;
import it.cleverad.engine.persistence.repository.service.MediaRepository;
import it.cleverad.engine.persistence.repository.service.TargetRepository;
import it.cleverad.engine.web.dto.TargetDTO;
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
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
@Transactional
public class TargetBusiness {

    @Autowired
    private TargetRepository repository;
    @Autowired
    private MediaRepository mediaRepository;
    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public TargetDTO create(BaseCreateRequest request) {
        Target map = mapper.map(request, Target.class);
        Media media = mediaRepository.findById(request.mediaId).orElseThrow(() -> new ElementCleveradException("Media", request.mediaId));
        map.setMedia(media);
        return TargetDTO.from(repository.save(map));
    }

    // GET BY ID
    public TargetDTO findById(Long id) {
        Target media = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Target", id));
        return TargetDTO.from(media);
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

    // UPDATE
    public TargetDTO update(Long id, TargetBusiness.Filter filter) {
        Target media = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Target", id));
        TargetDTO mediaDTOfrom = TargetDTO.from(media);
        mapper.map(filter, mediaDTOfrom);

        Target mappedEntity = mapper.map(media, Target.class);
        mapper.map(mediaDTOfrom, mappedEntity);
        return TargetDTO.from(repository.save(mappedEntity));
    }

    // SEARCH PAGINATED
    public Page<TargetDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<Target> page = repository.findAll(getSpecification(request), pageable);
        return page.map(TargetDTO::from);
    }

    public Page<TargetDTO> getByMediaId(Long mediaId, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Filter filter = new Filter();
        filter.setMediaId(mediaId);
        Page<Target> page = repository.findAll(getSpecification(filter), pageable);
        return page.map(TargetDTO::from);
    }

    public Page<TargetDTO> getByMediaIdAll(Long mediaId) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("id")));
        Filter filter = new Filter();
        filter.setMediaId(mediaId);
        Page<Target> page = repository.findAll(getSpecification(filter), pageable);
        return page.map(TargetDTO::from);
    }

    /**
     * ============================================================================================================
     **/

    private Specification<Target> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getTarget() != null) {
                predicates.add(cb.equal(root.get("target"), "%" + request.getTarget() + "%"));
            }
            if (request.getMediaId() != null) {
                predicates.add(cb.equal(root.get("media").get("id"), request.getMediaId()));
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
        private Long mediaId;
        private String target;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private String id;
        private Long mediaId;
        private String target;
    }

}