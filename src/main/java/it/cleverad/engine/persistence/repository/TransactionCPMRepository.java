package it.cleverad.engine.persistence.repository;

import it.cleverad.engine.persistence.model.TransactionCPM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TransactionCPMRepository extends JpaRepository<TransactionCPM, Long>, JpaSpecificationExecutor<TransactionCPM> {


}

