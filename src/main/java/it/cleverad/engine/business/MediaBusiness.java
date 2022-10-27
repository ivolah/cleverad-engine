package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.Affiliate;
import it.cleverad.engine.persistence.model.Campaign;
import it.cleverad.engine.persistence.model.Media;
import it.cleverad.engine.persistence.repository.AffiliateRepository;
import it.cleverad.engine.persistence.repository.CampaignRepository;
import it.cleverad.engine.persistence.repository.MediaRepository;
import it.cleverad.engine.persistence.repository.MediaTypeRepository;
import it.cleverad.engine.service.JwtUserDetailsService;
import it.cleverad.engine.service.RefferalService;
import it.cleverad.engine.web.dto.MediaDTO;
import it.cleverad.engine.web.dto.MediaTypeDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import it.cleverad.engine.web.exception.PostgresDeleteCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
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

            String campID = String.valueOf(id);
            String mediaID = String.valueOf(media.getId());
            String affilaiteID = String.valueOf(jwtUserDetailsService.getAffiliateID());

            String channelID = "";
            String bannerCode = dto.getBannerCode();
            String url = dto.getUrl();
            if (StringUtils.isNotBlank(url)) bannerCode = bannerCode.replace("{{url}}", url);

            String target = dto.getTarget();
            if (StringUtils.isNotBlank(target)) bannerCode = bannerCode.replace("{{target}}", target);

            bannerCode = bannerCode.replace("{{refferalId}}", refferalService.creaEncoding(campID, mediaID, affilaiteID, channelID));
            dto.setBannerCode(bannerCode);

        }
        return dto;
    }

    // DELETE BY ID
    public void delete(Long id) {
        Media media = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Media", id));
        try {
            //    if (media.getMediaCampaign() != null) mediaCampaignBusiness.delete(media.getMediaCampaign().getId());
            repository.deleteById(id);
        } catch (ConstraintViolationException ex) {
            throw ex;
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
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
        mappedEntity.setBannerCode(bannerCode);
        Media saved = repository.save(mappedEntity);

        Campaign cc = campaignRepository.findById(filter.campaignId).orElseThrow(() -> new ElementCleveradException("Campaign", filter.getCampaignId()));
        cc.addMedia(saved);
        campaignRepository.save(cc);

        return MediaDTO.from(saved);
    }

    // SEARCH PAGINATED
    public Page<MediaDTO> search(Filter request, Pageable pageableRequest) {
        if (jwtUserDetailsService.getRole().equals("Admin")) {
            Page<Media> page = repository.findAll(getSpecification(request), PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id"))));
            return page.map(media -> MediaDTO.from(media));
        } else {
            Affiliate cc = affiliateRepository.findById(jwtUserDetailsService.getAffiliateID()).orElseThrow(() -> new ElementCleveradException("Affiliate", jwtUserDetailsService.getAffiliateID()));
            Set<Campaign> campaigns = cc.getCampaigns();

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

    public Page<MediaDTO> getByCampaignId(Long campaignId, Pageable pageableRequest) {
        Campaign cc = campaignRepository.findById(campaignId).orElseThrow(() -> new ElementCleveradException("Campaign", campaignId));
        Set<Media> list = cc.getMedias();
        Page<Media> page = new PageImpl<>(list.stream().distinct().collect(Collectors.toList()));
        return page.map(media -> {
            MediaDTO dto = MediaDTO.from(media);
            dto.setBannerCode(generaBannerCode(dto, media.getId(), campaignId, 0L));
            return dto;
        });
    }

    public MediaDTO getByIdAndCampaignID(Long mediaId, Long campaignId) {
        Media media = repository.findById(mediaId).orElseThrow(() -> new ElementCleveradException("Media", mediaId));
        MediaDTO dto = MediaDTO.from(media);
        dto.setBannerCode(generaBannerCode(dto, mediaId, campaignId, 0L));
        return dto;
    }

    public MediaDTO getByIdAndCampaignIDChannelID(Long mediaId, Long campaignId, Long channelID) {
        Media media = repository.findById(mediaId).orElseThrow(() -> new ElementCleveradException("Media", mediaId));
        MediaDTO dto = MediaDTO.from(media);
        dto.setBannerCode(generaBannerCode(dto, mediaId, campaignId, channelID));
        return dto;
    }

    private String generaBannerCode(MediaDTO dto, Long mediaId, Long campaignId, Long channelID) {
        String bannerCode = dto.getBannerCode();

        if (StringUtils.isNotBlank(dto.getUrl())) bannerCode = bannerCode.replace("{{url}}", dto.getUrl());
        if (StringUtils.isNotBlank(dto.getTarget())) bannerCode = bannerCode.replace("{{target}}", dto.getTarget());

        if (!jwtUserDetailsService.getRole().equals("Admin")) {
            String refID = campaignId + "||" + mediaId + "||" + jwtUserDetailsService.getAffiliateID() + "||" + channelID;
            byte[] encodedRefferal = Base64.getEncoder().encode(refID.getBytes(StandardCharsets.UTF_8));
            String reString = new String(encodedRefferal);
            bannerCode = bannerCode.replace("{{refferalId}}", reString);
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
                predicates.add(cb.equal(root.get("name"), request.getName()));
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
        private String bannerCode;
        private String note;

        private String idFile;
        private String status;
        private LocalDateTime creationDate;
        private LocalDateTime lastModificationDate;

        private Long campaignId;

        private Instant creationDateFrom;
        private Instant creationDateTo;
        private Instant lastModificationDateFrom;
        private Instant lastModificationDateTo;

    }

}
