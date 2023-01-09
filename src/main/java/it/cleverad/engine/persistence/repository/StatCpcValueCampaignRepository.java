package it.cleverad.engine.persistence.repository;

import it.cleverad.engine.persistence.model.StatCpcValueCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StatCpcValueCampaignRepository extends JpaRepository<StatCpcValueCampaign, Long>, JpaSpecificationExecutor<StatCpcValueCampaign> {


}
