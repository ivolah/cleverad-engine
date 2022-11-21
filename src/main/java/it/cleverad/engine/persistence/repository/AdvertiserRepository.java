package it.cleverad.engine.persistence.repository;

import it.cleverad.engine.persistence.model.Advertiser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AdvertiserRepository extends JpaRepository<Advertiser, Long>, JpaSpecificationExecutor<Advertiser> {
}
