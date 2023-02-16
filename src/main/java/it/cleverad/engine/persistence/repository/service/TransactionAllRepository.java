package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.TransactionAll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TransactionAllRepository extends JpaRepository<TransactionAll, Long>, JpaSpecificationExecutor<TransactionAll> {


}
