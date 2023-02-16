package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.ChannelCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChannelCategoryRepository extends JpaRepository<ChannelCategory, Long>, JpaSpecificationExecutor<ChannelCategory> {
}
