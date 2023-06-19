package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.CampaignAffiliateRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CampaignAffiliateRequestRepository extends JpaRepository<CampaignAffiliateRequest, Long>, JpaSpecificationExecutor<CampaignAffiliateRequest> {

}
