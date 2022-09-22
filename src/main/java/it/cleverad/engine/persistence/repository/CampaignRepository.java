package it.cleverad.engine.persistence.repository;

import it.cleverad.engine.persistence.model.Campaign;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface CampaignRepository extends JpaRepository<Campaign, Long>, JpaSpecificationExecutor<Campaign> {

    @Query(nativeQuery = true,
            value = " select cam.* from t_campaign cam " +
                    " join t_affiliate_campaign tac on cam.id = tac.campaign_id " +
                    " where tac.affiliate_id = ?1 ")
    Page<Campaign> findAffiliateCampaigns(Long affiliateId, Pageable pageable);

}
