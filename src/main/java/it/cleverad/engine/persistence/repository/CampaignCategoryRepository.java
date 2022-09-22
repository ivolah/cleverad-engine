package it.cleverad.engine.persistence.repository;

import it.cleverad.engine.persistence.model.CampaignCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CampaignCategoryRepository extends JpaRepository<CampaignCategory, Long>, JpaSpecificationExecutor<CampaignCategory> {
}
