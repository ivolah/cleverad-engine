package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.*;
import it.cleverad.engine.persistence.repository.service.*;
import it.cleverad.engine.web.dto.AffiliateChannelCommissionCampaignDTO;
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
    private AffiliateRepository affiliateRepository;
    @Autowired
    private CampaignRepository campaignRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private CommissionRepository commissionRepository;

    @Autowired
    private CampaignAffiliateBusiness campaignAffiliateBusiness;

    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public AffiliateChannelCommissionCampaignDTO create(BaseCreateRequest request) {

        AffiliateChannelCommissionCampaign map = mapper.map(request, AffiliateChannelCommissionCampaign.class);
        map.setCreationDate(LocalDateTime.now());
        map.setLastModificationDate(LocalDateTime.now());

        Affiliate affiliate = affiliateRepository.findById(request.getAffiliateId()).orElseThrow(() -> new ElementCleveradException("Affilirte", request.getAffiliateId()));
        map.setAffiliate(affiliate);
        Channel channel = channelRepository.findById(request.getChannelId()).orElseThrow(() -> new ElementCleveradException("Channel", request.getChannelId()));
        map.setChannel(channel);
        Commission commission = commissionRepository.findById(request.getCommissionId()).orElseThrow(() -> new ElementCleveradException("Commission", request.getCommissionId()));
        map.setCommission(commission);
        Campaign campaign = campaignRepository.findById(request.getCampaignId()).orElseThrow(() -> new ElementCleveradException("Campaign", request.getCampaignId()));
        map.setCampaign(campaign);

        campaignAffiliateBusiness.create(new CampaignAffiliateBusiness.BaseCreateRequest(campaign.getId(), affiliate.getId(), null, null));

        return AffiliateChannelCommissionCampaignDTO.from(repository.save(map));
    }

    // DELETE BY ID
    public void delete(Long id) {
        try {
            AffiliateChannelCommissionCampaign affiliateChannelCommissionCampaign = repository.findById(id).orElseThrow(() -> new ElementCleveradException("AffiliateChannelCommissionCampaign", id));
            repository.deleteById(affiliateChannelCommissionCampaign.getId());
            //campaignAffiliateBusiness.deleteByCampaignIdAndAffiliateId(affiliateChannelCommissionCampaign.getCampaign().getId(), affiliateChannelCommissionCampaign.getAffiliate().getId());
        } catch (ConstraintViolationException ex) {
            throw ex;
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    public void deletebyCampaignAndCommission(Long campaignId, Long commissionId) {
        try {
            Filter request = new Filter();
            request.setCampaignId(campaignId);
            request.setCommissionId(commissionId);
            Pageable pageable = PageRequest.of(0, 1000, Sort.by(Sort.Order.asc("id")));
            Page<AffiliateChannelCommissionCampaign> page = repository.findAll(getSpecification(request), pageable);
            page.stream().spliterator().forEachRemaining(affiliateChannelCommissionCampaign -> repository.deleteById(affiliateChannelCommissionCampaign.getId()));
        } catch (ConstraintViolationException ex) {
            throw ex;
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    // GET BY ID
    public AffiliateChannelCommissionCampaignDTO findById(Long id) {
        AffiliateChannelCommissionCampaign affiliateChannelCommissionCampaign = repository.findById(id).orElseThrow(() -> new ElementCleveradException("AffiliateChannelCommissionCampaign", id));
        return AffiliateChannelCommissionCampaignDTO.from(affiliateChannelCommissionCampaign);
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

    public Page<AffiliateChannelCommissionCampaignDTO> searchByCampaignIdAffiliate(Long campaignId, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Filter filter = new Filter();
        filter.setCampaignId(campaignId);
        Page<AffiliateChannelCommissionCampaign> page = repository.findAll(getSpecification(filter), pageable);
        return page.map(AffiliateChannelCommissionCampaignDTO::from);
    }


    // SEARCH searchScheduledActivities
    public AffiliateChannelCommissionCampaign searchScheduledActivities(Filter request) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("id")));
        Page<AffiliateChannelCommissionCampaign> page = repository.findAll(getSpecification(request), pageable);

        if (page.getTotalPages() > 0) {
            return page.getContent().get(0);
        } else return null;
    }

    public Page<AffiliateChannelCommissionCampaignDTO> search(Filter request, Pageable pageableR) {
        Pageable pageable = PageRequest.of(pageableR.getPageNumber(), pageableR.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<AffiliateChannelCommissionCampaign> page = repository.findAll(getSpecification(request), pageable);
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
                predicates.add(cb.equal(root.get("affiliate").get("id"), request.getAffiliateId()));
            }
            if (request.getCampaignId() != null) {
                predicates.add(cb.equal(root.get("campaign").get("id"), request.getCampaignId()));
            }
            if (request.getChannelId() != null) {
                predicates.add(cb.equal(root.get("channel").get("id"), request.getChannelId()));
            }
            if (request.getCommissionId() != null) {
                predicates.add(cb.equal(root.get("commission").get("id"), request.getCommissionId()));
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
