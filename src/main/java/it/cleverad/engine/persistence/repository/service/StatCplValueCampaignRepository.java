package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.StatCplValueCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StatCplValueCampaignRepository extends JpaRepository<StatCplValueCampaign, Long>, JpaSpecificationExecutor<StatCplValueCampaign> {


}
