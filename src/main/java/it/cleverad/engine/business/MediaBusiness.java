package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.persistence.model.service.*;
import it.cleverad.engine.persistence.repository.service.*;
import it.cleverad.engine.service.ReferralService;
import it.cleverad.engine.service.tinyurl.TinyData;
import it.cleverad.engine.service.tinyurl.TinyUrlService;
import it.cleverad.engine.web.dto.MediaDTO;
import it.cleverad.engine.web.dto.MediaTypeDTO;
import it.cleverad.engine.web.dto.TargetDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
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
    private DictionaryRepository dictionaryRepository;
    @Autowired
    private ReferralService referralService;
    @Autowired
    private TargetBusiness targetBusiness;
    @Autowired
    private TinyUrlService tinyUrlService;
    @Autowired
    private UrlBusiness urlBusiness;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public MediaDTO create(BaseCreateRequest request) {

        log.trace("Creating MediaDTO  from " + request);
        String bannerCode = request.getBannerCode();
        request.setVisibile(true);
        String url = request.getUrl();
        if (StringUtils.isNotBlank(url)) bannerCode = bannerCode.replace("{{url}}", url);

        String target = request.getTarget();
        if (StringUtils.isNotBlank(target)) bannerCode = bannerCode.replace("{{target}}", target);

        request.setBannerCode(bannerCode);

        Media map = mapper.map(request, Media.class);
        map.setMediaType(mediaTypeRepository.findById(request.typeId).orElseThrow(() -> new ElementCleveradException("Media Type", request.typeId)));
        map.setStatus(false);
        map.setCreationDate(LocalDateTime.now());
        Media saved = repository.save(map);

        Campaign cc = campaignRepository.findById(request.campaignId).orElseThrow(() -> new ElementCleveradException("Campaign", request.getCampaignId()));
        cc.addMedia(saved);
        if (request.formatId != null)
            map.setDictionary(dictionaryRepository.findById(request.formatId).orElseThrow(() -> new ElementCleveradException("Dictionary", request.formatId)));
        campaignRepository.save(cc);

        return MediaDTO.from(saved);
    }

    // GET BY ID
    public MediaDTO findById(Long id) {
        Media media = repository.findById(id).orElse(null);
        MediaDTO dto = null;
        if (media != null) {
            dto = MediaDTO.from(media);
            if (dto.getTypeId() != null) {
                MediaTypeDTO mtDto = mediaTypeBusiness.findById(dto.getTypeId());
                dto.setTypeName(mtDto.getName());
                dto.setBannerCode(generaBannerCode(dto, media.getId(), id, 0L, 0L));
            }
        }
        return dto;
    }

    // DELETE BY ID
    public void delete(Long id) {
        Media media = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Media", id));
        try {
            if (media.getCampaigns() != null) {
                media.getCampaigns().forEach(campaign -> campaign.removeMedia(id));
            }
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            this.disable(id);
            log.error("Eccezione gestita nella cancellazione, disabilito media " + id, ex);
        }
    }

    // UPDATE
    public MediaDTO update(Long id, Filter filter) {
        log.trace("Updating media" + filter);

        Media media = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Media", id));
        mapper.map(filter, media);

        String bannerCode = media.getBannerCode();
        String url = media.getUrl();
        if (StringUtils.isNotBlank(url)) bannerCode = bannerCode.replace("{{url}}", url);
        String target = media.getTarget();
        if (StringUtils.isNotBlank(target)) bannerCode = bannerCode.replace("{{target}}", target);

//        List<Target> targets = (List<Target>) mappedEntity.getTargets();
//        targets.stream().filter(target -> StringUtils.isNotBlank(target.getTarget())).forEach(target -> {
//            bannerCode.replace("{{target}}", target.getTarget());
//            // TODO COME GESTIRE????? NON AGGIORNIAMO ma facciamo in logica target?
//            // multipli banner code??
//        });

        media.setMediaType(mediaTypeRepository.findById(filter.typeId).orElseThrow(() -> new ElementCleveradException("Media Type", filter.typeId)));
        media.setLastModificationDate(LocalDateTime.now());
        media.setBannerCode(bannerCode);
        if (filter.formatId != null)
            media.setDictionary(dictionaryRepository.findById(filter.formatId).orElseThrow(() -> new ElementCleveradException("Dictionary", filter.formatId)));

        Media saved = repository.save(media);

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

        if (jwtUserDetailsService.isAdmin()) {
            if (request.getCampaignId() != null) {
                Campaign cc = campaignRepository.findById(request.getCampaignId()).orElseThrow(() -> new ElementCleveradException("Campaign", request.getCampaignId()));
                Set<Media> list = cc.getMedias();
                if (request.getTypeId() != null)
                    list = list.stream().filter(media -> media.getMediaType().getId().equals(request.getTypeId())).collect(Collectors.toSet());

                final int end = (int) Math.min((pageableRequest.getOffset() + pageableRequest.getPageSize()), list.size());
                Page<Media> page = new PageImpl<>(list.stream().distinct().collect(Collectors.toList()).subList(pageableRequest.getPageNumber(), end), pageableRequest, list.size());
                return page.map(MediaDTO::from);
            } else {
                Page<Media> page = repository.findAll(getSpecification(request), PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id"))));
                return page.map(MediaDTO::from);
            }
        } else {
            Affiliate cc = affiliateRepository.findById(jwtUserDetailsService.getAffiliateId()).orElseThrow(() -> new ElementCleveradException("Affiliate", jwtUserDetailsService.getAffiliateId()));

            List<Campaign> campaigns = new ArrayList<>();
            if (cc.getCampaignAffiliates() != null) {
                campaigns = cc.getCampaignAffiliates().stream().map(CampaignAffiliate::getCampaign).filter(Campaign::getStatus).collect(Collectors.toList());
            }

            Set<Long> ids = new HashSet<>();
            campaigns.stream().spliterator().forEachRemaining(campaign -> campaign.getMedias().stream().filter(media -> media.getStatus().equals(true)).forEach(media -> ids.add(media.getId())));

            Page<Media> page = repository.findByIdIn(ids, pageableRequest);
            return page.map(MediaDTO::from);
        }

    }

    public Media getByFileId(Long fileId) {
        return repository.findByIdFile(Long.toString(fileId));
    }

    public Page<MediaDTO> getByCampaignId(Long campaignId, Pageable pageableRequest) {
        Campaign cc = campaignRepository.findById(campaignId).orElseThrow(() -> new ElementCleveradException("Campaign", campaignId));
        Set<Media> list;
        if (jwtUserDetailsService.isAdmin()) {
            list = cc.getMedias();
        } else {
            list = cc.getMedias().stream().filter(Media::getStatus).collect(Collectors.toSet());
        }
        Page<Media> page = new PageImpl<>(list.stream().distinct().collect(Collectors.toList()));
        return page.map(media -> {
            MediaDTO dto = MediaDTO.from(media);
            dto.setBannerCode(generaBannerCode(dto, media.getId(), campaignId, 0L, 0L));
            return dto;
        });
    }

    public Page<MediaDTO> searchBB() {

        Affiliate cc = affiliateRepository.findById(jwtUserDetailsService.getAffiliateId()).orElseThrow(() -> new ElementCleveradException("Affiliate", jwtUserDetailsService.getAffiliateId()));

        List<Campaign> campaigns = new ArrayList<>();
        if (cc.getCampaignAffiliates() != null) {
            campaigns = cc.getCampaignAffiliates().stream().map(CampaignAffiliate::getCampaign).filter(Campaign::getStatus).collect(Collectors.toList());
        }

        Set<Long> ids = new HashSet<>();
        campaigns.stream().spliterator().forEachRemaining(campaign -> campaign.getMedias().stream().filter(media -> media.getStatus().equals(true)).forEach(media -> ids.add(media.getId())));

        Page<Media> page = repository.findByIdIn(ids, Pageable.ofSize(Integer.MAX_VALUE));
        List<Media> ll = page.filter(media -> media.getMediaType().getId() == 6L).stream().collect(Collectors.toList());
        page = new PageImpl<>(ll);
        return page.map(MediaDTO::from);
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
        if (dto.getTypeId() == 5L) {
            dto.setTarget(setUrlTarget(dto.getTarget(), mediaId, campaignId, channelID, 0L));
            String desc = dto.getDescription();
            desc = desc.replace("{{shorturl}}", dto.getTarget());
            desc = desc.replace("{{linktoimage}}", dto.getUrl());
            dto.setDescription(desc);
        }
        dto.setBannerCode(generaBannerCode(dto, mediaId, campaignId, channelID, 0L));
        return dto;
    }
    private String generaBannerCode(MediaDTO dto, Long mediaId, Long campaignId, Long channelID, Long targetId) {
        String bannerCode = dto.getBannerCode();

        if (StringUtils.isNotBlank(dto.getUrl())) bannerCode = bannerCode.replace("{{url}}", dto.getUrl());

        String url = dto.getUrl();
        if (StringUtils.isNotBlank(url)) bannerCode = bannerCode.replace("{{url}}", url);

        List<TargetDTO> lista = targetBusiness.getByMediaIdAll(mediaId).stream().collect(Collectors.toList());
        if (!lista.isEmpty()) {
            TargetDTO tt = new TargetDTO();
            lista.forEach(targetDTO -> {
                if (targetDTO.getId().equals(targetId)) {
                    tt.setTarget(targetDTO.getTarget());
                }
            });
            if (StringUtils.isNotBlank(tt.getTarget())) bannerCode = bannerCode.replace("{{target}}", tt.getTarget());
        }

        if (!jwtUserDetailsService.isAdmin()) {
            bannerCode = bannerCode.replace("{{refferalId}}", referralService.creaEncoding(Long.toString(campaignId), Long.toString(mediaId), String.valueOf(jwtUserDetailsService.getAffiliateId()), Long.toString(channelID), Long.toString(targetId)));
        }

        return bannerCode;
    }

    private String setUrlTarget(String target, Long mediaId, Long campaignId, Long channelID, Long targetId) {
        target = target.replace("{{refferalId}}", referralService.creaEncoding(Long.toString(campaignId), Long.toString(mediaId), String.valueOf(jwtUserDetailsService.getAffiliateId()), Long.toString(channelID), Long.toString(targetId)));
        Url urlRicercato = urlBusiness.findByLong(target);
        if (urlRicercato == null || urlRicercato.getTiny() == null) {
            // INTEGRAZIONE TINY URL
            String alias = "CAD-" + RandomStringUtils.randomAlphanumeric(6);
            TinyData tinyUrlData = tinyUrlService.createShort(alias, target);
            String tiny = target;
            if (tinyUrlData != null) {
                tiny = tinyUrlData.getData().getTinyUrl();
                UrlBusiness.BaseCreateRequest tinyurl = new UrlBusiness.BaseCreateRequest();
                tinyurl.setLongUrl(target);
                tinyurl.setTiny(tiny);
                tinyurl.setAlias(alias);
                urlBusiness.create(tinyurl);
            }
            return tiny;
        } else {
            log.info("TROVATO :: " + urlRicercato.getTiny());
            return urlRicercato.getTiny();
        }

    }

    /**
     * ============================================================================================================
     **/

    public Specification<Media> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getName() != null) {
                predicates.add(cb.equal(cb.upper(root.get("name")), "%" + request.getName().toUpperCase() + "%"));
            }

            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }

            if (request.getVisibile() != null) {
                predicates.add(cb.equal(root.get("visibile"), request.getVisibile()));
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
                predicates.add(cb.like(cb.upper(root.get("target")), "%" + request.getTarget().toUpperCase() + "%"));
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
    @ToString
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
        private String sender;
        private Boolean visibile;
        private Boolean status;
        private String description;
        private String title;
        private Long formatId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
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
        private Instant creationDateFrom;
        private Instant creationDateTo;
        private Instant lastModificationDateFrom;
        private Instant lastModificationDateTo;
        private String sender;
        private Boolean visibile;
        private String description;
        private String title;
        private Long formatId;
    }

}