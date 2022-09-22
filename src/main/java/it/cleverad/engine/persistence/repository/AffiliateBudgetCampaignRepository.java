package it.cleverad.engine.persistence.repository;

import it.cleverad.engine.persistence.model.AffiliateBudgetCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AffiliateBudgetCampaignRepository extends JpaRepository<AffiliateBudgetCampaign, Long>, JpaSpecificationExecutor<AffiliateBudgetCampaign> {
}
