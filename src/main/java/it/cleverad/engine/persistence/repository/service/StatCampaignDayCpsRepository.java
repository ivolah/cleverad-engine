package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.StatCampaignDayCps;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StatCampaignDayCpsRepository extends JpaRepository<StatCampaignDayCps, Long>, JpaSpecificationExecutor<StatCampaignDayCps> {


}
