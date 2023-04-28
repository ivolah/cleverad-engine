package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AgentRepository extends JpaRepository<Agent, Long>, JpaSpecificationExecutor<Agent> {

}

