package it.cleverad.engine.persistence.repository.tracking;

import it.cleverad.engine.persistence.model.tracking.Cps;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CpsRepository extends JpaRepository<Cps, Long>, JpaSpecificationExecutor<Cps> {

}
