package it.cleverad.engine.persistence.repository;

import it.cleverad.engine.persistence.model.CampaignCookie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CampaignCookieRepository extends JpaRepository<CampaignCookie, Long>, JpaSpecificationExecutor<CampaignCookie> {
}
