package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.WidgetCampaignDayCpc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WidgetCampaignDayCpcRepository extends JpaRepository<WidgetCampaignDayCpc, Long>, JpaSpecificationExecutor<WidgetCampaignDayCpc> {
}