package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.WidgetCampaignDayCpm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;

public interface WidgetCampaignDayCpmRepository extends JpaRepository<WidgetCampaignDayCpm, Long>, JpaSpecificationExecutor<WidgetCampaignDayCpm> {



}
