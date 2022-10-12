package it.cleverad.engine.persistence.repository;

import it.cleverad.engine.persistence.model.Tracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TrackingRepository extends JpaRepository<Tracking, Long>, JpaSpecificationExecutor<Tracking> {


}

