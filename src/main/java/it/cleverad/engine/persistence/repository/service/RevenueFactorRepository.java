package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.RevenueFactor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RevenueFactorRepository extends JpaRepository<RevenueFactor, Long>, JpaSpecificationExecutor<RevenueFactor> {
    RevenueFactor findFirstByActionAndStatus(String action, Boolean status);
}