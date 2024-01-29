package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.FileCost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FileCostRepository extends JpaRepository<FileCost, Long>, JpaSpecificationExecutor<FileCost> {


}