package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.StatCpcTransactionCampaignWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StatCpcTransactionCampaignWeekRepository extends JpaRepository<StatCpcTransactionCampaignWeek, Long>, JpaSpecificationExecutor<StatCpcTransactionCampaignWeek> {


}
