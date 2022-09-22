package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.Campaign;
import it.cleverad.engine.persistence.repository.CampaignRepository;
import it.cleverad.engine.service.JwtUserDetailsService;
import it.cleverad.engine.web.dto.CampaignDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@Transactional
public class CampaignBusiness {

    @Autowired
    private CampaignRepository repository;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private Mapper mapper;

    @Autowired
    private MediaCampaignBusiness mediaCampaignBusiness;

    @Autowired
    private AffiliateChannelCommissionCampaignBusiness affiliateChannelCommissionCampaignBusiness;

    @Autowired
    private CampaignCategoryBusiness campaignCategoryBusiness;

    @Autowired
    private CampaignCookieBusiness campaignCookieBusiness;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public CampaignDTO create(BaseCreateRequest request) {
        Campaign map = mapper.map(request, Campaign.class);
        map.setCreationDate(LocalDateTime.now());
        map.setLastModificationDate(LocalDateTime.now());
        CampaignDTO dto = CampaignDTO.from(repository.save(map));

        //Category
        if (StringUtils.isNotBlank(request.getCategories())) {
            Arrays.stream(request.getCategories().split(",")).forEach(s -> {
                campaignCategoryBusiness.create(new CampaignCategoryBusiness.BaseCreateRequest(dto.getId(), Long.valueOf(s)));
            });
        }

        //Cookie
        if (StringUtils.isNotBlank(request.getCookies())) {
            campaignCookieBusiness.create(new CampaignCookieBusiness.BaseCreateRequest(dto.getId(), Long.valueOf(request.getCookies())));
        }


        return dto;
    }

    // GET BY ID
    public CampaignDTO findById(Long id) {
        try {
            Campaign campaign = null;

            if (jwtUserDetailsService.getRole().equals("Admin")) {
                campaign = repository.findById(id).orElse(null);
            } else {
                Filter request = new Filter();
                request.setId(id);
//                Page<Campaign> page = repository.findAll(getSpecification(request), Pageable.unpaged());
                campaign = repository.findById(id).orElse(null);
            }

            if (campaign != null) return CampaignDTO.from(campaign);
            else return null;

        } catch (Exception e) {
            log.error("Errore in findById", e);
            return null;
        }
    }

    // DELETE BY ID
    public void delete(Long id) {
        Campaign campaign = repository.findById(id).orElse(null);
        campaign.getMediaCampaignList().stream().forEach(mediaCampaign -> mediaCampaignBusiness.delete(mediaCampaign.getId()));
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ee) {
            log.warn("Impossibile cancellare campagna.");
        }
    }

    // SEARCH PAGINATED
    public Page<CampaignDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<Campaign> page;
        if (jwtUserDetailsService.getRole().equals("Admin")) {
            page = repository.findAll(getSpecification(request), pageable);
        } else {
            page = repository.findAffiliateCampaigns(jwtUserDetailsService.getAffiliateID(), pageable);
        }
        return page.map(CampaignDTO::from);
    }

    // UPDATE
    public CampaignDTO update(Long id, Filter filter) {
        try {
            Campaign campaign = repository.findById(id).orElseThrow(Exception::new);
            CampaignDTO campaignDTOfrom = CampaignDTO.from(campaign);
            mapper.map(filter, campaignDTOfrom);
            Campaign mappedEntity = mapper.map(campaign, Campaign.class);
            mappedEntity.setLastModificationDate(LocalDateTime.now());
            mapper.map(campaignDTOfrom, mappedEntity);
            return CampaignDTO.from(repository.save(mappedEntity));
        } catch (Exception e) {
            log.error("Errore in update", e);
            return null;
        }
    }

    // TROVA LE CAMPAGNE DELL AFFILIATE
    public Page<CampaignDTO> getCampaigns(Long affiliateId, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<Campaign> page = repository.findAffiliateCampaigns(affiliateId, pageable);
//        return page.stream().map(CampaignDTO::from).collect(Collectors.toList());
        return page.map(CampaignDTO::from);
    }

    /**
     * ============================================================================================================
     **/
    private Specification<Campaign> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getName() != null) {
                predicates.add(cb.equal(root.get("name"), request.getName()));
            }
            if (request.getShortDescription() != null) {
                predicates.add(cb.equal(root.get("shortDescription"), request.getShortDescription()));
            }
            if (request.getLongDescription() != null) {
                predicates.add(cb.equal(root.get("longDescription"), request.getLongDescription()));
            }
            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
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

            if (request.getStartDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startDate"), LocalDateTime.ofInstant(request.getStartDateFrom(), ZoneOffset.UTC)));
            }
            if (request.getStartDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("startDate"), LocalDateTime.ofInstant(request.getStartDateTo().plus(1, ChronoUnit.DAYS), ZoneOffset.UTC)));
            }

            if (request.getEndDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("endDate"), LocalDateTime.ofInstant(request.getEndDateFrom(), ZoneOffset.UTC)));
            }
            if (request.getEndDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("endDate"), LocalDateTime.ofInstant(request.getEndDateTo().plus(1, ChronoUnit.DAYS), ZoneOffset.UTC)));
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
        private String shortDescription;
        private String longDescription;
        private Boolean status;
        private String idFile;
        private String comissions;
        private String categories;
        private String cookies;
        private String valuta;
        private Long budget;
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSz")
        private LocalDateTime startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSz")
        private LocalDateTime endDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private String name;
        private String shortDescription;
        private String longDescription;
        private Boolean status;
        private String idFile;
        private String valuta;
        private Long budget;
        private Instant creationDateFrom;
        private Instant creationDateTo;
        private Instant lastModificationDateFrom;
        private Instant lastModificationDateTo;
        private Instant startDateFrom;
        private Instant startDateTo;
        private Instant endDateFrom;
        private Instant endDateTo;
    }

}