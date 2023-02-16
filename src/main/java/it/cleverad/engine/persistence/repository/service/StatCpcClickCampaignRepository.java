package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.StatCpcClickCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StatCpcClickCampaignRepository extends JpaRepository<StatCpcClickCampaign, Long>, JpaSpecificationExecutor<StatCpcClickCampaign> {


}
