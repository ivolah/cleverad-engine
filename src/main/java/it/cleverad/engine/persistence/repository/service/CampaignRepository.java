package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CampaignRepository extends JpaRepository<Campaign, Long>, JpaSpecificationExecutor<Campaign> {

}
