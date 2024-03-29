package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.FileCampaignBudgetInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FileCampaignBudgetInvoiceRepository extends JpaRepository<FileCampaignBudgetInvoice, Long>, JpaSpecificationExecutor<FileCampaignBudgetInvoice> {
}