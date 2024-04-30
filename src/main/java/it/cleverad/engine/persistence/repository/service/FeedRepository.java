package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.Feed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FeedRepository extends JpaRepository<Feed, Long>, JpaSpecificationExecutor<Feed> {

}