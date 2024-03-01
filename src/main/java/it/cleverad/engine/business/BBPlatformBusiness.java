package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.persistence.model.service.BBPlatform;
import it.cleverad.engine.persistence.repository.service.AffiliateRepository;
import it.cleverad.engine.persistence.repository.service.BBPlatformRepository;
import it.cleverad.engine.persistence.repository.service.DictionaryRepository;
import it.cleverad.engine.web.dto.BBPlatformDTO;
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
public class BBPlatformBusiness {

    @Autowired
    private BBPlatformRepository repository;
    @Autowired
    private DictionaryRepository dictionaryRepository;
    @Autowired
    private AffiliateRepository affiliateRepository;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;  @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public BBPlatformDTO create(BaseCreateRequest request) {
        BBPlatform map = mapper.map(request, BBPlatform.class);
        if (!jwtUserDetailsService.getRole().equals("Admin")) {
            map.setAffiliate(affiliateRepository.findById(jwtUserDetailsService.getAffiliateID()).orElseThrow(() -> new ElementCleveradException("Affilaite", jwtUserDetailsService.getAffiliateID())));
        }else{
            map.setAffiliate(affiliateRepository.findById(request.getBrandbuddiesId()).orElseThrow(() -> new ElementCleveradException("Affilaite", request.getBrandbuddiesId())));
        }
        map.setDictionary(dictionaryRepository.findById(request.platformId).orElseThrow(() -> new ElementCleveradException("Dictionary", request.platformId)));
        return BBPlatformDTO.from(repository.save(map));
    }

    // GET BY ID
    public BBPlatformDTO findById(Long id) {
        BBPlatform platform = repository.findById(id).orElseThrow(() -> new ElementCleveradException("BBPlatform", id));
        return BBPlatformDTO.from(platform);
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
    public Page<BBPlatformDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<BBPlatform> page = repository.findAll(getSpecification(request), pageable);
        return page.map(BBPlatformDTO::from);
    }

    // UPDATE
    public BBPlatformDTO update(Long id, Filter filter) {
        BBPlatform platform = repository.findById(id).orElseThrow(() -> new ElementCleveradException("BBPlatform", id));
        mapper.map(filter, platform);
        platform.setId(id);
        platform.setAffiliate(affiliateRepository.findById(filter.getBrandbuddiesId()).orElseThrow(() -> new ElementCleveradException("Affilaite", filter.getBrandbuddiesId())));
        platform.setDictionary(dictionaryRepository.findById(filter.platformId).orElseThrow(() -> new ElementCleveradException("Dictionary", filter.platformId)));
        return BBPlatformDTO.from(repository.save(platform));
    }

    /**
     * ============================================================================================================
     **/
    private Specification<BBPlatform> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getName() != null) {
                predicates.add(cb.equal(root.get("name"), request.getName()));
            }
            if (request.getVerified() != null) {
                predicates.add(cb.equal(root.get("verified"), request.getVerified()));
            }
            if (request.getPlatformId() != null) {
                predicates.add(cb.equal(root.get("dictionary").get("id"), request.getPlatformId()));
            }
            if (request.getBrandbuddiesId() != null) {
                predicates.add(cb.equal(root.get("affiliate").get("id"), request.getBrandbuddiesId()));
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
        private String dimension;
        private Boolean verified;
        private Long platformId;
        private Long brandbuddiesId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private String name;
        private String dimension;
        private Boolean verified;
        private Long platformId;
        private Long brandbuddiesId;
    }
}