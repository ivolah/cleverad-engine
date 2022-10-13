package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.Affiliate;
import it.cleverad.engine.persistence.model.AffiliateBudgetCampaign;
import it.cleverad.engine.persistence.model.Campaign;
import it.cleverad.engine.persistence.repository.AffiliateBudgetCampaignRepository;
import it.cleverad.engine.service.JwtUserDetailsService;
import it.cleverad.engine.web.dto.AffiliateBudgetCampaignDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import it.cleverad.engine.web.exception.PostgresCleveradException;
import it.cleverad.engine.web.exception.PostgresDeleteCleveradException;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional
public class AffiliateBudgetCampaignBusiness {

    @Autowired
    private AffiliateBudgetCampaignRepository repository;

    @Autowired
    private Mapper mapper;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public AffiliateBudgetCampaignDTO create(BaseCreateRequest request) {
        AffiliateBudgetCampaignDTO dto = null;
        try {
            AffiliateBudgetCampaign map = mapper.map(request, AffiliateBudgetCampaign.class);
            map.setCreationDate(LocalDateTime.now());
            map.setLastModificationDate(LocalDateTime.now());

            Affiliate affiliate = new Affiliate();
            affiliate.setId(request.getAffiliateId());
            map.setAffiliate(affiliate);

            Campaign campaign = new Campaign();
            campaign.setId(request.getCampaignId());
            map.setCampaign(campaign);
            map = repository.save(map);

            dto = AffiliateBudgetCampaignDTO.from(map);
        } catch (Exception exception) {
            throw new PostgresCleveradException("Eccezione nella creazione : " + exception.getLocalizedMessage());
        }
        return dto;

    }

    // DELETE BY ID
    public void delete(Long id) {

        try {
            repository.deleteById(id);
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    // GET BY ID
    public AffiliateBudgetCampaignDTO findById(Long id) {
        AffiliateBudgetCampaign affiliateBudgetCampaign = repository.findById(id).orElseThrow(() -> new ElementCleveradException(id));
        return AffiliateBudgetCampaignDTO.from(affiliateBudgetCampaign);
    }

    public Page<AffiliateBudgetCampaignDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<AffiliateBudgetCampaign> page = repository.findAll(getSpecification(request), pageable);
        return page.map(AffiliateBudgetCampaignDTO::from);
    }

    /**
     * ============================================================================================================
     **/
    private Specification<AffiliateBudgetCampaign> getSpecification(AffiliateBudgetCampaignBusiness.Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getAffiliateId() != null) {
                predicates.add(cb.equal(root.get("affiliate").get("id"), request.getAffiliateId()));
            }
            if (request.getCampaignId() != null) {
                predicates.add(cb.equal(root.get("campaign").get("id"), request.getCampaignId()));
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
        private Long affiliateId;
        private Long budgetId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private Long campaignId;
        private Long affiliateId;
        private Long budgetId;
    }

}
