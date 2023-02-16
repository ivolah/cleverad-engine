package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.StatCpcValueCampaignWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StatCpcValueCampaignWeekRepository extends JpaRepository<StatCpcValueCampaignWeek, Long>, JpaSpecificationExecutor<StatCpcValueCampaignWeek> {


}
