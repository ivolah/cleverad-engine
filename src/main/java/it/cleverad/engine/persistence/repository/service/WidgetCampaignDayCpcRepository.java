package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.WidgetCampaignDayCpc;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface WidgetCampaignDayCpcRepository extends JpaRepository<WidgetCampaignDayCpc, Long>, JpaSpecificationExecutor<WidgetCampaignDayCpc> {

}