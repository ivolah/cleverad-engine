package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.BotData;
import it.cleverad.engine.persistence.model.service.Target;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BotDataRepository extends JpaRepository<BotData, Long>, JpaSpecificationExecutor<BotData> {

}