package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.FileAffiliate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FileAffiliateRepository extends JpaRepository<FileAffiliate, Long>, JpaSpecificationExecutor<FileAffiliate> {


}
