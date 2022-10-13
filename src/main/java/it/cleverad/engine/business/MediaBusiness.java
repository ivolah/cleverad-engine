package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.Media;
import it.cleverad.engine.persistence.repository.MediaRepository;
import it.cleverad.engine.service.JwtUserDetailsService;
import it.cleverad.engine.web.dto.MediaDTO;
import it.cleverad.engine.web.dto.MediaTypeDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Slf4j
@Component
@Transactional
public class MediaBusiness {

    @Autowired
    private MediaRepository repository;

    @Autowired
    private Mapper mapper;

    @Autowired
    private MediaCampaignBusiness mediaCampaignBusiness;

    @Autowired
    private MediaTypeBusiness mediaTypeBusiness;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

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
        map.setCreationDate(LocalDateTime.now());
        map.setLastModificationDate(LocalDateTime.now());
        MediaDTO mediaDTO = MediaDTO.from(repository.save(map));

        // aggiungo riferimento campagna se c'è
        if (request.getCampaignId() != null) {
            MediaCampaignBusiness.BaseCreateRequest rr = new MediaCampaignBusiness.BaseCreateRequest();
            rr.setMediaId(mediaDTO.getId());
            rr.setCampaignId(Long.valueOf(request.getCampaignId()));
            mediaCampaignBusiness.create(rr);
        }

