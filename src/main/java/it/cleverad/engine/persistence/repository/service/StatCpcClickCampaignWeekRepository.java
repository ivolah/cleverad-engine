package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.StatCpcClickCampaignWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StatCpcClickCampaignWeekRepository extends JpaRepository<StatCpcClickCampaignWeek, Long>, JpaSpecificationExecutor<StatCpcClickCampaignWeek> {


}
