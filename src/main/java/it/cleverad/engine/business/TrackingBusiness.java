package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.config.model.Refferal;
import it.cleverad.engine.persistence.model.service.Campaign;
import it.cleverad.engine.persistence.model.tracking.Tracking;
import it.cleverad.engine.persistence.repository.service.CampaignRepository;
import it.cleverad.engine.persistence.repository.tracking.TrackingRepository;
import it.cleverad.engine.service.ReferralService;
import it.cleverad.engine.web.dto.AffiliateChannelCommissionCampaignDTO;
import it.cleverad.engine.web.dto.MediaDTO;
import it.cleverad.engine.web.dto.TargetDTO;
import it.cleverad.engine.web.dto.TrackingDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import it.cleverad.engine.web.exception.PostgresDeleteCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
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
import java.util.List;

@Slf4j
@Component
@Transactional
public class TrackingBusiness {
    @Autowired
    private CampaignRepository campaignRepository;
    @Autowired
    private TrackingRepository repository;
    @Autowired
    private Mapper mapper;
    @Autowired
    private MediaBusiness mediaBusiness;
    @Autowired
    private CampaignBusiness campaignBusiness;
    @Autowired
    private CampaignAffiliateBusiness campaignAffiliateBusiness;
    @Autowired
    private AffiliateChannelCommissionCampaignBusiness affiliateChannelCommissionCampaignBusiness;
    @Autowired
    private ReferralService referralService;

    /**
     * ============================================================================================================
     */

    // GET BY ID
    public TargetDTO getTarget(BaseCreateRequest request) {
        TargetDTO targetDTO = new TargetDTO();
        Refferal refferal = referralService.decodificaReferral(request.getRefferalId());
        //log.info("REQ: " + refferal);

        if (refferal != null && refferal.getMediaId() != null) {

            MediaDTO mediaDTO = null;
            if (mediaBusiness.findById(refferal.getMediaId()) != null) {
                mediaDTO = mediaBusiness.findById(refferal.getMediaId());
                //log.info(">>>>>>>> " + mediaDTO.getTarget() + " ------ " + refferal.getMediaId());
                targetDTO.setTarget(mediaDTO.getTarget());
            }

            targetDTO.setMediaId(refferal.getMediaId());

            if (mediaDTO != null) {
                Long cID = mediaDTO.getCampaignId();
                if (cID != null) {
                    targetDTO.setCookieTime(campaignBusiness.findById(cID).getCookieValue());
                }
            } else {
                targetDTO.setCookieTime("60");
            }
        }

        //log.info("TT " + targetDTO.getTarget());

        if (refferal != null && refferal.getCampaignId() != null) {

            if (refferal.getAffiliateId() != null) {
                campaignAffiliateBusiness.searchByAffiliateIdAndCampaignId(refferal.getAffiliateId(), refferal.getCampaignId()).stream().findFirst().ifPresent(campaignAffiliateDTO -> targetDTO.setFollowThorugh(campaignAffiliateDTO.getFollowThrough()));
                // CHECK BLOCKED
                if (refferal.getChannelId() != null) {

                    AffiliateChannelCommissionCampaignBusiness.Filter filter = new AffiliateChannelCommissionCampaignBusiness.Filter();
                    filter.setAffiliateId(refferal.getAffiliateId());
                    filter.setChannelId(refferal.getChannelId());
                    filter.setCampaignId(refferal.getCampaignId());
                    filter.setBlocked(true);
                    Page<AffiliateChannelCommissionCampaignDTO> dtos = affiliateChannelCommissionCampaignBusiness.search(filter, Pageable.ofSize(Integer.MAX_VALUE));
                    if (dtos.getTotalElements() > 1) {
                        log.warn(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> {} per referral {}", dtos.getTotalElements(), refferal);
                    }
                    if (dtos.getTotalElements() == 1) {
                        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> BLOCKED {}", dtos.stream().findFirst().get().getId());
                        targetDTO.setTarget("https://www.cleverad.it/");
                    }
                }
            }

            // GESTIONE CAMPAGNA SOSPESA
            Campaign campaign = campaignRepository.findById(refferal.getCampaignId()).orElse(null);
            if (campaign == null) {
                // null
            } else if (campaign.getSuspended() == null) {
                //null
            } else if (campaign.getSuspended()) {
                log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> CAMPAGNA SOSPESA {}", refferal.getCampaignId());
                targetDTO.setTarget("https://www.cleverad.it/");
                targetDTO.setFollowThorugh("https://www.cleverad.it/");
            }

            //TODO VERIFICA SE OVER CAP PER AFFILIATO O PER CAMPAGNA

            //TODO VERIFICA SE OVERBUDGET X AFFILIATO O PER CAMPAGNA

        }// refferal not null

        return targetDTO;
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
        Tracking media = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Tracking", id));
        return TrackingDTO.from(media);
    }

    // DELETE BY ID
    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (ConstraintViolationException ex) {
            throw ex;
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    // UPDATE
    public TrackingDTO update(Long id, Filter filter) {
        Tracking media = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Tracking", id));
        TrackingDTO mediaDTOfrom = TrackingDTO.from(media);
        mapper.map(filter, mediaDTOfrom);

        Tracking mappedEntity = mapper.map(media, Tracking.class);
        mapper.map(mediaDTOfrom, mappedEntity);
        return TrackingDTO.from(repository.save(mappedEntity));
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
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("id")));
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
    @ToString
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