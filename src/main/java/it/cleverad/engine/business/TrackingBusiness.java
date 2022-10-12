package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.Tracking;
import it.cleverad.engine.persistence.repository.TrackingRepository;
import it.cleverad.engine.web.dto.MediaCampaignDTO;
import it.cleverad.engine.web.dto.MediaDTO;
import it.cleverad.engine.web.dto.TargetDTO;
import it.cleverad.engine.web.dto.TrackingDTO;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Slf4j
@Component
@Transactional
public class TrackingBusiness {

    @Autowired
    private TrackingRepository repository;

    @Autowired
    private Mapper mapper;

    @Autowired
    private MediaBusiness mediaBusiness;
    @Autowired
    private MediaCampaignBusiness mediaCampaignBusiness;
    @Autowired
    private CampaignBusiness campaignBusiness;

    /**
     * ============================================================================================================
     */

    // GET BY ID
    public TargetDTO getTarget(BaseCreateRequest request) {
        try {
            String refferalID = request.getRefferalId();
            byte[] decoder = Base64.getDecoder().decode(refferalID);
            String str = new String(decoder);
            TargetDTO targetDTO = new TargetDTO();
            log.info("REFFERAL :: {}", str);

            //  String[] tokens = str.split(Pattern.quote("||"));
            String[] tokens = str.split("\\|\\|");
            String mediaID = tokens[1];
            MediaDTO mediaDTO = mediaBusiness.findById(Long.valueOf(mediaID));
            targetDTO.setTarget(mediaDTO.getTarget());

            MediaCampaignDTO mcb = mediaCampaignBusiness.findByIdMedia(mediaDTO.getId());
            if (mcb != null) {
                Long cID = mcb.getCampaignId();
                if (cID != null) {
                    if (campaignBusiness.findById(cID).getCookies() != null && campaignBusiness.findById(cID).getCookies().size() > 0) {
                        targetDTO.setCookieTime(campaignBusiness.findById(cID).getCookies().get(0).getValue());
                    }
                }
            } else {
                targetDTO.setCookieTime("60");
            }

            return targetDTO;
        } catch (Exception e) {
            log.error("Errore in getTarget", e.getMessage());
            return new TargetDTO();
        }
    }

    // CREATE
    public TrackingDTO create(BaseCreateRequest request) {
        Tracking map = mapper.map(request, Tracking.class);
        map.setCreationDate(LocalDateTime.now());
        map.setRead(false);
        return TrackingDTO.from(repository.save(map));
    }

    // GET BY ID
    public TrackingDTO findById(Long id) {
        try {
            Tracking media = repository.findById(id).orElseThrow(Exception::new);
            return TrackingDTO.from(media);
        } catch (Exception e) {
            log.error("Errore in findById", e);
            return null;
        }
    }

    // DELETE BY ID
    public void delete(Long id) {
        repository.deleteById(id);
    }

    // UPDATE
    public TrackingDTO update(Long id, Filter filter) {
        try {
            Tracking media = repository.findById(id).orElseThrow(Exception::new);
            TrackingDTO mediaDTOfrom = TrackingDTO.from(media);
            mapper.map(filter, mediaDTOfrom);

            Tracking mappedEntity = mapper.map(media, Tracking.class);
            mapper.map(mediaDTOfrom, mappedEntity);
            return TrackingDTO.from(repository.save(mappedEntity));
        } catch (Exception e) {
            log.error("Errore in update", e);
            return null;
        }
    }

    public void setRead(long id) {

        Tracking media = repository.findById(id).get();
        media.setRead(true);
        repository.save(media);

    }


    // SEARCH PAGINATED
    public Page<TrackingDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<Tracking> page = repository.findAll(getSpecification(request), pageable);
        return page.map(TrackingDTO::from);
    }

    public Page<TrackingDTO> getUnread() {
        Pageable pageable = PageRequest.of(0, 1000, Sort.by(Sort.Order.asc("id")));
        Filter request = new Filter();
        request.setRead(false);
        Page<Tracking> page = repository.findAll(getSpecification(request), pageable);
        log.info("UNREAD {}", page.getTotalElements());
        return page.map(TrackingDTO::from);
    }


    /**
     * ============================================================================================================
     **/
    private Specification<Tracking> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }

            if (request.getRead() != null) {
                predicates.add(cb.equal(root.get("read"), request.getRead()));
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
        private String refferalId;
        private String ip;
        private String agent;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private String refferalId;
        private String ip;
        private String agent;
        private Boolean read;
        private LocalDateTime creationDate;
    }

}
