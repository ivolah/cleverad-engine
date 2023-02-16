package it.cleverad.engine.persistence.repository.tracking;

import it.cleverad.engine.persistence.model.tracking.Cpc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CpcRepository extends JpaRepository<Cpc, Long>, JpaSpecificationExecutor<Cpc> {


}

