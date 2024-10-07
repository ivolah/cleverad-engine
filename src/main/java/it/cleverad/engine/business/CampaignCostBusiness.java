package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.CampaignCost;
import it.cleverad.engine.persistence.repository.service.CampaignCostRepository;
import it.cleverad.engine.persistence.repository.service.CampaignRepository;
import it.cleverad.engine.persistence.repository.service.DictionaryRepository;
import it.cleverad.engine.web.dto.CampaignCostDTO;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional
public class CampaignCostBusiness {

    @Autowired
    private CampaignCostRepository repository;
    @Autowired
    private CampaignRepository campaignRepository;
    @Autowired
    private DictionaryRepository dictionaryRepository;
    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public CampaignCostDTO create(BaseCreateRequest request) {
        CampaignCost map = mapper.map(request, CampaignCost.class);
        map.setCampaign(campaignRepository.findById(request.campaignId).orElseThrow(() -> new ElementCleveradException("Campaign", request.campaignId)));
        map.setDictionary(dictionaryRepository.findById(request.typeId).orElseThrow(() -> new ElementCleveradException("Dictionary", request.typeId)));
        map.setCreationDate(LocalDateTime.now());
        map.setStatus(true);
        return CampaignCostDTO.from(repository.save(map));
    }

    // UPDATE
    public CampaignCostDTO update(Long id, BaseCreateRequest request) {
        CampaignCost budget = repository.findById(id).orElseThrow(() -> new ElementCleveradException("CampaignCost", id));
        mapper.map(request, budget);
        if (request.getCampaignId() != null)
            budget.setCampaign(campaignRepository.findById(request.getCampaignId()).orElseThrow(() -> new ElementCleveradException("Campaign", request.getCampaignId())));
        if (request.getTypeId() != null)
            budget.setDictionary(dictionaryRepository.findById(request.typeId).orElseThrow(() -> new ElementCleveradException("Dictionary", request.typeId)));
        return CampaignCostDTO.from(repository.save(budget));
    }

    // GET BY ID
    public CampaignCostDTO findById(Long id) {
        CampaignCost entity = repository.findById(id).orElseThrow(() -> new ElementCleveradException("CampaignCost", id));
        return CampaignCostDTO.from(entity);
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
    public Page<CampaignCostDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<CampaignCost> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CampaignCostDTO::from);
    }

    public Page<CampaignCostDTO> searchByCampaignID(Long campaignId, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Filter request = new Filter();
        request.setCampaignId(campaignId);
        Page<CampaignCost> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CampaignCostDTO::from);
    }

    public Page<CampaignCostDTO> searchByCampaignIdUnpaged(Long campaignId) {
        Filter request = new Filter();
        request.setCampaignId(campaignId);
        Page<CampaignCost> page = repository.findAll(getSpecification(request), Pageable.unpaged());
        return page.map(CampaignCostDTO::from);
    }

    public Page<CampaignCostDTO> searchByCampaignIdDate(Long campaignId, LocalDate from, LocalDate to) {
        Filter request = new Filter();
        request.setCampaignId(campaignId);
        request.setStartDateFrom(from);
        request.setEndDateTo(to);
        Page<CampaignCost> page = repository.findAll(getSpecification(request), Pageable.unpaged());
        return page.map(CampaignCostDTO::from);
    }

    /**
     * ============================================================================================================
     **/

    private Specification<CampaignCost> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
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
        private Long campaignId;
        private String nome;
        private Integer numero;
        private Double costo;
        private String note;
        private Long typeId;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private Long campaignId;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDateFrom;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDateTo;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDateFrom;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDateTo;
        private Long typeId;
    }


}