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
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.lang.reflect.Field;
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

    private static double calculateMedian(Page<CampaignBudget> page, String fieldName) {

        double[] pp = page.getContent().stream().mapToDouble(entity -> {
            try {
                Field field = CampaignBudget.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value = field.get(entity);
                if (value instanceof Double) {
                    return (Double) value;
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            return 0.0;
        }).toArray();

        Double sum = 0D;
        for (double value : pp) {
            sum += value;
        }
        // log.info("SS {} + {} - {}", sum, pp.length, overallPercentage);

        return DoubleRounder.round(sum / pp.length, 2);
    }

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
        map.setBudgetIniziale(DoubleRounder.round(request.getPayout() * request.capIniziale, 2));
        if (request.getScarto() == null) map.setScarto(0D);
        return CampaignBudgetDTO.from(repository.save(map));
    }

    // UPDATE
    public CampaignBudgetDTO update(Long id, Update filter) {
        CampaignBudget budget = repository.findById(id).orElseThrow(() -> new ElementCleveradException("CampaignBudget", id));
        mapper.map(filter, budget);
        budget.setStatus(true);
        budget.setBudgetIniziale(DoubleRounder.round(filter.getPayout() * filter.capIniziale, 2));
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
        budget.setFileCampaignBudgetOrders(null);
        budget.setFileCampaignBudgetInvoices(null);
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

        Sort sort = Sort.by(Sort.Order.desc("campaignStatus"), Sort.Order.asc("advertiserName"), Sort.Order.asc("plannerName"), Sort.Order.asc("campaignName"));

        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), sort);
        request.setStatus(true);
        Page<CampaignBudget> page = repository.findAll(getSpecification(request), pageable);

        CampaignBudget calcolato = calculateTotalsForDoubleFields(page);
        List<CampaignBudget> newListWithAdditionalElement = new ArrayList<>(page.getContent());
        newListWithAdditionalElement.add(calcolato);
        Page<CampaignBudget> newPage = new PageImpl<>(newListWithAdditionalElement, pageable, page.getTotalElements() + 1);

        return newPage.map(CampaignBudgetDTO::from);
    }

    private CampaignBudget calculateTotalsForDoubleFields(Page<CampaignBudget> page) {
        CampaignBudget campaignBudget = new CampaignBudget();
        campaignBudget.setCapIniziale(getIntegerFieldValue(page, "capIniziale"));
        campaignBudget.setCapErogato(getIntegerFieldValue(page, "capErogato"));

        // versione originale
//        if (campaignBudget.getCapErogato() != 0D) {
//            campaignBudget.setCapPc(DoubleRounder.round((campaignBudget.getCapErogato() * 100) / campaignBudget.getCapIniziale(), 2));
//        } else campaignBudget.setCapPc(0D);

        campaignBudget.setPayout(calculateMedian(page, "payout"));
        campaignBudget.setBudgetIniziale(getFieldValue(page, "budgetIniziale"));
        campaignBudget.setBudgetErogato(getFieldValue(page, "budgetErogato"));

        // hack per manenere stesso campo ma calcolare % budget
        if (campaignBudget.getBudgetErogato() != 0D) {
            campaignBudget.setCapPc(DoubleRounder.round((campaignBudget.getBudgetErogato() * 100) / campaignBudget.getBudgetIniziale(), 2));
        } else campaignBudget.setCapPc(0D);

        campaignBudget.setCommissioniErogate(getFieldValue(page, "commissioniErogate"));
        campaignBudget.setRevenue(getFieldValue(page, "revenue"));
        if (campaignBudget.getBudgetErogato() != 0D) {
            campaignBudget.setRevenuePC(DoubleRounder.round((campaignBudget.getRevenue() / campaignBudget.getBudgetErogato()) * 100, 2));
        } else campaignBudget.setRevenuePC(0D);

        campaignBudget.setScarto(getFieldValue(page, "scarto"));
        campaignBudget.setBudgetErogatops(getFieldValue(page, "budgetErogatops"));
        campaignBudget.setCommissioniErogateps(getFieldValue(page, "commissioniErogateps"));
        campaignBudget.setRevenuePS(getFieldValue(page, "revenuePS"));
        if (campaignBudget.getBudgetErogatops() != 0D) {
            campaignBudget.setRevenuePCPS(DoubleRounder.round((campaignBudget.getRevenuePS() / campaignBudget.getBudgetErogatops()) * 100, 2));
        } else campaignBudget.setRevenuePCPS(0D);
        campaignBudget.setRevenueDay(DoubleRounder.round(campaignBudget.getRevenue() / LocalDate.now().getDayOfMonth(), 2));
        campaignBudget.setFatturato(getFieldValue(page, "fatturato"));
        campaignBudget.setStatus(null);
        campaignBudget.setPrenotato(null);

        return campaignBudget;
    }

    // Helper method to get field value by field name using Reflection
    private double getFieldValue(Page<CampaignBudget> page, String fieldName) {
        return page.getContent().stream().mapToDouble(entity -> {
            Field field = null;
            try {
                field = CampaignBudget.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value = field.get(entity);
                if (value instanceof Double) {
                    return (Double) value;
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            return 0.0;
        }).sum();
    }

    private Integer getIntegerFieldValue(Page<CampaignBudget> page, String fieldName) {
        return page.getContent().stream().mapToInt(entity -> {
            Field field = null;
            try {
                field = CampaignBudget.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value = field.get(entity);
                if (value instanceof Integer) {
                    return (Integer) value;
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            return 0;
        }).sum();
    }

    public Page<CampaignBudgetDTO> searchByCampaignID(Long campaignId, Pageable pageableRequest) {
        Sort sort = Sort.by(Sort.Order.desc("campaignStatus"), Sort.Order.asc("advertiserName"), Sort.Order.asc("plannerName"), Sort.Order.asc("campaignName"));
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), sort);
        Filter request = new Filter();
        request.setCampaignId(campaignId);
        Page<CampaignBudget> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CampaignBudgetDTO::from);
    }

    public Page<CampaignBudgetDTO> searchAttivi() {
        Sort sort = Sort.by(Sort.Order.desc("campaignStatus"), Sort.Order.asc("advertiserName"), Sort.Order.asc("plannerName"), Sort.Order.asc("campaignName"));
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, sort);
        Filter request = new Filter();
        request.setStatus(true);
        Page<CampaignBudget> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CampaignBudgetDTO::from);
    }

    public Page<CampaignBudgetDTO> searchByCampaignAndDate(Long campaignId, LocalDate date) {
        Filter request = new Filter();
        request.setCampaignId(campaignId);
        request.setStartDateFrom(date);
        request.setEndDateTo(date);
        Page<CampaignBudget> page = repository.findAll(getSpecification(request), PageRequest.ofSize(Integer.MAX_VALUE));
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
        private Integer capVolume;
        private Double capPc;
        private Double budgetErogato;
        private Double commissioniErogate;
        private Double revenuePC;
        private Double revenue;
        private Double scarto;
        private Double budgetErogatops;
        private Double commissioniErogateps;
        private Double revenuePCPS;
        private Double revenuePS;
        private Double revenueDay;
        private String materiali;
        private String note;
        private Integer capFatturabile;
        private Double fatturato;
        private Boolean status;
        private Long id;
        private Integer volume;
        private LocalDate volumeDate;
        private Integer volumeDelta;
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
        private Integer capVolume;
        private Double capPc;
        private Double budgetErogato;
        private Double commissioniErogate;
        private Double revenuePC;
        private Double revenue;
        private Double scarto;
        private Double budgetErogatops;
        private Double commissioniErogateps;
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
        private Boolean prenotato;
        private Integer capIniziale;
        private Double payout;
        private Double scarto;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;
        private Boolean statoFatturato;
        private Boolean statoPagato;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate invoiceDueDate;
        private Integer volume;
        private LocalDate volumeDate;
    }

}