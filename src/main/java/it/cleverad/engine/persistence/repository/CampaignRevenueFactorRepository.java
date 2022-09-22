package it.cleverad.engine.persistence.repository;

import it.cleverad.engine.persistence.model.CampaignRevenueFactor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CampaignRevenueFactorRepository extends JpaRepository<CampaignRevenueFactor, Long>, JpaSpecificationExecutor<CampaignRevenueFactor> {


}

