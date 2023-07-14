package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.Budget;
import it.cleverad.engine.persistence.repository.service.AffiliateRepository;
import it.cleverad.engine.persistence.repository.service.BudgetRepository;
import it.cleverad.engine.persistence.repository.service.CampaignRepository;
import it.cleverad.engine.web.dto.BudgetDTO;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@Transactional
public class BudgetBusiness {

    @Autowired
    private AffiliateRepository affiliateRepository;
    @Autowired
    private CampaignRepository campaignRepository;
    @Autowired
    private AffiliateBusiness affiliateBusiness;
    @Autowired
    private BudgetRepository repository;
    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public BudgetDTO create(BaseCreateRequest request) {
        Budget map = mapper.map(request, Budget.class);
        map.setStatus(true);
        map.setInitialBudget(request.getBudget());
        map.setAffiliate(affiliateRepository.findById(request.affiliateId).orElseThrow(() -> new ElementCleveradException("Affiliat", request.affiliateId)));
        map.setCampaign(campaignRepository.findById(request.campaignId).orElseThrow(() -> new ElementCleveradException("Campaign", request.campaignId)));
        return BudgetDTO.from(repository.save(map));
    }

    // GET BY ID
    public BudgetDTO findById(Long id) {
        Budget budget = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Budget", id));
        return BudgetDTO.from(budget);
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
    public Page<BudgetDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<Budget> page = repository.findAll(getSpecification(request), pageable);
        return page.map(BudgetDTO::from);
    }

    // UPDATE
    public BudgetDTO update(Long id, Filter filter) {

        log.info(">>>> " + filter);
        Budget budget = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Budget", id));

        Double newBudget = null;
        if (!budget.getInitialBudget().equals(filter.getInitialBudget())) {
            newBudget = budget.getBudget() + filter.getInitialBudget() - budget.getInitialBudget();
            log.info(newBudget + "");
        }

        mapper.map(filter, budget);

        budget.setBudget(newBudget);
        budget.setLastModificationDate(LocalDateTime.now());
        budget.setAffiliate(affiliateRepository.findById(filter.affiliateId).orElseThrow(() -> new ElementCleveradException("Affiliate", filter.affiliateId)));
        budget.setCampaign(campaignRepository.findById(filter.campaignId).orElseThrow(() -> new ElementCleveradException("Campaign", filter.campaignId)));

        return BudgetDTO.from(repository.save(budget));
    }

    public BudgetDTO disable(Long id) {
        Budget budget = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Budget", id));
        budget.setLastModificationDate(LocalDateTime.now());
        budget.setStatus(false);
        return BudgetDTO.from(repository.save(budget));
    }

    public BudgetDTO updateBudget(Long id, Double budgetValue) {
        Budget budget = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Budget", id));
        budget.setLastModificationDate(LocalDateTime.now());
        budget.setBudget(budgetValue);
        return BudgetDTO.from(repository.save(budget));
    }

    public Page<BudgetDTO> getByIdCampaign(Long id) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("id")));
        Filter request = new Filter();
        request.setCampaignId(id);
        Page<Budget> page = repository.findAll(getSpecification(request), pageable);
        return page.map(BudgetDTO::from);
    }

    public Page<BudgetDTO> getByIdCampaignAndIdAffiliate(Long idCampaign, Long idAffilaite) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("id")));
        Filter request = new Filter();
        request.setCampaignId(idCampaign);
        request.setAffiliateId(idAffilaite);
        request.setStatus(true);
        Page<Budget> page = repository.findAll(getSpecification(request), pageable);
        return page.map(BudgetDTO::from);
    }

    public List<BudgetDTO> getBudgetToDisable() {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("id")));
        Filter request = new Filter();
        request.setDueDateTo(LocalDate.now());
        request.setStatus(true);
        Page<Budget> page = repository.findAll(getSpecification(request), pageable);
        return page.map(BudgetDTO::from).toList();
    }

    /**
     * ============================================================================================================
     **/
    private Specification<Budget> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getAffiliateId() != null) {
                predicates.add(cb.equal(root.get("affiliate").get("id"), request.getAffiliateId()));
            }
            if (request.getCampaignId() != null) {
                predicates.add(cb.equal(root.get("campaign").get("id"), request.getCampaignId()));
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

            if (request.getDueDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dueDate"), (request.getDueDateFrom())));
            }
            if (request.getDueDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dueDate"), (request.getDueDateTo().plus(1, ChronoUnit.DAYS))));
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
        private Long affiliateId;
        private Long campaignId;
        private Double budget;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private Date dueDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Filter {
        private Long id;
        private Long affiliateId;
        private Long campaignId;
        private Double budget;
        private Double initialBudget;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private Date dueDate;

        private Boolean status;
        private Instant creationDateFrom;
        private Instant creationDateTo;
        private Instant lastModificationDateFrom;
        private Instant lastModificationDateTo;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dueDateFrom;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dueDateTo;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDateFrom;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDateTo;
    }

}