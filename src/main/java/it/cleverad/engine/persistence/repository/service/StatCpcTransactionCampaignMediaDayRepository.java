package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.StatCpcTransactionCampaignMediaDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StatCpcTransactionCampaignMediaDayRepository extends JpaRepository<StatCpcTransactionCampaignMediaDay, Long>, JpaSpecificationExecutor<StatCpcTransactionCampaignMediaDay> {
}
