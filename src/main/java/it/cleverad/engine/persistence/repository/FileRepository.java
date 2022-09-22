package it.cleverad.engine.persistence.repository;

import it.cleverad.engine.persistence.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FileRepository extends JpaRepository<File, Long>, JpaSpecificationExecutor<File> {


}

