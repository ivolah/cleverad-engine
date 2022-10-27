package it.cleverad.engine.persistence.repository;

import it.cleverad.engine.persistence.model.TransactionCPC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TransactionCPCRepository extends JpaRepository<TransactionCPC, Long>, JpaSpecificationExecutor<TransactionCPC> {


}

