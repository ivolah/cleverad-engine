package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.FilePayout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FilePayoutRepository extends JpaRepository<FilePayout, Long>, JpaSpecificationExecutor<FilePayout> {


}