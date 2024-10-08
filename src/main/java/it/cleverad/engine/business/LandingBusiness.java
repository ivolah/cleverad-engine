package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.Landing;
import it.cleverad.engine.persistence.repository.service.LandingRepository;
import it.cleverad.engine.web.dto.LandingDTO;
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
public class LandingBusiness {

    @Autowired
    private LandingRepository repository;
    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public LandingDTO create(BaseCreateRequest request) {
        Landing map = mapper.map(request, Landing.class);
        return LandingDTO.from(repository.save(map));
    }

    // GET BY ID
    public LandingDTO findById(Long id) {
        Landing platform = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Landing", id));
        return LandingDTO.from(platform);
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
    public Page<LandingDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<Landing> page = repository.findAll(getSpecification(request), pageable);
        return page.map(LandingDTO::from);
    }

    // UPDATE
    public LandingDTO update(Long id, Filter filter) {
        Landing platform = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Landing", id));
        mapper.map(filter, platform);
        return LandingDTO.from(repository.save(platform));
    }

    /**
     * ============================================================================================================
     **/
    private Specification<Landing> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
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
        private String nome;
        private String cognome;
        private String telefono;
        private String email;
        private String citta;
        private String via;
        private String civico;
        private String cap;
        private String provincia;
        private Boolean privacy1;
        private Boolean privacy2;
        private Boolean privacy3;
        private String ip;
        private String referral;
        private String order;
        private String transaction;
        private Long campagna;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private String nome;
        private String cognome;
        private String telefono;
        private String email;
        private String citta;
        private String via;
        private String civico;
        private String cap;
        private String provincia;
        private Boolean privacy1;
        private Boolean privacy2;
        private Boolean privacy3;
        private String ip;
        private String referral;
        private String order;
        private String transaction;
        private Long campagna;
    }
}