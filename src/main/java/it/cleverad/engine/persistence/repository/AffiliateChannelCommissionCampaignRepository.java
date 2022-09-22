package it.cleverad.engine.persistence.repository;

import it.cleverad.engine.persistence.model.AffiliateChannelCommissionCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AffiliateChannelCommissionCampaignRepository extends JpaRepository<AffiliateChannelCommissionCampaign, Long>, JpaSpecificationExecutor<AffiliateChannelCommissionCampaign> {
}
