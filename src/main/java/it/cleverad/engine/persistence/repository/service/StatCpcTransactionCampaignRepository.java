package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.StatCpcTransactionCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StatCpcTransactionCampaignRepository extends JpaRepository<StatCpcTransactionCampaign, Long>, JpaSpecificationExecutor<StatCpcTransactionCampaign> {


}
