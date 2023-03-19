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
        Budget budget = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Budget", id));
        BudgetDTO budgetDTO = BudgetDTO.from(budget);
        mapper.map(filter, budgetDTO);

        Budget mappedEntity = mapper.map(budget, Budget.class);
        mappedEntity.setLastModificationDate(LocalDateTime.now());
        mappedEntity.setAffiliate(affiliateRepository.findById(filter.affiliateId).orElseThrow(() -> new ElementCleveradException("Affiliat", filter.affiliateId)));
        mappedEntity.setCampaign(campaignRepository.findById(filter.campaignId).orElseThrow(() -> new ElementCleveradException("Campaign", filter.campaignId)));
        mapper.map(budgetDTO, mappedEntity);

        return BudgetDTO.from(repository.save(mappedEntity));
    }

    public Page<BudgetDTO> getByIdCampaign(Long id) {
        Pageable pageable = PageRequest.of(0,1000, Sort.by(Sort.Order.asc("id")));
        Filter request = new Filter();
        request.setCampaignId(id);
        Page<Budget> page = repository.findAll(getSpecification(request), pageable);
        return page.map(BudgetDTO::from);
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
            if (request.isStatus()) {
                predicates.add(cb.equal(root.get("status"), request.isStatus()));
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
        private Long affiliateId;
        private Long campaignId;
        private Long budget;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private Date dueDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private Long affiliateId;
        private Long campaignId;
        private Long budget;
        private Date dueDate;        private Instant dueDateFrom;
        private Instant dueDateTo;
        private Instant startDateFrom;
        private Instant startDateTo;
        private boolean status;
        private Instant creationDateFrom;
        private Instant creationDateTo;
        private Instant lastModificationDateFrom;
        private Instant lastModificationDateTo;
    }

}
