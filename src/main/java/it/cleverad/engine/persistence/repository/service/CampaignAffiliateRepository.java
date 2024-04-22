package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.CampaignAffiliate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CampaignAffiliateRepository extends JpaRepository<CampaignAffiliate, Long>, JpaSpecificationExecutor<CampaignAffiliate> {

    List<CampaignAffiliate> findByAffiliateId(Long affiliateId);

    List<CampaignAffiliate> findByAffiliateIdAndCampaignId(Long affiliateId, Long campaignId);

    List<CampaignAffiliate> findByCampaignId(Long campaignId);

}