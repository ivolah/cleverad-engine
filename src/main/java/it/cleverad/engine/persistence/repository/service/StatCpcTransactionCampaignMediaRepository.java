package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.StatCpcTransactionCampaignMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StatCpcTransactionCampaignMediaRepository extends JpaRepository<StatCpcTransactionCampaignMedia, Long>, JpaSpecificationExecutor<StatCpcTransactionCampaignMedia> {


}
