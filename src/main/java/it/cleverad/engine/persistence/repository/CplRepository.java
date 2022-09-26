package it.cleverad.engine.persistence.repository;

import it.cleverad.engine.persistence.model.Cpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CplRepository extends JpaRepository<Cpl, Long>, JpaSpecificationExecutor<Cpl> {


}

