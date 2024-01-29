package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.AffiliateBudget;
import it.cleverad.engine.persistence.repository.service.AffiliateRepository;
import it.cleverad.engine.persistence.repository.service.AffiliateBudgetRepository;
import it.cleverad.engine.persistence.repository.service.CampaignRepository;
import it.cleverad.engine.web.dto.AffiliateBudgetDTO;
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
public class AffiliateBudgetBusiness {

    @Autowired
    private AffiliateRepository affiliateRepository;
    @Autowired
    private CampaignRepository campaignRepository;
    @Autowired
    private AffiliateBudgetRepository repository;
    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public AffiliateBudgetDTO create(BaseCreateRequest request) {
        AffiliateBudget map = mapper.map(request, AffiliateBudget.class);
        map.setStatus(true);
        map.setInitialBudget(request.getBudget());
        map.setInitialCap(request.getCap());
        map.setAffiliate(affiliateRepository.findById(request.affiliateId).orElseThrow(() -> new ElementCleveradException("Affiliate", request.affiliateId)));
        map.setCampaign(campaignRepository.findById(request.campaignId).orElseThrow(() -> new ElementCleveradException("Campaign", request.campaignId)));
        return AffiliateBudgetDTO.from(repository.save(map));
    }

    // GET BY ID
    public AffiliateBudgetDTO findById(Long id) {
        AffiliateBudget affiliateBudget = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Budget", id));
        return AffiliateBudgetDTO.from(affiliateBudget);
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
    public Page<AffiliateBudgetDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        Page<AffiliateBudget> page = repository.findAll(getSpecification(request), pageable);
        return page.map(AffiliateBudgetDTO::from);
    }

    // UPDATE
    public AffiliateBudgetDTO update(Long id, Filter filter) {
        AffiliateBudget affiliateBudget = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Budget", id));
        Double newBudget = null;
        if (!affiliateBudget.getInitialBudget().equals(filter.getInitialBudget())) {
            newBudget = affiliateBudget.getBudget() + filter.getInitialBudget() - affiliateBudget.getInitialBudget();
            log.info(newBudget + "");
        }
        mapper.map(filter, affiliateBudget);
        affiliateBudget.setBudget(newBudget);
        affiliateBudget.setLastModificationDate(LocalDateTime.now());
        affiliateBudget.setAffiliate(affiliateRepository.findById(filter.affiliateId).orElseThrow(() -> new ElementCleveradException("Affiliate", filter.affiliateId)));
        affiliateBudget.setCampaign(campaignRepository.findById(filter.campaignId).orElseThrow(() -> new ElementCleveradException("Campaign", filter.campaignId)));
        return AffiliateBudgetDTO.from(repository.save(affiliateBudget));
    }

    public AffiliateBudgetDTO disable(Long id) {
        AffiliateBudget affiliateBudget = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Budget", id));
        affiliateBudget.setLastModificationDate(LocalDateTime.now());
        affiliateBudget.setStatus(false);
        return AffiliateBudgetDTO.from(repository.save(affiliateBudget));
    }

    public AffiliateBudgetDTO updateBudget(Long id, Double budgetValue) {
        AffiliateBudget affiliateBudget = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Budget", id));
        affiliateBudget.setLastModificationDate(LocalDateTime.now());
        affiliateBudget.setBudget(budgetValue);
        return AffiliateBudgetDTO.from(repository.save(affiliateBudget));
    }

    public AffiliateBudgetDTO updateCap(Long id, Integer cap) {
        AffiliateBudget affiliateBudget = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Budget", id));
        affiliateBudget.setLastModificationDate(LocalDateTime.now());
        affiliateBudget.setCap(cap);
        return AffiliateBudgetDTO.from(repository.save(affiliateBudget));
    }

    public Page<AffiliateBudgetDTO> getByIdCampaign(Long id) {
        Filter request = new Filter();
        request.setCampaignId(id);
        Page<AffiliateBudget> page = repository.findAll(getSpecification(request), PageRequest.of(0, Integer.MAX_VALUE));
        return page.map(AffiliateBudgetDTO::from);
    }

    public Page<AffiliateBudgetDTO> getByIdCampaignAndIdAffiliate(Long idCampaign, Long idAffilaite) {
        Filter request = new Filter();
        request.setCampaignId(idCampaign);
        request.setAffiliateId(idAffilaite);
        request.setStatus(true);
        Page<AffiliateBudget> page = repository.findAll(getSpecification(request), PageRequest.of(0, Integer.MAX_VALUE));
        return page.map(AffiliateBudgetDTO::from);
    }

    public List<AffiliateBudgetDTO> getBudgetToDisable() {
        Filter request = new Filter();
        request.setDisableDueDateTo(LocalDate.now().plusDays(1));
        request.setStatus(true);
        Page<AffiliateBudget> page = repository.findAll(getSpecification(request), PageRequest.of(0, Integer.MAX_VALUE));
        return page.map(AffiliateBudgetDTO::from).toList();
    }

    /**
     * ============================================================================================================
     **/

    private Specification<AffiliateBudget> getSpecification(Filter request) {
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

            if (request.getDisableDueDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dueDate"), request.getDisableDueDateTo()));
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
        private Integer cap;
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
        private Integer cap;
        private Integer initialCap;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate disableDueDateTo;

    }

}