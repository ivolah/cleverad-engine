package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.TransactionCPC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TransactionCPCRepository extends JpaRepository<TransactionCPC, Long>, JpaSpecificationExecutor<TransactionCPC> {


}
