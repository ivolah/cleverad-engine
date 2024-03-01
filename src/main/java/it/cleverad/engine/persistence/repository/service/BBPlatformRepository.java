package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.BBPlatform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BBPlatformRepository extends JpaRepository<BBPlatform, Long>, JpaSpecificationExecutor<BBPlatform> {


}