package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.Campaign;
import it.cleverad.engine.persistence.model.Media;
import it.cleverad.engine.persistence.model.MediaCampaign;
import it.cleverad.engine.persistence.repository.MediaCampaignRepository;
import it.cleverad.engine.web.dto.MediaCampaignDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import it.cleverad.engine.web.exception.PostgresCleveradException;
import it.cleverad.engine.web.exception.PostgresDeleteCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional
public class MediaCampaignBusiness {

    @Autowired
    private MediaCampaignRepository repository;

    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public MediaCampaignDTO create(MediaCampaignBusiness.BaseCreateRequest request) {
        MediaCampaignDTO dto = null;
        try {
            MediaCampaign map = mapper.map(request, MediaCampaign.class);
            Media media = new Media();
            media.setId(request.getMediaId());
            map.setMedia(media);
            Campaign camp = new Campaign();
            camp.setId(request.getCampaignId());
            map.setCampaign(camp);
            map.setCreationDate(LocalDateTime.now());
            map.setLastModificationDate(LocalDateTime.now());
            dto = MediaCampaignDTO.from(repository.save(map));
        } catch (Exception exception) {
            throw new PostgresCleveradException("Eccezione nella creazione : " + exception.getLocalizedMessage());
        }
        return dto;
    }

    // DELETE BY ID
    public void delete(Long id) {
         try {
            repository.deleteById(id);
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    // GET BY ID
    public MediaCampaignDTO findById(Long id) {
        try {
            MediaCampaign mediaCampaign = repository.findById(id).orElseThrow(() -> new ElementCleveradException(id));
            return MediaCampaignDTO.from(mediaCampaign);
        } catch (Exception e) {
            log.error("Errore in findById", e);
            return null;
        }
    }

    // GET BY ID
    public MediaCampaignDTO findByIdMedia(Long id) {
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Order.asc("id")));
        Filter request = new Filter();
        request.setMediaId(id);
        Page<MediaCampaign> page = repository.findAll(getSpecification(request), pageable);
        return MediaCampaignDTO.from(page.stream().findFirst().get());
    }

    public MediaCampaignDTO findByIdCampaign(Long id) {
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Order.asc("id")));
        Filter request = new Filter();
        request.setCampaignId(id);
        Page<MediaCampaign> page = repository.findAll(getSpecification(request), pageable);
        return MediaCampaignDTO.from(page.stream().findFirst().get());
    }

    /**
     * ============================================================================================================
     **/
    private Specification<MediaCampaign> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }

            if (request.getMediaId() != null) {
                predicates.add(cb.equal(root.get("media").get("id"), request.getMediaId()));
            }
            if (request.getCampaignId() != null) {
                predicates.add(cb.equal(root.get("campaign").get("id"), request.getCampaignId()));
            }

            if (request.getCreationDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("creationDate"), LocalDateTime.ofInstant(request.getCreationDateFrom(), ZoneOffset.UTC)));
            }
            if (request.getCreationDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("creationDate"), LocalDateTime.ofInstant(request.getCreationDateTo().plus(1, ChronoUnit.DAYS), ZoneOffset.UTC)));
            }

            if (request.getLastModificationDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("lastModificationDate"), LocalDateTime.ofInstant(request.getLastModificationDateFrom(), ZoneOffset.UTC)));
            }
            if (request.getLastModificationDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("lastModificationDate"), LocalDateTime.ofInstant(request.getLastModificationDateTo().plus(1, ChronoUnit.DAYS), ZoneOffset.UTC)));
            }

            completePredicate = cb.and(predicates.toArray(new Predicate[0]));

            return completePredicate;
        };
    }

    /**
     * ============================================================================================================
     **/

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BaseCreateRequest {
        private Long mediaId;
        private Long campaignId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private Long mediaId;
        private Long campaignId;
        private Instant creationDateFrom;
        private Instant creationDateTo;
        private Instant lastModificationDateFrom;
        private Instant lastModificationDateTo;
    }

}
