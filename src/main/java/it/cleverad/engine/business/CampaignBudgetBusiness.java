package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.CampaignAffiliate;
import it.cleverad.engine.persistence.model.service.CampaignBudget;
import it.cleverad.engine.persistence.repository.service.AdvertiserRepository;
import it.cleverad.engine.persistence.repository.service.CampaignBudgetRepository;
import it.cleverad.engine.persistence.repository.service.CampaignRepository;
import it.cleverad.engine.web.dto.CampaignAffiliateDTO;
import it.cleverad.engine.web.dto.CampaignBudgetDTO;
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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional
public class CampaignBudgetBusiness {

    @Autowired
    private CampaignBudgetRepository repository;
    @Autowired
    private AdvertiserRepository advertiserRepository;
    @Autowired
    private CampaignRepository campaignRepository;
    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public CampaignBudgetDTO create(BaseCreateRequest request) {
        CampaignBudget map = mapper.map(request, CampaignBudget.class);
        map.setAdvertiser(advertiserRepository.findById(request.advertiserId).orElseThrow(() -> new ElementCleveradException("Advertiser", request.advertiserId)));
        map.setCampaign(campaignRepository.findById(request.campaignId).orElseThrow(() -> new ElementCleveradException("Campaign", request.campaignId)));
        return CampaignBudgetDTO.from(repository.save(map));
    }

    // UPDATE
    public CampaignBudgetDTO update(Long id, Filter filter) {
        CampaignBudget entity = repository.findById(id).orElseThrow(() -> new ElementCleveradException("CampaignBudget", id));
        mapper.map(filter, entity);
        entity.setAdvertiser(advertiserRepository.findById(filter.advertiserId).orElseThrow(() -> new ElementCleveradException("Advertiser", filter.advertiserId)));
        entity.setCampaign(campaignRepository.findById(filter.campaignId).orElseThrow(() -> new ElementCleveradException("Campaign", filter.campaignId)));
        return CampaignBudgetDTO.from(repository.save(entity));
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

    public void deleteByCampaignID(Long id) {
        Filter request = new Filter();
        request.setCampaignId(id);
        Page<CampaignBudget> page = repository.findAll(getSpecification(request), PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("id"))));
        try {
            page.stream().forEach(campaignBudget -> repository.deleteById(campaignBudget.getId()));
        } catch (javax.validation.ConstraintViolationException ex) {
            throw ex;
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    // SEARCH PAGINATED
    public Page<CampaignBudgetDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<CampaignBudget> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CampaignBudgetDTO::from);
    }

    public Page<CampaignBudgetDTO> searchByCampaignID(Long campaignId ,Pageable pageableRequest)  {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Filter request = new Filter();
        request.setCampaignId(campaignId);
        Page<CampaignBudget> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CampaignBudgetDTO::from);
    }

    /**
     * ============================================================================================================
     **/

    private Specification<CampaignBudget> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
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

            if (request.getCreationDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("creationDate"), LocalDateTime.ofInstant(request.getCreationDateFrom(), ZoneOffset.UTC)));
            }
            if (request.getCreationDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("creationDate"), LocalDateTime.ofInstant(request.getCreationDateTo().plus(1, ChronoUnit.DAYS), ZoneOffset.UTC)));
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
        private Long advertiserId;
        private Long campaignId;
        private Integer capIniziale;
        private Integer capErogato;
        private Integer capFatturabile;

        private Double budgetIniziale;
        private Double budgetErogato;

        private Long fatturaId;

        private Double fatturato;
        private Double scarto;

        private String materiali;
        private String note;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;

        private Long advertiserId;
        private Long campaignId;

        private Instant creationDateFrom;
        private Instant creationDateTo;

    }

}
