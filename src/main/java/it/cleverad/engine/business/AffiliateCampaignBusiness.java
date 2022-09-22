package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.Affiliate;
import it.cleverad.engine.persistence.model.AffiliateChannelCommissionCampaign;
import it.cleverad.engine.persistence.model.Campaign;
import it.cleverad.engine.persistence.repository.AffiliateChannelCommissionCampaignRepository;
import it.cleverad.engine.service.JwtUserDetailsService;
import it.cleverad.engine.web.dto.AffiliateCampaignDTO;
import it.cleverad.engine.web.exception.PostgresCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class AffiliateCampaignBusiness {

    @Autowired
    private AffiliateChannelCommissionCampaignRepository repository;

    @Autowired
    private Mapper mapper;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public AffiliateCampaignDTO create(BaseCreateRequest request) {
        AffiliateCampaignDTO dto = null;
        try {
            AffiliateChannelCommissionCampaign map = mapper.map(request, AffiliateChannelCommissionCampaign.class);
            map.setCreationDate(LocalDateTime.now());
            map.setLastModificationDate(LocalDateTime.now());

            Affiliate affiliate = new Affiliate();
            affiliate.setId(request.getAffiliateId());
            map.setAffiliate(affiliate);

            Campaign campaign = new Campaign();
            campaign.setId(request.getCampaignId());
            map.setCampaign(campaign);
            map = repository.save(map);

            dto = AffiliateCampaignDTO.from(map);
        } catch (Exception exception) {
            throw new PostgresCleveradException("Eccezione nella creazione : " + exception.getLocalizedMessage());
        }
        return dto;

    }

    // DELETE BY ID
    public void delete(Long id) {
        repository.deleteById(id);
    }

    // GET BY ID
    public AffiliateCampaignDTO findById(Long id) {
        try {
            AffiliateChannelCommissionCampaign affiliateCampaign = repository.findById(id).orElseThrow(Exception::new);
            return AffiliateCampaignDTO.from(affiliateCampaign);
        } catch (Exception e) {
            log.error("Errore in findById", e);
            return null;
        }
    }

    /**
     * ============================================================================================================
     **/
    private Specification<AffiliateChannelCommissionCampaign> getSpecification(AffiliateCampaignBusiness.Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getAffiliateId() != null) {
                predicates.add(cb.equal(root.get("affiliateId"), request.getAffiliateId()));
            }
            if (request.getCampaignId() != null) {
                predicates.add(cb.equal(root.get("campaignId"), request.getCampaignId()));
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
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private Long campaignId;
        private Long affiliateId;
    }

}
