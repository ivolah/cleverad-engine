package it.cleverad.engine.persistence.repository;

import it.cleverad.engine.persistence.model.Representative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RepresentativeRepository extends JpaRepository<Representative, Long>, JpaSpecificationExecutor<Representative> {


}
