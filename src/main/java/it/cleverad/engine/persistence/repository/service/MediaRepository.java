package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.Media;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface MediaRepository extends JpaRepository<Media, Long>, JpaSpecificationExecutor<Media> {

    @Query(nativeQuery = true, value = "SELECT * from t_media tm left join t_media_campaign tmc on tm.id = tmc.media_id where tmc.campaign_id = ?1 ")
    Page<Media> findMediaCampaxigns(Long campaignId, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT tm.*, tmc from t_media tm left join t_media_campaign tmc on tm.id = tmc.media_id ")
    Page<Media> findAllWithCampaign(Specification<Media> specification, Pageable pageable);

    // List<Media> findMediasByCampaingsId(Long campaignId);
    Page<Media> findByIdIn(Collection<Long> states, Pageable pageable);

    Media findByIdFile(String Id);

}
