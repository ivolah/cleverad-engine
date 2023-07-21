package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.CampaignBudget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CampaignBudgetRepository extends JpaRepository<CampaignBudget, Long>, JpaSpecificationExecutor<CampaignBudget> {

}
