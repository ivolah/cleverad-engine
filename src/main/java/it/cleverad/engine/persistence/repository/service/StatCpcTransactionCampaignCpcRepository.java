package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.StatCpcTransactionCampaignDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StatCpcTransactionCampaignCpcRepository extends JpaRepository<StatCpcTransactionCampaignDay, Long>, JpaSpecificationExecutor<StatCpcTransactionCampaignDay> {
}
