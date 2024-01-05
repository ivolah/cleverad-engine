package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.FileCampaignBudgetOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FileCampaignBudgetOrderRepository extends JpaRepository<FileCampaignBudgetOrder, Long>, JpaSpecificationExecutor<FileCampaignBudgetOrder> {
}