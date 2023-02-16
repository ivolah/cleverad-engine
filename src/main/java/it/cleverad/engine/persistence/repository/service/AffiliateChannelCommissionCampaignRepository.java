package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.AffiliateChannelCommissionCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface AffiliateChannelCommissionCampaignRepository extends JpaRepository<AffiliateChannelCommissionCampaign, Long>, JpaSpecificationExecutor<AffiliateChannelCommissionCampaign> {
    List<AffiliateChannelCommissionCampaign> findByAffiliateIdAndChannelIdAndCampaignId(Long affiliateId, Long channelId, Long campaignId);
}
