package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.Commission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CommissionRepository extends JpaRepository<Commission, Long>, JpaSpecificationExecutor<Commission> {

    Commission findFirstByActionAndStatus(String action, Boolean status);

}