package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.Affiliate;
import it.cleverad.engine.persistence.model.service.Campaign;
import it.cleverad.engine.persistence.model.service.Media;
import it.cleverad.engine.persistence.repository.service.AffiliateRepository;
import it.cleverad.engine.persistence.repository.service.CampaignRepository;
import it.cleverad.engine.persistence.repository.service.MediaRepository;
import it.cleverad.engine.persistence.repository.service.MediaTypeRepository;
import it.cleverad.engine.service.JwtUserDetailsService;
import it.cleverad.engine.service.RefferalService;
import it.cleverad.engine.web.dto.MediaDTO;
import it.cleverad.engine.web.dto.MediaTypeDTO;
import it.cleverad.engine.web.dto.TargetDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Component
@Transactional
public class MediaBusiness {

    @Autowired
    private MediaRepository repository;

    @Autowired
    private Mapper mapper;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private MediaTypeBusiness mediaTypeBusiness;

    @Autowired
    private MediaTypeRepository mediaTypeRepository;
    @Autowired
    private AffiliateRepository affiliateRepository;
    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private RefferalService refferalService;
    @Autowired
    private TargetBusiness targetBusiness;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public MediaDTO create(BaseCreateRequest request) {

        String bannerCode = request.getBannerCode();
        String url = request.getUrl();
        if (StringUtils.isNotBlank(url)) bannerCode.replace("{{url}}", url);

        String target = request.getTarget();
        if (StringUtils.isNotBlank(target)) bannerCode.replace("{{target}}", target);

        request.setBannerCode(bannerCode);

        Media map = mapper.map(request, Media.class);
        map.setMediaType(mediaTypeRepository.findById(request.typeId).orElseThrow(() -> new ElementCleveradException("Media Type", request.typeId)));
        map.setStatus(true);
        Media saved = repository.save(map);
        Campaign cc = campaignRepository.findById(request.campaignId).orElseThrow(() -> new ElementCleveradException("Campaign", request.getCampaignId()));
        cc.addMedia(saved);
        campaignRepository.save(cc);

        return MediaDTO.from(saved);
    }

    // GET BY ID
    public MediaDTO findById(Long id) {
        Media media = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Media", id));
        MediaDTO dto = MediaDTO.from(media);
        if (dto.getTypeId() != null) {
            MediaTypeDTO mtDto = mediaTypeBusiness.findById(dto.getTypeId());
            dto.setTypeName(mtDto.getName());
            dto.setBannerCode(generaBannerCode(dto, media.getId(), id, 0L, 0L));
        }
        return dto;
    }

    // DELETE BY ID
    public void delete(Long id) {
        Media media = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Media", id));
        try {
            if (media.getCampaigns() != null) {
                media.getCampaigns().forEach(campaign -> {
                    campaign.removeMedia(id);
                });
            }
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            this.disable(id);
            log.error("Eccezione gestita nella cancellazione, disabilito media " + id, ex);
        }
    }

    // UPDATE
    public MediaDTO update(Long id, Filter filter) {
        Media media = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Media", id));
        MediaDTO mediaDTOfrom = MediaDTO.from(media);
        mapper.map(filter, mediaDTOfrom);

        Media mappedEntity = mapper.map(media, Media.class);
        mapper.map(mediaDTOfrom, mappedEntity);
        mappedEntity.setLastModificationDate(LocalDateTime.now());

        String bannerCode = mappedEntity.getBannerCode();
        String url = mappedEntity.getUrl();
        if (StringUtils.isNotBlank(url)) bannerCode.replace("{{url}}", url);

        String target = mappedEntity.getTarget();
        if (StringUtils.isNotBlank(target)) bannerCode.replace("{{target}}", target);


//        List<Target> targets = (List<Target>) mappedEntity.getTargets();
//        targets.stream().filter(target -> StringUtils.isNotBlank(target.getTarget())).forEach(target -> {
//            bannerCode.replace("{{target}}", target.getTarget());
//            // TODO COME GESTIRE????? NON AGGIORNIAMO ma facciamo in logica target?
//            // multipli banner code??
//        });

        mappedEntity.setBannerCode(bannerCode);
        mappedEntity.setStatus(true);
        Media saved = repository.save(mappedEntity);

        Campaign cc = campaignRepository.findById(filter.campaignId).orElseThrow(() -> new ElementCleveradException("Campaign", filter.getCampaignId()));
        cc.addMedia(saved);
        campaignRepository.save(cc);

        return MediaDTO.from(saved);
    }

    public MediaDTO enable(Long id) {
        Media media = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Media", id));
        media.setStatus(true);
        return MediaDTO.from(repository.save(media));
    }

    public MediaDTO disable(Long id) {
        Media media = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Media", id));
        media.setStatus(false);
        return MediaDTO.from(repository.save(media));
    }

