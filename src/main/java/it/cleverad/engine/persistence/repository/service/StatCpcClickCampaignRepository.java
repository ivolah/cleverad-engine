package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.StatCpcClickCampaignMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StatCpcClickCampaignRepository extends JpaRepository<StatCpcClickCampaignMedia, Long>, JpaSpecificationExecutor<StatCpcClickCampaignMedia> {


}
