package it.cleverad.engine.persistence.repository;

import it.cleverad.engine.persistence.model.StatCplValueCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StatCplValueCampaignRepository extends JpaRepository<StatCplValueCampaign, Long>, JpaSpecificationExecutor<StatCplValueCampaign> {


}
