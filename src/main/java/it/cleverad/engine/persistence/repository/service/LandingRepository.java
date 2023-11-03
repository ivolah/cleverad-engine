package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.Landing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LandingRepository extends JpaRepository<Landing, Long>, JpaSpecificationExecutor<Landing> {
}