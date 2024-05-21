package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.ViewTransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ViewTransactionStatusRepository extends JpaRepository<ViewTransactionStatus, Long>, JpaSpecificationExecutor<ViewTransactionStatus> {

}