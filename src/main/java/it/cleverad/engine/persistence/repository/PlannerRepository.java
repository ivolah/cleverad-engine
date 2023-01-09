package it.cleverad.engine.persistence.repository;

import it.cleverad.engine.persistence.model.Planner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PlannerRepository extends JpaRepository<Planner, Long>, JpaSpecificationExecutor<Planner> {


}
