package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.WidgetCampaignDayCpm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WidgetCampaignDayCpmRepository extends JpaRepository<WidgetCampaignDayCpm, Long>, JpaSpecificationExecutor<WidgetCampaignDayCpm> {



}