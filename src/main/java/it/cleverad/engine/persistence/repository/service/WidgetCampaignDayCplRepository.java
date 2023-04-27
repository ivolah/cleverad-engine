package it.cleverad.engine.persistence.repository.service;


import it.cleverad.engine.persistence.model.service.WidgetCampaignDayCpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WidgetCampaignDayCplRepository extends JpaRepository<WidgetCampaignDayCpl, Long>, JpaSpecificationExecutor<WidgetCampaignDayCpl> {


}
