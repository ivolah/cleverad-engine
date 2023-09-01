package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.Affiliate;
import it.cleverad.engine.persistence.model.service.Campaign;
import it.cleverad.engine.persistence.model.service.CampaignCategory;
import it.cleverad.engine.persistence.model.service.Media;
import it.cleverad.engine.persistence.repository.service.*;
import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.service.ReferralService;
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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.time.LocalDate;
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
    private ReferralService referralService;
    @Autowired
    private RevenueFactorBusiness revenueFactorBusiness;
    @Autowired
    private CommissionBusiness commissionBusiness;
    @Autowired
    private CampaignAffiliateBusiness campaignAffiliateBusiness;
    @Autowired
    private MediaBusiness mediaBusiness;
//    @Autowired
//    TelegramService telegramService;

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

        map.setBudget(request.initialBudget);
        map.setValuta("EUR");

        CampaignDTO dto = CampaignDTO.from(repository.save(map));

        CampaignDTO finalDto = dto;

        //Category
        if (StringUtils.isNotBlank(request.getCategories())) {
            Arrays.stream(request.getCategories().split(",")).map(s -> campaignCategoryBusiness.create(new CampaignCategoryBusiness.BaseCreateRequest(finalDto.getId(), Long.valueOf(s)))).collect(Collectors.toList());
        }
        String encodedID = referralService.encode(String.valueOf(dto.getId()));

        Campaign campaign = repository.findById(dto.getId()).orElseThrow(() -> new ElementCleveradException("Campaign", finalDto.getId()));
        campaign.setEncodedId(encodedID);

        //Aggiungo revenue factor vuoti
//        RevenueFactorBusiness.BaseCreateRequest rfRequest = new RevenueFactorBusiness.BaseCreateRequest();
//        rfRequest.setCampaignId(dto.getId());
//        rfRequest.setStartDate(dto.getStartDate());
//        rfRequest.setDueDate(dto.getEndDate());
//        rfRequest.setStatus(true);
//        rfRequest.setRevenue(0D);
//
//        rfRequest.setDictionaryId(10L);
//        revenueFactorBusiness.create(rfRequest);
//        rfRequest.setDictionaryId(11L);
//        revenueFactorBusiness.create(rfRequest);
//        rfRequest.setDictionaryId(50L);
//        revenueFactorBusiness.create(rfRequest);
        //rfRequest.setDictionaryId(51L);
        //revenueFactorBusiness.create(rfRequest);

        //Aggiungio Commissioni di default altrimenti non funziona
