package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.*;
import it.cleverad.engine.persistence.repository.CampaignRevenueFactorRepository;
import it.cleverad.engine.web.dto.CampaignRevenueFactorDTO;
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
public class CampaignRevenueFactorBusiness {

    @Autowired
    private CampaignRevenueFactorRepository repository;

    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public CampaignRevenueFactorDTO create(BaseCreateRequest request) {
        CampaignRevenueFactor map = mapper.map(request, CampaignRevenueFactor.class);
        map.setCreationDate(LocalDateTime.now());
        map.setLastModificationDate(LocalDateTime.now());

        RevenueFactor revenueFactor = new RevenueFactor();
        revenueFactor.setId(request.getRevenuefactorId());
        map.setRevenuefactor(revenueFactor);

        Campaign campaign = new Campaign();
        campaign.setId(request.getCampaignId());
        map.setCampaign(campaign);

        map = repository.save(map);

        return CampaignRevenueFactorDTO.from(map);
    }

    // GET BY ID
    public CampaignRevenueFactorDTO findById(Long id) {
        try {
            CampaignRevenueFactor commission = repository.findById(id).orElseThrow(Exception::new);
            return CampaignRevenueFactorDTO.from(commission);
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
    public Page<CampaignRevenueFactorDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<CampaignRevenueFactor> page = repository.findAll(getSpecification(request), pageable);

        return page.map(CampaignRevenueFactorDTO::from);
    }

    // UPDATE
    public CampaignRevenueFactorDTO update(Long id, Filter filter) {
        try {
            CampaignRevenueFactor ommission = repository.findById(id).orElseThrow(Exception::new);
            CampaignRevenueFactorDTO campaignDTOfrom = CampaignRevenueFactorDTO.from(ommission);

            mapper.map(filter, campaignDTOfrom);

            CampaignRevenueFactor mappedEntity = mapper.map(ommission, CampaignRevenueFactor.class);
            mapper.map(campaignDTOfrom, mappedEntity);

            return CampaignRevenueFactorDTO.from(repository.save(mappedEntity));
        } catch (Exception e) {
            log.error("Errore in update", e);
            return null;
        }
    }

    // GET FROM CAMPAIGN ID
    public Page<CampaignRevenueFactorDTO> searchByCampaignId(Long campaignId, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Filter filter = new Filter();
        filter.setCampaignId(campaignId);
        log.info(">>> " + campaignId);
        Page<CampaignRevenueFactor> page = repository.findAll(getSpecification(filter), pageable);
        return page.map(CampaignRevenueFactorDTO::from);
    }

    /**
     * ============================================================================================================
     **/
    private Specification<CampaignRevenueFactor> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }

            if (request.getCampaignId() != null) {
                predicates.add(cb.equal(root.get("campaign"), request.getCampaignId()));
            }
            if (request.getRevenuefactorId() != null) {
                predicates.add(cb.equal(root.get("revenuefactorId"), request.getRevenuefactorId()));
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
        private Long revenuefactorId;

        private LocalDateTime creationDate;
        private LocalDateTime lastModificationDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;

        private Long campaignId;
        private Long revenuefactorId;

        private LocalDateTime creationDate;
        private LocalDateTime lastModificationDate;

        private Instant creationDateFrom;
        private Instant creationDateTo;
        private Instant lastModificationDateFrom;
        private Instant lastModificationDateTo;
    }

}

