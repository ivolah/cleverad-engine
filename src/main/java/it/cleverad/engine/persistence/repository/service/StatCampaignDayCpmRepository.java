package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.StatCampaignDayCpc;
import it.cleverad.engine.persistence.model.service.StatCampaignDayCpm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StatCampaignDayCpmRepository extends JpaRepository<StatCampaignDayCpm, Long>, JpaSpecificationExecutor<StatCampaignDayCpm> {


}
