package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.StatCpcClickCampaignMediaDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StatCpcClickCampaignMediaDayRepository extends JpaRepository<StatCpcClickCampaignMediaDay, Long>, JpaSpecificationExecutor<StatCpcClickCampaignMediaDay> {


}
