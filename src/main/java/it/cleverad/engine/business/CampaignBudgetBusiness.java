package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.Campaign;
import it.cleverad.engine.persistence.model.service.CampaignBudget;
import it.cleverad.engine.persistence.repository.service.CampaignBudgetRepository;
import it.cleverad.engine.persistence.repository.service.CampaignRepository;
import it.cleverad.engine.persistence.repository.service.DictionaryRepository;
import it.cleverad.engine.service.CampaignBudgetService;
import it.cleverad.engine.web.dto.CampaignBudgetDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import it.cleverad.engine.web.exception.PostgresDeleteCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.decimal4j.util.DoubleRounder;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional
public class CampaignBudgetBusiness {

    @Autowired
    private CampaignBudgetRepository repository;
    @Autowired
    private CampaignRepository campaignRepository;
    @Autowired
    private DictionaryRepository dictionaryRepository;
    @Autowired
    private CampaignBudgetService campaignBudgetService;
    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public CampaignBudgetDTO create(BaseCreateRequest request) {
        CampaignBudget map = mapper.map(request, CampaignBudget.class);
        Campaign campaign = campaignRepository.findById(request.campaignId).orElseThrow(() -> new ElementCleveradException("Campaign", request.campaignId));
        map.setCampaign(campaign);
        map.setAdvertiser(campaign.getAdvertiser());
        map.setPlanner(campaign.getPlanner());
        map.setDictionary(dictionaryRepository.findById(request.tipologiaId).orElseThrow(() -> new ElementCleveradException("Dictionary", request.tipologiaId)));
        map.setCanali(dictionaryRepository.findById(request.canaleId).orElseThrow(() -> new ElementCleveradException("Dictionary-canale", request.canaleId)));
        map.setStatus(true);
        map.setPayout(DoubleRounder.round(request.getBudgetIniziale() / request.capIniziale, 2));
        if (request.getScarto() == null)
            map.setScarto(0D);
        return CampaignBudgetDTO.from(repository.save(map));
    }

    // UPDATE
    public CampaignBudgetDTO update(Long id, Update filter) {
        CampaignBudget budget = repository.findById(id).orElseThrow(() -> new ElementCleveradException("CampaignBudget", id));
        mapper.map(filter, budget);

        if (filter.getCampaignId() != null) {
            Campaign campaign = campaignRepository.findById(filter.getCampaignId()).orElseThrow(() -> new ElementCleveradException("Campaign", filter.getCampaignId()));
            budget.setCampaign(campaign);
            budget.setAdvertiser(campaign.getAdvertiser());
            budget.setPlanner(campaign.getPlanner());
        }
        if (filter.getTipologiaId() != null)
            budget.setDictionary(dictionaryRepository.findById(filter.getTipologiaId()).orElseThrow(() -> new ElementCleveradException("Dictionary", filter.getTipologiaId())));
        if (filter.getCanaleId() != null)
            budget.setCanali(dictionaryRepository.findById(filter.getCanaleId()).orElseThrow(() -> new ElementCleveradException("Dictionary-canale", filter.getCanaleId())));

        budget.setStatus(true);

        return CampaignBudgetDTO.from(repository.save(budget));
    }

    public void recalculate(Long id) {
        campaignBudgetService.gestisciCampaignBudget(id);
    }

    // ENABLE E DISABLE
    public CampaignBudgetDTO enable(Long id) {
        CampaignBudget budget = repository.findById(id).orElseThrow(() -> new ElementCleveradException("CampaignBudget", id));
        budget.setStatus(true);
        return CampaignBudgetDTO.from(repository.save(budget));
    }

    public CampaignBudgetDTO disable(Long id) {
        CampaignBudget budget = repository.findById(id).orElseThrow(() -> new ElementCleveradException("CampaignBudget", id));
        budget.setStatus(false);
        return CampaignBudgetDTO.from(repository.save(budget));
    }

