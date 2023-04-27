package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.StatCampaignDayCpc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface StatCampaignDayCpcRepository extends JpaRepository<StatCampaignDayCpc, Long>, JpaSpecificationExecutor<StatCampaignDayCpc> {

}
