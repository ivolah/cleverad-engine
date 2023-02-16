package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.Dictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DictionaryRepository extends JpaRepository<Dictionary, Long>, JpaSpecificationExecutor<Dictionary> {

}
