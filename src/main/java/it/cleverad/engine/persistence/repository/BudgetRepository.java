package it.cleverad.engine.persistence.repository;

import it.cleverad.engine.persistence.model.Affiliate;
import it.cleverad.engine.persistence.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BudgetRepository extends JpaRepository<Budget, Long>, JpaSpecificationExecutor<Budget> {
}
