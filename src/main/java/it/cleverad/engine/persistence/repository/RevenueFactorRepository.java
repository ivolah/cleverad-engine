package it.cleverad.engine.persistence.repository;

import it.cleverad.engine.persistence.model.RevenueFactor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RevenueFactorRepository extends JpaRepository<RevenueFactor, Long>, JpaSpecificationExecutor<RevenueFactor> {


}

