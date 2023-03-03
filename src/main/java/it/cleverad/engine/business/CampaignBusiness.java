package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.Affiliate;
import it.cleverad.engine.persistence.model.service.Campaign;
import it.cleverad.engine.persistence.model.service.CampaignCategory;
import it.cleverad.engine.persistence.repository.service.*;
import it.cleverad.engine.service.JwtUserDetailsService;
import it.cleverad.engine.service.RefferalService;
import it.cleverad.engine.web.dto.CampaignAffiliateDTO;
import it.cleverad.engine.web.dto.CampaignDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import it.cleverad.engine.web.exception.PostgresDeleteCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
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
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional
public class CampaignBusiness {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private CampaignRepository repository;
    @Autowired
    private Mapper mapper;
    @Autowired
    private CampaignCategoryBusiness campaignCategoryBusiness;
    @Autowired
    private CookieRepository cookieRepository;
    @Autowired
    private AdvertiserRepository advertiserRepository;
    @Autowired
    private PlannerRepository plannerRepository;
    @Autowired
    private DealerRepository dealerRepository;
    @Autowired
    private AffiliateRepository affiliateRepository;
    @Autowired
    private CampaignAffiliateBusiness campaignAffiliateBusiness;
    @Autowired
    private RefferalService refferalService;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public CampaignDTO create(BaseCreateRequest request) {

        Campaign map = mapper.map(request, Campaign.class);
        map.setCookie(cookieRepository.findById(request.getCookieId()).orElseThrow(() -> new ElementCleveradException("Cookie", request.cookieId)));
        map.setAdvertiser(advertiserRepository.findById(request.getCompanyId()).orElseThrow(() -> new ElementCleveradException("Advertiser", request.getCompanyId())));
        map.setDealer(dealerRepository.findById(request.getDealerId()).orElseThrow(() -> new ElementCleveradException("Dealer", request.dealerId)));
        map.setPlanner(plannerRepository.findById(request.getPlannerId()).orElseThrow(() -> new ElementCleveradException("Planner", request.plannerId)));

        CampaignDTO dto = CampaignDTO.from(repository.save(map));
        CampaignDTO finalDto = dto;
        //Category
        if (StringUtils.isNotBlank(request.getCategories())) {
            Arrays.stream(request.getCategories().split(","))
                    .map(s -> campaignCategoryBusiness.create(new CampaignCategoryBusiness.BaseCreateRequest(finalDto.getId(), Long.valueOf(s)))).collect(Collectors.toList());
        }
        String encodedID = refferalService.encode(String.valueOf(dto.getId()));

        Campaign campaign = repository.findById(dto.getId()).orElseThrow(() -> new ElementCleveradException("Campaign", finalDto.getId()));
        campaign.setEncodedId(encodedID);

        dto = CampaignDTO.from(repository.save(map));

        return dto;
    }

    // GET BY ID
    public CampaignDTO findById(Long id) {

        Campaign campaign = null;

        if (jwtUserDetailsService.getRole().equals("Admin")) {
            campaign = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Campaign", id));
        } else {
            Filter request = new Filter();
            request.setId(id);
            // TODO logica per seach di quelli assegnati
            campaign = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Campaign", id));
        }

        if (campaign != null)
            return CampaignDTO.from(campaign);
        else
            return null;

    }

