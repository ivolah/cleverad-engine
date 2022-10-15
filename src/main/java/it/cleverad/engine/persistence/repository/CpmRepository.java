package it.cleverad.engine.persistence.repository;

import it.cleverad.engine.persistence.model.Cpm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CpmRepository extends JpaRepository<Cpm, Long>, JpaSpecificationExecutor<Cpm> {


}

