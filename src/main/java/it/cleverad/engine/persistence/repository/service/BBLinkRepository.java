package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.BBLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BBLinkRepository extends JpaRepository<BBLink, Long>, JpaSpecificationExecutor<BBLink> {


}