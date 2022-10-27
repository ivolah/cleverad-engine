package it.cleverad.engine.persistence.repository;

import it.cleverad.engine.persistence.model.TransactionCPL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TransactionCPLRepository extends JpaRepository<TransactionCPL, Long>, JpaSpecificationExecutor<TransactionCPL> {


}