//        CommissionBusiness.BaseCreateRequest comReq = new CommissionBusiness.BaseCreateRequest();
//        comReq.setCampaignId(dto.getId());
//        comReq.setBase(true);
//        comReq.setStatus(true);
//        comReq.setValue(0D);
//        comReq.setStartDate(dto.getStartDate());
//        comReq.setDueDate(dto.getEndDate());
//
//        comReq.setDescription("Commissione CPC di Default");
//        comReq.setName("CPC @0 Default");
//        comReq.setDictionaryId(10L);
//        commissionBusiness.create(comReq);
//        comReq.setDescription("Commissione CPM di Default");
//        comReq.setName("CPM @0 Default");
//        comReq.setDictionaryId(50L);
//        commissionBusiness.create(comReq);
//        comReq.setDescription("Commissione CPL di Default");
//        comReq.setName("CPL @0 Default");
//        comReq.setDictionaryId(11L);
//        commissionBusiness.create(comReq);
//        comReq.setDescription("Commissione CPS di Default");
//        comReq.setName("CPS @0 Default");
//        comReq.setDictionaryId(51L);
//        commissionBusiness.create(comReq);

        // INVIO NOTIFICA CANALE
        //telegramService.sendNotification("Nuova CAMPAGNA  " + campaign.getName() + "!!!! \n Accedi alla piattaforma e richiedi di partecipare!");


        dto = CampaignDTO.from(repository.save(campaign));

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
            campaign = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Campaign", id));
        }

        if (campaign != null) return CampaignDTO.from(campaign);
        else return null;

    }

    public CampaignDTO findByIdAdmin(Long id) {
        Campaign campaign = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Campaign", id));
        return CampaignDTO.from(campaign);
    }

    public CampaignDTO findByIdAdminNull(Long id) {
        Campaign campaign = repository.findById(id).orElse(null);
        if (campaign == null) return null;
        else return CampaignDTO.from(campaign);
    }

    // DELETE BY ID
    public void delete(Long id) {
        Campaign campaign = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Campaign", id));
        try {
            if (campaign != null) {
                campaignAffiliateBusiness.deleteByCampaignID(id);
                Set<Media> mm = campaign.getMedias();
                List<Media> listaMedia = mm.stream().collect(Collectors.toList());
                listaMedia.forEach(media -> mediaBusiness.delete(media.getId()));
                campaign.getCampaignCategories().stream().forEach(campaignCategory -> campaignCategoryBusiness.delete(campaignCategory.getId()));
                campaign.getRevenueFactors().stream().forEach(revenueFactor -> revenueFactorBusiness.delete(revenueFactor.getId()));
                campaign.getCommissionCampaigns().stream().forEach(commission -> commissionBusiness.delete(campaign.getId(), commission.getId()));

                repository.deleteById(id);
            }
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    // SEARCH PAGINATED
    public Page<CampaignDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("name")));
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

        Double newBB = null;
        if (!campaign.getInitialBudget().equals(filter.getInitialBudget())) {
            newBB = campaign.getBudget() + (filter.getInitialBudget()) - campaign.getInitialBudget();
            campaign.setInitialBudget(filter.getInitialBudget());
        }

        mapper.map(filter, campaign);

        if (newBB != null)
            campaign.setBudget(newBB);

        // SET
        if (filter.getCookieId() != null)
            campaign.setCookie(cookieRepository.findById(filter.getCookieId()).orElseThrow(() -> new ElementCleveradException("Cookie", filter.cookieId)));
        if (filter.getCompanyId() != null)
            campaign.setAdvertiser(advertiserRepository.findById(filter.getCompanyId()).orElseThrow(() -> new ElementCleveradException("Advertiser", filter.companyId)));
        if (filter.getDealerId() != null)
            campaign.setDealer(dealerRepository.findById(filter.getDealerId()).orElseThrow(() -> new ElementCleveradException("Dealer", filter.dealerId)));
        if (filter.getPlannerId() != null)
            campaign.setPlanner(plannerRepository.findById(filter.getPlannerId()).orElseThrow(() -> new ElementCleveradException("Planner", filter.plannerId)));

        // SET Category - cancello precedenti
        campaignCategoryBusiness.deleteByCampaignID(id);

        // setto nuvoi
        if (filter.getCategoryList() != null && !filter.getCategoryList().isEmpty()) {
            Set<CampaignCategory> collect = filter.getCategoryList().stream().map(ss -> campaignCategoryBusiness.createEntity(new CampaignCategoryBusiness.BaseCreateRequest(id, ss))).collect(Collectors.toSet());
            campaign.setCampaignCategories(collect);
        }
        campaign.setLastModificationDate(LocalDateTime.now());

        return CampaignDTO.from(repository.save(campaign));
    }

    public CampaignDTO disable(Long id) {
        Campaign campaign = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Campaign", id));
        campaign.setStatus(false);
        campaign.setLastModificationDate(LocalDateTime.now());
        return CampaignDTO.from(repository.save(campaign));
    }

    public CampaignDTO updateBudget(Long campaignId, Double budget) {
        Campaign campaign = repository.findById(campaignId).orElseThrow(() -> new ElementCleveradException("Campaign", campaignId));
        campaign.setBudget(budget);
        return CampaignDTO.from(repository.save(campaign));
    }

    // TROVA LE CAMPAGNE DELL AFFILIATE
    public Page<CampaignDTO> getCampaigns(Long affiliateId) {
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

    public Page<CampaignDTO> getCampaignsActive(Long affiliateId, Pageable pageable) {

        List<Long> listaId = new ArrayList<>();
        affiliateRepository.findById(affiliateId).get().getCampaignAffiliates().stream().forEach(campaignAffiliate -> {
            listaId.add(campaignAffiliate.getCampaign().getId());
        });

        Filter request = new Filter();
        request.setStatus(true);
        request.setIdListIn(listaId);
        Page<Campaign> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CampaignDTO::from);
    }

    public Page<CampaignDTO> getCampaignsNot(Long affiliateId, Pageable pageable) {

        List<Long> listaId = new ArrayList<>();
        affiliateRepository.findById(affiliateId).get().getCampaignAffiliates().stream().forEach(campaignAffiliate -> {
            listaId.add(campaignAffiliate.getCampaign().getId());
        });

        Filter requestNot = new Filter();
        requestNot.setStatus(true);
        requestNot.setIdListNotIn(listaId);
        Page<Campaign> page = repository.findAll(getSpecification(requestNot), pageable);
        return page.map(CampaignDTO::from);
    }

    // TROVA LE CAMPAGNE DELL AFFILIATE filtrate per ID AFFIALIseTE DAL USER
    public Page<CampaignDTO> getCampaignsGuest(Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        Filter request = new Filter();
        Page<Campaign> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CampaignDTO::from);
    }

    public List<CampaignDTO> getCampaignsToDisable() {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc("name")));
        Filter request = new Filter();
        request.setStatus(true);
        request.setEndDateTo(LocalDate.now().plusDays(1));
        Page<Campaign> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CampaignDTO::from).toList();
    }

    public List<CampaignDTO> getEnabledCampaigns() {
        Filter request = new Filter();
        request.setStatus(true);
        Page<Campaign> page = repository.findAll(getSpecification(request), PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc("id"))));
        return page.map(CampaignDTO::from).toList();
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
                predicates.add(cb.like(cb.upper(root.get("name")), "%" + request.getName().toUpperCase() + "%"));
            }
            if (request.getShortDescription() != null) {
                predicates.add(cb.like(cb.upper(root.get("shortDescription")), "%" + request.getShortDescription().toUpperCase() + "%"));
            }
            if (request.getLongDescription() != null) {
                predicates.add(cb.like(cb.upper(root.get("longDescription")), "%" + request.getLongDescription().toUpperCase() + "%"));
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
                predicates.add(cb.greaterThanOrEqualTo(root.get("startDate"), request.getStartDateFrom()));
            }
            if (request.getStartDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("startDate"), (request.getStartDateTo().plus(1, ChronoUnit.DAYS))));
            }

            if (request.getEndDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("endDate"), (request.getEndDateFrom())));
            }
            if (request.getEndDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("endDate"), (request.getEndDateTo().plus(1, ChronoUnit.DAYS))));
            }

            if (request.getIdListIn() != null) {
                CriteriaBuilder.In<Long> inClause = cb.in(root.get("id"));
                for (Long id : request.getIdListIn()) {
                    inClause.value(id);
                }
                predicates.add(inClause);
            }

            if (request.getIdListNotIn() != null) {
                CriteriaBuilder.In<Long> inClauseNot = cb.in(root.get("id"));
                for (Long id : request.getIdListNotIn()) {
                    inClauseNot.value(id);
                }
                predicates.add(inClauseNot.not());
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
        private Double budget;
        private Double initialBudget;
        private String note;
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSz")
        private LocalDateTime startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSz")
        private LocalDateTime endDate;
        private String cap;
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
        private Double budget;
        private Double initialBudget;
        private Long cookieId;
        private String comissions;
        private List<Long> categoryList;
        private String note;
        private Instant creationDateFrom;
        private Instant creationDateTo;
        private Instant lastModificationDateFrom;
        private Instant lastModificationDateTo;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDateFrom;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDateTo;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDateFrom;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDateTo;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;
        private Long companyId;
        private Long plannerId;
        private Long dealerId;
        private String cap;
        private List<Long> idListIn;
        private List<Long> idListNotIn;
    }

}
