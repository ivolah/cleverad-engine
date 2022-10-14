package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.MediaType;
import it.cleverad.engine.persistence.repository.MediaTypeRepository;
import it.cleverad.engine.web.dto.MediaTypeDTO;
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
public class MediaTypeBusiness {

    @Autowired
    private MediaTypeRepository repository;

    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public MediaTypeDTO create(BaseCreateRequest request) {
        MediaType map = mapper.map(request, MediaType.class);
        return MediaTypeDTO.from(repository.save(map));
    }

    // GET BY ID
    public MediaTypeDTO findById(Long id) {
        MediaType media = repository.findById(id).orElseThrow(() -> new ElementCleveradException("MediaType", id));
        return MediaTypeDTO.from(media);
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
    public MediaTypeDTO update(Long id, Filter filter) {
        try {
            MediaType media = repository.findById(id).orElseThrow(() -> new ElementCleveradException("MediaType", id));
            MediaTypeDTO mediaDTOfrom = MediaTypeDTO.from(media);
            mapper.map(filter, mediaDTOfrom);

            MediaType mappedEntity = mapper.map(media, MediaType.class);
            mapper.map(mediaDTOfrom, mappedEntity);
            return MediaTypeDTO.from(repository.save(mappedEntity));
        } catch (Exception e) {
            log.error("Errore in update", e);
            return null;
        }
    }

    // SEARCH PAGINATED
    public Page<MediaTypeDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<MediaType> page = repository.findAll(getSpecification(request), pageable);
        return page.map(MediaTypeDTO::from);
    }

    /**
     * ============================================================================================================
     **/
    private Specification<MediaType> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getName() != null) {
                predicates.add(cb.equal(root.get("name"), request.getName()));
            }

            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }

            if (request.getDescription() != null) {
                predicates.add(cb.equal(root.get("description"), request.getDescription()));
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
        private String description;
        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private String name;
        private String description;
        private String status;
    }

}