        return mediaDTO;
    }

    // GET BY ID
    public MediaDTO findById(Long id) {
        try {
            Media media = repository.findById(id).orElseThrow(() -> new ElementCleveradException(id));
            MediaDTO dto = MediaDTO.from(media);
            if (dto.getTypeId() != null) {
                MediaTypeDTO mtDto = mediaTypeBusiness.findById(dto.getTypeId());
                dto.setTypeName(mtDto.getName());
            }
            return dto;
        } catch (Exception e) {
            log.error("Errore in findById", e);
            return null;
        }
    }

    // DELETE BY ID
    public void delete(Long id) {
        Media media = repository.findById(id).orElse(null);
        if (media.getMediaCampaign() != null) {
            mediaCampaignBusiness.delete(media.getMediaCampaign().getId());
        }
        repository.deleteById(id);
    }

    // UPDATE
    public MediaDTO update(Long id, Filter filter) {
        try {
            Media media = repository.findById(id).orElseThrow(() -> new ElementCleveradException(id));
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

            // aggiungo riferimento campagna se c'è
            if (filter.getCampaignId() != null) {
                MediaCampaignBusiness.BaseCreateRequest rr = null;
                rr.setMediaId(mappedEntity.getId());
                rr.setCampaignId(Long.valueOf(filter.getCampaignId()));
                mediaCampaignBusiness.create(rr);
            }

            return MediaDTO.from(repository.save(mappedEntity));
        } catch (Exception e) {
            log.error("Errore in update", e);
            return null;
        }
    }

    // SEARCH PAGINATED
    public Page<MediaDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<Media> page = repository.findAll(getSpecification(request), pageable);
        return page.map(media -> {
            MediaDTO dto = MediaDTO.from(media);
            if (dto.getTypeId() != null) {
                MediaTypeDTO mtDto = mediaTypeBusiness.findById(dto.getTypeId());
                dto.setTypeName(mtDto.getName());
            }
            return dto;
        });
    }

    public Page<MediaDTO> getByCampaignId(Long id, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<Media> page = repository.findMediaCampaigns(id, pageable);
        return page.map(media -> {
            MediaDTO dto = MediaDTO.from(media);
            if (dto.getTypeId() != null) {

                MediaTypeDTO mtDto = mediaTypeBusiness.findById(dto.getTypeId());
                dto.setTypeName(mtDto.getName());

                String campID = String.valueOf(id);
                String mediaID = String.valueOf(media.getId());
                String affilaiteID = String.valueOf(jwtUserDetailsService.getAffiliateID());

                String channelID = "";

                String refID = campID + "||" + mediaID + "||" + affilaiteID + "||" + channelID;
                byte[] encodedRefferal = Base64.getEncoder().encode(refID.getBytes(StandardCharsets.UTF_8));

                String bannerCode = dto.getBannerCode();
                String url = dto.getUrl();
                if (StringUtils.isNotBlank(url)) bannerCode = bannerCode.replace("{{url}}", url);

                String target = dto.getTarget();
                if (StringUtils.isNotBlank(target)) bannerCode = bannerCode.replace("{{target}}", target);

                bannerCode = bannerCode.replace("{{refferalId}}", new String(encodedRefferal));
                dto.setBannerCode(bannerCode);

                log.info("BANNER CODE  {}", bannerCode);
            }
            return dto;
        });
    }

    public MediaDTO getByIdAndCampaignID(Long id, Long idCampaign) {
        Pageable pageable = PageRequest.of(0,100, Sort.by(Sort.Order.asc("id")));
        Page<Media> page = repository.findMediaCampaigns(idCampaign, pageable);
        return page.stream().filter(media ->
            media.getId() == id
        ).findFirst().map(media -> {
            MediaDTO dto = MediaDTO.from(media);
            if (dto.getTypeId() != null) {
                MediaTypeDTO mtDto = mediaTypeBusiness.findById(dto.getTypeId());
                dto.setTypeName(mtDto.getName());

                String bannerCode = dto.getBannerCode();

                String refID = idCampaign + "||" + id + "||" + jwtUserDetailsService.getAffiliateID() + "||" + "0";
                byte[] encodedRefferal = Base64.getEncoder().encode(refID.getBytes(StandardCharsets.UTF_8));
                bannerCode = bannerCode.replace("{{refferalId}}", new String(encodedRefferal));
                if (StringUtils.isNotBlank(dto.getUrl())) bannerCode = bannerCode.replace("{{url}}", dto.getUrl());
                if (StringUtils.isNotBlank(dto.getTarget())) bannerCode = bannerCode.replace("{{target}}", dto.getTarget());

                dto.setBannerCode(bannerCode);
            }
            return dto;
        }).get();

    }

    public MediaDTO getByIdAndCampaignIDChannelID(Long id, Long idCampaign, Long idChannel) {
        Pageable pageable = PageRequest.of(0,100, Sort.by(Sort.Order.asc("id")));
        Page<Media> page = repository.findMediaCampaigns(idCampaign, pageable);
        return page.stream().filter(media ->
                media.getId() == id
        ).findFirst().map(media -> {
            MediaDTO dto = MediaDTO.from(media);
            if (dto.getTypeId() != null) {
                MediaTypeDTO mtDto = mediaTypeBusiness.findById(dto.getTypeId());
                dto.setTypeName(mtDto.getName());

                String bannerCode = dto.getBannerCode();

                String refID = idCampaign + "||" + id + "||" + jwtUserDetailsService.getAffiliateID() + "||" + idChannel;
                byte[] encodedRefferal = Base64.getEncoder().encode(refID.getBytes(StandardCharsets.UTF_8));
                bannerCode = bannerCode.replace("{{refferalId}}", new String(encodedRefferal));
                if (StringUtils.isNotBlank(dto.getUrl())) bannerCode = bannerCode.replace("{{url}}", dto.getUrl());
                if (StringUtils.isNotBlank(dto.getTarget())) bannerCode = bannerCode.replace("{{target}}", dto.getTarget());

                dto.setBannerCode(bannerCode);
            }
            return dto;
        }).get();

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
                predicates.add(cb.equal(root.get("typeId"), request.getTypeId()));
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
        private String typeId;
        private String url;
        private String target;
        private String bannerCode;
        private String note;
        private String idFile;
        private String status;
        private String campaignId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;

        private String name;
        private String typeId;
        private String url;
        private String target;
        private String bannerCode;
        private String note;

        private String idFile;
        private String status;
        private LocalDateTime creationDate;
        private LocalDateTime lastModificationDate;

        private String campaignId;

        private Instant creationDateFrom;
        private Instant creationDateTo;
        private Instant lastModificationDateFrom;
        private Instant lastModificationDateTo;

    }

}
