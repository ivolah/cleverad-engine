package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.AffiliateBudget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AffiliateBudgetRepository extends JpaRepository<AffiliateBudget, Long>, JpaSpecificationExecutor<AffiliateBudget> {
}