    // GET BY ID
    public CampaignBudgetDTO findById(Long id) {
        CampaignBudget entity = repository.findById(id).orElseThrow(() -> new ElementCleveradException("CampaignBudget", id));
        return CampaignBudgetDTO.from(entity);
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

    // SEARCH PAGINATED
    public Page<CampaignBudgetDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize());
        request.setStatus(true);
        Page<CampaignBudget> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CampaignBudgetDTO::from);
    }

    public Page<CampaignBudgetDTO> searchByCampaignID(Long campaignId, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize());
        Filter request = new Filter();
        request.setCampaignId(campaignId);
        Page<CampaignBudget> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CampaignBudgetDTO::from);
    }

    public Page<CampaignBudgetDTO> searchAttivi() {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("id", "status").ascending());
        Filter request = new Filter();
        request.setStatus(true);
        Page<CampaignBudget> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CampaignBudgetDTO::from);
    }

    /**
     * ============================================================================================================
     **/

    private Specification<CampaignBudget> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getAdvertiserId() != null) {
                predicates.add(cb.equal(root.get("advertiser").get("id"), request.getAdvertiserId()));
            }
            if (request.getCampaignId() != null) {
                predicates.add(cb.equal(root.get("campaign").get("id"), request.getCampaignId()));
            }
            if (request.getStartDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startDate"), request.getStartDateFrom()));
            }
            if (request.getStartDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("startDate"), request.getStartDateTo()));
            }
            if (request.getEndDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("endDate"), request.getEndDateFrom()));
            }
            if (request.getEndDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("endDate"), request.getEndDateTo()));
            }
            if (request.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startDate"), request.getStartDate()));
            }
            if (request.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("endDate"), request.getEndDate()));
            }
            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
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
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;
        private Long campaignId;
        private Long canaleId;
        private Boolean prenotato;
        private Long tipologiaId;
        private Integer capIniziale;
        private Double payout;
        private Double budgetIniziale;
        private Integer capErogato;
        private Double capPc;
        private Double budgetErogato;
        private Double commissioniErogate;
        private Double revenuePC;
        private Double revenue;
        private Double scarto;
        private Double budgetErogatoPS;
        private Double commissioniErogatePS;
        private Double revenuePCPS;
        private Double revenuePS;
        private Double revenueDay;
        private String materiali;
        private String note;
        private Integer capFatturabile;
        private Double fatturato;
        private Boolean status;
        private Long id;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
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
        private Long campaignId;
        private Long advertiserId;
        private Long plannerId;
        private Long canaleId;
        private Boolean prenotato;
        private Long tipologiaId;
        private Integer capIniziale;
        private Double payout;
        private Double budgetIniziale;
        private Integer capErogato;
        private Double capPc;
        private Double budgetErogato;
        private Double commissioniErogate;
        private Double revenuePC;
        private Double revenue;
        private Double scarto;
        private Double budgetErogatoPS;
        private Double commissioniErogatePS;
        private Double revenuePCPS;
        private Double revenuePS;
        private Double revenueDay;
        private String materiali;
        private String note;
        private Integer capFatturabile;
        private Double fatturato;
        private Long fatturaId;
        private Boolean status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Update {
        private Long tipologiaId;
        private Long campaignId;
        private Long canaleId;
        private Boolean prenotato;
        private Integer capIniziale;
        private Double payout;
        private Double budgetIniziale;
        private Integer capErogato;
        private Double capPc;
        private Double budgetErogato;
        private Double commissioniErogate;
        private Double revenuePC;
        private Double revenue;
        private Double scarto;
        private Double budgetErogatoPS;
        private Double commissioniErogatePS;
        private Double revenuePCPS;
        private Double revenuePS;
        private Double revenueDay;
        private String materiali;
        private String note;
        private Integer capFatturabile;
        private Double fatturato;
        private Boolean status;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;
        private Boolean statoFatturato;
        private Boolean statoPagato;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate invoiceDueDate;
    }

}