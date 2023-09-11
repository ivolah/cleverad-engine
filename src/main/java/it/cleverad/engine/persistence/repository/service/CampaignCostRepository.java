package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.CampaignCost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CampaignCostRepository extends JpaRepository<CampaignCost, Long>, JpaSpecificationExecutor<CampaignCost> {

}