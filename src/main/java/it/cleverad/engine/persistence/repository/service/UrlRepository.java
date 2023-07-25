package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.TransactionCPS;
import it.cleverad.engine.persistence.model.service.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface UrlRepository extends JpaRepository<Url, Long>, JpaSpecificationExecutor<Url> {
}