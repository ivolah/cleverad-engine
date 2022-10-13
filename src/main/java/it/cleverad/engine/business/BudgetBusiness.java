package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.Budget;
import it.cleverad.engine.persistence.repository.BudgetRepository;
import it.cleverad.engine.web.dto.AffiliateBudgetCampaignDTO;
import it.cleverad.engine.web.dto.BudgetDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional
public class BudgetBusiness {

    @Autowired
    AffiliateBudgetCampaignBusiness affiliateBudgetCampaignBusiness;
    @Autowired
    AffiliateBusiness affiliateBusiness;
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
        map.setCreationDate(LocalDateTime.now());
        map.setLastModificationDate(LocalDateTime.now());
        return BudgetDTO.from(repository.save(map));
    }

    // GET BY ID
    public BudgetDTO findById(Long id) {
        Budget budget = repository.findById(id).orElseThrow(() -> new ElementCleveradException(id));
        return BudgetDTO.from(budget);
    }

    // DELETE BY ID
    public void delete(Long id) {
        repository.deleteById(id);
    }

    // SEARCH PAGINATED
    public Page<BudgetDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<Budget> page = repository.findAll(getSpecification(request), pageable);

        return page.map(BudgetDTO::from);
    }

    // UPDATE
    public BudgetDTO update(Long id, Filter filter) {
        try {
            Budget budget = repository.findById(id).orElseThrow(() -> new ElementCleveradException(id));
            BudgetDTO budgetDTO = BudgetDTO.from(budget);

            mapper.map(filter, budgetDTO);

            Budget mappedEntity = mapper.map(budget, Budget.class);
            mappedEntity.setLastModificationDate(LocalDateTime.now());
            mapper.map(budgetDTO, mappedEntity);

            return BudgetDTO.from(repository.save(mappedEntity));
        } catch (Exception e) {
            log.error("Errore in update", e);
            return null;
        }
    }

    public Page<BudgetDTO> getByIdCampaign(Long id) {
        AffiliateBudgetCampaignBusiness.Filter reques = new AffiliateBudgetCampaignBusiness.Filter();
        reques.setCampaignId(id);
        Page<AffiliateBudgetCampaignDTO> ll = affiliateBudgetCampaignBusiness.search(reques, PageRequest.of(0, 100, Sort.by(Sort.Order.asc("id"))));

        Page<BudgetDTO> listaB = new PageImpl<>(ll.stream().map(affiliateBudgetCampaignDTO -> {
            BudgetDTO budgetDTO = new BudgetDTO();
            budgetDTO = this.findById(affiliateBudgetCampaignDTO.getBudgetId());
            try {
                budgetDTO.setAffiliateName(affiliateBusiness.findById(affiliateBudgetCampaignDTO.getAffiliateId()).getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return budgetDTO;
        }).collect(Collectors.toList()));

        return listaB;
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
            if (request.getIdAffiliate() != null) {
                predicates.add(cb.equal(root.get("idAffiliate"), request.getIdAffiliate()));
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
        private Long idAffiliate;
        private Long budget;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private Date dueDate;

        private boolean status;
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime creationDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime lastModificationDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private Long idAffiliate;
        private Long budget;
        private Date dueDate;

        private boolean status;
        private Instant creationDateFrom;
        private Instant creationDateTo;
        private Instant lastModificationDateFrom;
        private Instant lastModificationDateTo;
    }

}
