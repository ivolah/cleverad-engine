package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.Campaign;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;

public interface CampaignRepository extends JpaRepository<Campaign, Long>, JpaSpecificationExecutor<Campaign> {

    Page<Campaign> findByCampaignCategories_CategoryId(Long idCa, Pageable page);
    List<Campaign> findByCampaignCategories_CampaignIdAndCampaignCategories_CategoryId(Long idCam, Long idCa);
    Page<Campaign> findByCampaignCategories_CampaignIdInAndCampaignCategories_CategoryId(List<Long> ids, Long idCa, Pageable pageable);

    List<Campaign> findByIdInAndCampaignCategories_CampaignId(List<Long> ids, Long id);
    List<Campaign> findByIdIn(List<Long> ids);

}