    // SEARCH PAGINATED
    public Page<MediaDTO> search(Filter request, Pageable pageableRequest) {
        if (jwtUserDetailsService.getRole().equals("Admin")) {
            Page<Media> page = repository.findAll(getSpecification(request), PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id"))));
            return page.map(media -> MediaDTO.from(media));
        } else {
            Affiliate cc = affiliateRepository.findById(jwtUserDetailsService.getAffiliateID()).orElseThrow(() -> new ElementCleveradException("Affiliate", jwtUserDetailsService.getAffiliateID()));

            List<Campaign> campaigns = new ArrayList<>();
            if (cc.getCampaignAffiliates() != null) {
                campaigns = cc.getCampaignAffiliates().stream().map(campaignAffiliate -> {
                    Campaign ccc = campaignAffiliate.getCampaign();
                    return ccc;
                }).collect(Collectors.toList());
            }

            Set<Long> ids = new HashSet<>();
            campaigns.stream().spliterator().forEachRemaining(campaign -> {
                campaign.getMedias().forEach(media -> {
                    ids.add(media.getId());
                });
            });

            Page<Media> page = repository.findByIdIn(ids, pageableRequest);
            return page.map(MediaDTO::from);
        }

    }

    public Media getByFileId(Long fileId) {
        Media page = repository.findByIdFile(Long.toString(fileId));
        return page;
    }

    public Page<MediaDTO> getByCampaignId(Long campaignId, Pageable pageableRequest) {
        Campaign cc = campaignRepository.findById(campaignId).orElseThrow(() -> new ElementCleveradException("Campaign", campaignId));
        Set<Media> list = new HashSet<>();
        if (jwtUserDetailsService.getRole().equals("Admin")) {
            list = cc.getMedias();
        } else {
            // N.B. Lista presettata a status == True
            list = cc.getMedias().stream().filter(media -> media.getStatus().booleanValue()).collect(Collectors.toSet());
        }
        Page<Media> page = new PageImpl<>(list.stream().distinct().collect(Collectors.toList()));

        return page.map(media -> {
            MediaDTO dto = MediaDTO.from(media);
            dto.setBannerCode(generaBannerCode(dto, media.getId(), campaignId, 0L, 0L));
            return dto;
        });
    }

    public MediaDTO getByIdAndCampaignID(Long mediaId, Long campaignId) {
        Media media = repository.findById(mediaId).orElseThrow(() -> new ElementCleveradException("Media", mediaId));
        MediaDTO dto = MediaDTO.from(media);
        dto.setBannerCode(generaBannerCode(dto, mediaId, campaignId, 0L, 0L));
        return dto;
    }

    public MediaDTO getByIdAndCampaignIDChannelID(Long mediaId, Long campaignId, Long channelID) {
        Media media = repository.findById(mediaId).orElseThrow(() -> new ElementCleveradException("Media", mediaId));
        MediaDTO dto = MediaDTO.from(media);
        dto.setBannerCode(generaBannerCode(dto, mediaId, campaignId, channelID, 0L));
        return dto;
    }

    public MediaDTO getByIdAndCampaignIDChannelIDTargetID(Long mediaId, Long campaignId, Long channelID, Long targetId) {
        Media media = repository.findById(mediaId).orElseThrow(() -> new ElementCleveradException("Media", mediaId));
        MediaDTO dto = MediaDTO.from(media);
        dto.setBannerCode(generaBannerCode(dto, mediaId, campaignId, channelID, targetId));
        return dto;
    }

    private String generaBannerCode(MediaDTO dto, Long mediaId, Long campaignId, Long channelID, Long targetId) {
        String bannerCode = dto.getBannerCode();

        if (StringUtils.isNotBlank(dto.getUrl())) bannerCode = bannerCode.replace("{{url}}", dto.getUrl());

        String url = dto.getUrl();
        if (StringUtils.isNotBlank(url)) bannerCode = bannerCode.replace("{{url}}", url);

        //TODO : sae non c'è una lista ma solo uno ?
        List<TargetDTO> lista = targetBusiness.getByMediaIdAll(mediaId).stream().collect(Collectors.toList());
        if (lista.size() > 0) {
            TargetDTO tt = new TargetDTO();
            lista.forEach(targetDTO -> {
                if (targetDTO.getId().equals(targetId)) {
                    tt.setTarget(targetDTO.getTarget());
                }
            });

            if (StringUtils.isNotBlank(tt.getTarget()))
                bannerCode = bannerCode.replace("{{target}}", tt.getTarget());
        }

        if (!jwtUserDetailsService.getRole().equals("Admin")) {
            bannerCode = bannerCode.replace("{{refferalId}}", refferalService.creaEncoding(Long.toString(campaignId), Long.toString(mediaId), String.valueOf(jwtUserDetailsService.getAffiliateID()), Long.toString(channelID), Long.toString(targetId)));
        }

        return bannerCode;
    }

    /**
     * ============================================================================================================
     **/

    private Specification<Media> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getName() != null) {
                predicates.add(cb.equal(root.get("name"), "%" + request.getName() + "%"));
            }

            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }

            if (request.getUrl() != null) {
                predicates.add(cb.equal(root.get("url"), request.getUrl()));
            }

            if (request.getTypeId() != null) {
                predicates.add(cb.equal(root.get("mediaType").get("id"), request.getTypeId()));
            }

            if (request.getCampaignId() != null) {
                predicates.add(cb.equal(root.get("campaigns").get("campaign").get("id"), request.getCampaignId()));
            }

            if (request.getTarget() != null) {
                predicates.add(cb.like(root.get("target"), request.getTarget()));
            }
            if (request.getBannerCode() != null) {
                predicates.add(cb.equal(root.get("bannerCode"), request.getBannerCode()));
            }
            if (request.getNote() != null) {
                predicates.add(cb.equal(root.get("note"), request.getNote()));
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
        private String name;
        private Long typeId;
        private String url;
        private String target;
        private String bannerCode;
        private String note;
        private String idFile;
        private Long campaignId;
        private String mailSubject;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private String name;
        private Long typeId;
        private String url;
        private String target;
        private String mailSubject;
        private String bannerCode;
        private String note;
        private Long campaignId;
        private Boolean status;
        private String idFile;
        private LocalDateTime creationDate;
        private LocalDateTime lastModificationDate;
        private Instant creationDateFrom;
        private Instant creationDateTo;
        private Instant lastModificationDateFrom;
        private Instant lastModificationDateTo;
    }

}
