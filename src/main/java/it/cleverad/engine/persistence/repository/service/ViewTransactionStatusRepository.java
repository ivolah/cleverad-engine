package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.ReportTopAffiliates;
import it.cleverad.engine.persistence.model.service.ReportTopCampaings;
import it.cleverad.engine.persistence.model.service.ViewTransactionAll;
import it.cleverad.engine.persistence.model.service.ViewTransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ViewTransactionStatusRepository extends JpaRepository<ViewTransactionStatus, Long>, JpaSpecificationExecutor<ViewTransactionStatus> {

}
