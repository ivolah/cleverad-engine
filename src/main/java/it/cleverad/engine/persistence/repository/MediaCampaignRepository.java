package it.cleverad.engine.persistence.repository;

import it.cleverad.engine.persistence.model.Campaign;
import it.cleverad.engine.persistence.model.MediaCampaign;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface MediaCampaignRepository extends JpaRepository<MediaCampaign, Long>, JpaSpecificationExecutor<MediaCampaign> {

}

