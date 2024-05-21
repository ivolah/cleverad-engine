package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.FileFeed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FileFeedRepository extends JpaRepository<FileFeed, Long>, JpaSpecificationExecutor<FileFeed> {
}