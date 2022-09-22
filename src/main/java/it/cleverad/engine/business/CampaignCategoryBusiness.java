package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.Campaign;
import it.cleverad.engine.persistence.model.CampaignCategory;
import it.cleverad.engine.persistence.model.Category;
import it.cleverad.engine.persistence.repository.CampaignCategoryRepository;
import it.cleverad.engine.web.dto.CampaignCategoryDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;

@Slf4j
@Component
@Transactional
public class CampaignCategoryBusiness {

    @Autowired
    private CampaignCategoryRepository repository;

    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public CampaignCategoryDTO create(BaseCreateRequest request) {
        CampaignCategory map = mapper.map(request, CampaignCategory.class);
        map.setCreationDate(LocalDateTime.now());
        map.setLastModificationDate(LocalDateTime.now());

        Category cat= new Category();
        cat.setId(request.getCategoryId());
        map.setCategory(cat);

        Campaign campaign = new Campaign();
        campaign.setId(request.getCampaignId());
        map.setCampaign(campaign);

        return CampaignCategoryDTO.from(repository.save(map));
    }

    // GET BY ID
    public CampaignCategoryDTO findById(Long id) {
        try {
            CampaignCategory channel = repository.findById(id).orElseThrow(Exception::new);
            return  CampaignCategoryDTO.from(channel);
        } catch (Exception e) {
            log.error("Errore in findById", e);
            return null;
        }
    }

    // DELETE BY ID
    public void delete(Long id) {
        repository.deleteById(id);
    }

    // SEARCH PAGINATED
    public Page<CampaignCategoryDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<CampaignCategory> page = repository.findAll(getSpecification(request), pageable);

        return page.map(CampaignCategoryDTO::from);
    }

    // UPDATE
    public CampaignCategoryDTO update(Long id, Filter filter) {
        try {
            CampaignCategory channel = repository.findById(id).orElseThrow(Exception::new);
            CampaignCategoryDTO campaignDTOfrom = CampaignCategoryDTO.from(channel);

            mapper.map(filter,campaignDTOfrom );

            CampaignCategory mappedEntity = mapper.map(channel, CampaignCategory.class);
            mapper.map(campaignDTOfrom, mappedEntity);

            return CampaignCategoryDTO.from(repository.save(mappedEntity));
        } catch (Exception e) {
            log.error("Errore in update", e);
            return null;
        }
    }

    /**
     * ============================================================================================================
     **/

    private Specification<CampaignCategory> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }

            if (request.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("categoryId"), request.getCategoryId()));
            }
            if (request.getCampaignId() != null) {
                predicates.add(cb.equal(root.get("campaignId"), request.getCampaignId()));
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

        private Long campaignId;
        private Long categoryId;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;

        private Long campaignId;
        private Long categoryId;

        private Instant creationDateFrom;
        private Instant creationDateTo;
        private Instant lastModificationDateFrom;
        private Instant lastModificationDateTo;
    }

}