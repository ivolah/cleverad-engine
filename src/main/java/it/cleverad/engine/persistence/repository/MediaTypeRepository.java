package it.cleverad.engine.persistence.repository;

import it.cleverad.engine.persistence.model.MediaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MediaTypeRepository extends JpaRepository<MediaType, Long>, JpaSpecificationExecutor<MediaType> {

}