    // DELETE BY ID
    public void delete(Long id) {
        Campaign campaign = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Campaign", id));
        try {
            if (campaign != null) {
                //   campaign.getMediaCampaignList().stream().forEach(mediaCampaign -> mediaCampaignBusiness.delete(mediaCampaign.getId()));
                campaign.getCampaignCategories().stream().forEach(campaignCategory -> campaignCategoryBusiness.delete(campaignCategory.getId()));
            }
            repository.deleteById(id);
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    // SEARCH PAGINATED
    public Page<CampaignDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<Campaign> page;
        if (jwtUserDetailsService.getRole().equals("Admin")) {
            page = repository.findAll(getSpecification(request), pageable);

            return page.map(CampaignDTO::from);
        } else {
            Affiliate cc = affiliateRepository.findById(jwtUserDetailsService.getAffiliateID()).orElseThrow(() -> new ElementCleveradException("Affiliate", jwtUserDetailsService.getAffiliateID()));

            List<Campaign> campaigns = new ArrayList<>();
            if (cc.getCampaignAffiliates() != null) {
                campaigns = cc.getCampaignAffiliates().stream().map(campaignAffiliate -> {
                    Campaign ccc = campaignAffiliate.getCampaign();
                    return ccc;
                }).collect(Collectors.toList());
            }

            page = new PageImpl<>(campaigns.stream().distinct().collect(Collectors.toList()));
            return page.map(CampaignDTO::from);
        }
    }


    // UPDATE
    public CampaignDTO update(Long id, Filter filter) {

        Campaign campaign = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Campaign", id));
        CampaignDTO campaignDTOfrom = CampaignDTO.from(campaign);
        mapper.map(filter, campaignDTOfrom);

        Campaign mappedEntity = mapper.map(campaign, Campaign.class);
        // SET
        mappedEntity.setCookie(cookieRepository.findById(filter.getCookieId()).orElseThrow(() -> new ElementCleveradException("Cookie", filter.cookieId)));
        mappedEntity.setAdvertiser(advertiserRepository.findById(filter.getCompanyId()).orElseThrow(() -> new ElementCleveradException("Advertiser", filter.companyId)));
        if (filter.getDealerId() != null)
            mappedEntity.setDealer(dealerRepository.findById(filter.getDealerId()).orElseThrow(() -> new ElementCleveradException("Dealer", filter.dealerId)));
        if (filter.getPlannerId() != null)
            mappedEntity.setPlanner(plannerRepository.findById(filter.getPlannerId()).orElseThrow(() -> new ElementCleveradException("Planner", filter.plannerId)));

        // SET Category - cancello precedenti
        campaignCategoryBusiness.deleteByCampaignID(id);
        // setto nuvoi
        if (!filter.getCategoryList().isEmpty()) {
            Set<CampaignCategory> collect =
                    filter.getCategoryList().stream().map(ss -> campaignCategoryBusiness.createEntity(new CampaignCategoryBusiness.BaseCreateRequest(id, ss))).collect(Collectors.toSet());
            mappedEntity.setCampaignCategories(collect);
        }
        mappedEntity.setLastModificationDate(LocalDateTime.now());
        mapper.map(campaignDTOfrom, mappedEntity);

        return CampaignDTO.from(repository.save(mappedEntity));
    }

    // TROVA LE CAMPAGNE DELL AFFILIATE
    public Page<CampaignDTO> getCampaigns(Long affiliateId) {
        Pageable pageable = PageRequest.of(0, 1000, Sort.by(Sort.Order.asc("id")));

        //         Set<Campaign> list = cc.getCampaigns();
        //        Page<Campaign> page = new PageImpl<>(list.stream().distinct().collect(Collectors.toList()));
        Page<CampaignAffiliateDTO> affs = campaignAffiliateBusiness.searchByAffiliateID(affiliateId);

        Affiliate cc = affiliateRepository.findById(affiliateId).orElseThrow(() -> new ElementCleveradException("Affiliate", affiliateId));

        List<Campaign> campaigns = new ArrayList<>();
        if (cc.getCampaignAffiliates() != null) {
            campaigns = cc.getCampaignAffiliates().stream().map(campaignAffiliate -> {
                Campaign ccc = campaignAffiliate.getCampaign();
                return ccc;
            }).collect(Collectors.toList());
        }

        Page<Campaign> page = new PageImpl<>(campaigns.stream().distinct().collect(Collectors.toList()));

        return page.map(CampaignDTO::from);
    }

    // TROVA LE CAMPAGNE DELL AFFILIATE filtrate per ID AFFIALIseTE DAL USER
    public Page<CampaignDTO> getCampaignsGuest(Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Filter request = new Filter();
        Page<Campaign> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CampaignDTO::from);
    }

    public Page<CampaignDTO> searchByMediaId(Long mediaId) {
        Pageable pageable = PageRequest.of(0, 1000, Sort.by(Sort.Order.asc("id")));
        Page<Campaign> page;
        return null;
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
        private Long cookieId;
        private Long companyId;
        private Long plannerId;
        private Long dealerId;
        private String valuta;
        private Long budget;
        private String trackingCode;
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
        private Long cookieId;
        private String comissions;
        private List<Long> categoryList;
        private String trackingCode;
        private Instant creationDateFrom;
        private Instant creationDateTo;
        private Instant lastModificationDateFrom;
        private Instant lastModificationDateTo;
        private Instant startDateFrom;
        private Instant startDateTo;
        private Instant endDateFrom;
        private Instant endDateTo;
        private Long companyId;
        private Long plannerId;
        private Long dealerId;
    }

}
