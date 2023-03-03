package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.Channel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChannelRepository extends JpaRepository<Channel, Long>, JpaSpecificationExecutor<Channel> {

    Page<Channel> findByAffiliateId(Long affiliateID, Pageable pageableRequest);

    Page<Channel> findByAffiliateIdAndStatus(Long affiliateID, Boolean status, Pageable pageableRequest);
}
