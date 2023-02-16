package it.cleverad.engine.persistence.repository.tracking;

import it.cleverad.engine.persistence.model.tracking.Cpm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CpmRepository extends JpaRepository<Cpm, Long>, JpaSpecificationExecutor<Cpm> {


}

