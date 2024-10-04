package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.Url;
import it.cleverad.engine.persistence.repository.service.UrlRepository;
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

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional
public class UrlBusiness {

    @Autowired
    private UrlRepository repository;
    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/






    // CREATE
    public Url create(BaseCreateRequest request) {
        Url map = mapper.map(request, Url.class);
        return repository.save(map);
    }

    // GET BY ID
    public Url findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ElementCleveradException("Url", id));
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
    public Page<Url> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        return repository.findAll(getSpecification(request), pageable);
    }    // SEARCH PAGINATED

    public Url findByLong(String longUrl) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("id")));
        Filter request = new Filter();
        request.setLongUrl(longUrl);
        return repository.findAll(getSpecification(request), pageable).get().findFirst().orElse(null);
    }

    /**
     * ============================================================================================================
     **/

    private Specification<Url> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getLongUrl() != null) {
                predicates.add(cb.equal(root.get("longUrl"), request.getLongUrl()));
            }
            if (request.getTiny() != null) {
                predicates.add(cb.equal(root.get("tiny"), request.getTiny()));
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
        private Long id;
        private String longUrl;
        private String tiny;
        private String alias;
        private LocalDate createdDate = LocalDate.now();
        private LocalDate expiresDate = LocalDate.now();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private String alias;
        private String longUrl;
        private String tiny;
        private LocalDate expiresDate;
    }

}