package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.Affiliate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AffiliateRepository extends JpaRepository<Affiliate, Long>, JpaSpecificationExecutor<Affiliate> {
}
