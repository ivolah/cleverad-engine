package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.WidgetCampaignDayCps;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WidgetCampaignDayCpsRepository extends JpaRepository<WidgetCampaignDayCps, Long>, JpaSpecificationExecutor<WidgetCampaignDayCps> {


}
