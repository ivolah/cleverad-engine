package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.TransactionCPS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TransactionCPSRepository extends JpaRepository<TransactionCPS, Long>, JpaSpecificationExecutor<TransactionCPS> {

}
