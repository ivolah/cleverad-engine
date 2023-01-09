package it.cleverad.engine.persistence.repository;

import it.cleverad.engine.persistence.model.StatCplValueCampaignWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StatCplValueCampaignWeekRepository extends JpaRepository<StatCplValueCampaignWeek, Long>, JpaSpecificationExecutor<StatCplValueCampaignWeek> {


}
