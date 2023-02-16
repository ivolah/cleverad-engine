package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BudgetRepository extends JpaRepository<Budget, Long>, JpaSpecificationExecutor<Budget> {
}
