package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.*;
import it.cleverad.engine.persistence.repository.AffiliateChannelCommissionCampaignRepository;
import it.cleverad.engine.web.dto.AffiliateChannelCommissionCampaignDTO;
import it.cleverad.engine.web.exception.PostgresCleveradException;
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
public class AffiliateChannelCommissionCampaignBusiness {

    @Autowired
    private AffiliateChannelCommissionCampaignRepository repository;

    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public AffiliateChannelCommissionCampaignDTO create(BaseCreateRequest request) {
        AffiliateChannelCommissionCampaignDTO dto = null;
        try {
            AffiliateChannelCommissionCampaign map = mapper.map(request, AffiliateChannelCommissionCampaign.class);
            map.setCreationDate(LocalDateTime.now());
            map.setLastModificationDate(LocalDateTime.now());

            Affiliate affiliate = new Affiliate();
            affiliate.setId(request.getAffiliateId());
            map.setAffiliate(affiliate);

            Channel channel = new Channel();
            channel.setId(request.getChannelId());
            map.setChannel(channel);

            Commission commission = new Commission();
            commission.setId(request.getCommissionId());
            map.setCommission(commission);

            Campaign campaign = new Campaign();
            campaign.setId(request.getCampaignId());
            map.setCampaign(campaign);
            map = repository.save(map);

            dto = AffiliateChannelCommissionCampaignDTO.from(map);
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
    public AffiliateChannelCommissionCampaignDTO findById(Long id) {
        try {
            AffiliateChannelCommissionCampaign AffiliateChannelCommissionCampaign = repository.findById(id).orElseThrow(Exception::new);
            return AffiliateChannelCommissionCampaignDTO.from(AffiliateChannelCommissionCampaign);
        } catch (Exception e) {
            log.error("Errore in findById", e);
            return null;
        }
    }

    // GET BY ID CAMPAIGN
    public Page<AffiliateChannelCommissionCampaignDTO> searchByCampaignId(Long campaignId, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Filter filter = new Filter();
        filter.setCampaignId(campaignId);
        log.info(">>> " + campaignId);
        Page<AffiliateChannelCommissionCampaign> page = repository.findAll(getSpecification(filter), pageable);


        return page.map(AffiliateChannelCommissionCampaignDTO::from);
    }

    /**
     * ============================================================================================================
     **/
    private Specification<AffiliateChannelCommissionCampaign> getSpecification(AffiliateChannelCommissionCampaignBusiness.Filter request) {
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
                predicates.add(cb.equal(root.get("campaign_id"), request.getCampaignId()));
            }
            if (request.getChannelId() != null) {
                predicates.add(cb.equal(root.get("channelId"), request.getChannelId()));
            }
            if (request.getCommissionId() != null) {
                predicates.add(cb.equal(root.get("commissionId"), request.getCommissionId()));
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
        private Long channelId;
        private Long commissionId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private Long campaignId;
        private Long affiliateId;
        private Long channelId;
        private Long commissionId;
    }

}
