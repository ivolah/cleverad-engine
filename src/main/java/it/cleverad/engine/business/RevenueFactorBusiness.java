package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.RevenueFactor;
import it.cleverad.engine.persistence.repository.service.CampaignRepository;
import it.cleverad.engine.persistence.repository.service.DictionaryRepository;
import it.cleverad.engine.persistence.repository.service.RevenueFactorRepository;
import it.cleverad.engine.web.dto.DictionaryDTO;
import it.cleverad.engine.web.dto.RevenueFactorDTO;
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
import java.util.List;

@Slf4j
@Component
@Transactional
public class RevenueFactorBusiness {

    @Autowired
    private RevenueFactorRepository repository;

    @Autowired
    private Mapper mapper;

    @Autowired
    private DictionaryBusiness dictionaryBusiness;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private DictionaryRepository dictionaryRepository;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public RevenueFactorDTO create(BaseCreateRequest request) {
        RevenueFactor map = mapper.map(request, RevenueFactor.class);
        map.setCampaign(campaignRepository.findById(request.campaignId).orElseThrow(() -> new ElementCleveradException("Campaign", request.campaignId)));
        map.setDictionary(dictionaryRepository.findById(request.dictionaryId).orElseThrow());
        map.setCreationDate(LocalDateTime.now());
        map.setLastModificationDate(LocalDateTime.now());
        map.setStatus(true);
        return RevenueFactorDTO.from(repository.save(map));
    }

    // GET BY ID
    public RevenueFactorDTO findById(Long id) {
        RevenueFactor entity = repository.findById(id).orElseThrow(() -> new ElementCleveradException("RevenueFactor", id));
        return RevenueFactorDTO.from(entity);
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
    public Page<RevenueFactorDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<RevenueFactor> page = repository.findAll(getSpecification(request), pageable);

        return page.map(RevenueFactorDTO::from);
    }

    // UPDATE
    public RevenueFactorDTO update(Long id, Filter filter) {
        RevenueFactor revenueFactor = repository.findById(id).orElseThrow(() -> new ElementCleveradException("RevenueFactor", id));
        mapper.map(filter, revenueFactor);

        revenueFactor.setCampaign(campaignRepository.findById(filter.campaignId).orElseThrow());
        revenueFactor.setDictionary(dictionaryRepository.findById(filter.dictionaryId).orElseThrow());
        revenueFactor.setLastModificationDate(LocalDateTime.now());

        return RevenueFactorDTO.from(repository.save(revenueFactor));
    }

    public RevenueFactorDTO disable(Long id) {
        RevenueFactor ommission = repository.findById(id).orElseThrow(() -> new ElementCleveradException("RevenueFactor", id));
        ommission.setLastModificationDate(LocalDateTime.now());
        ommission.setStatus(false);
        return RevenueFactorDTO.from(repository.save(ommission));
    }

    public Page<RevenueFactorDTO> getbyIdCampaign(Long id, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Filter request = new Filter();
        request.setCampaignId(id);
        Page<RevenueFactor> page = repository.findAll(getSpecification(request), pageable);
        return page.map(RevenueFactorDTO::from);
    }

    public RevenueFactor getbyIdCampaign(Long id) {
        Filter request = new Filter();
        request.setCampaignId(id);
        RevenueFactor revenueFactor = repository.findAll(getSpecification(request)).stream().findFirst().get();
        return revenueFactor;
    }

    public RevenueFactor getbyIdCampaignAndDictionrayId(Long campId, Long dictId) {
        Filter request = new Filter();
        request.setCampaignId(campId);
        request.setStatus(true);
        request.setDictionaryId(dictId);
        RevenueFactor revenueFactor = repository.findAll(getSpecification(request)).stream().findFirst().orElse(null);
        return revenueFactor;
    }


    //  GET TIPI
    public Page<DictionaryDTO> getTypes() {
        return dictionaryBusiness.getTypeCommission();
    }

    public List<RevenueFactorDTO> getRevenueToDisable() {
        Pageable pageable = PageRequest.of(0, 1000, Sort.by(Sort.Order.asc("id")));
        Filter request = new Filter();
        request.setDueDateTo(LocalDate.now());
        request.setStatus(true);
        Page<RevenueFactor> page = repository.findAll(getSpecification(request), pageable);
        return page.map(RevenueFactorDTO::from).toList();
    }

    /**
     * ============================================================================================================
     **/
    private Specification<RevenueFactor> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }

            if (request.getCampaignId() != null) {
                predicates.add(cb.equal(root.get("campaign").get("id"), request.getCampaignId()));
            }
            if (request.getDictionaryId() != null) {
                predicates.add(cb.equal(root.get("dictionary").get("id"), request.getDictionaryId()));
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
        private Double revenue;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dueDate;
        private Boolean status;
        private Long campaignId;
        private Long dictionaryId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dueDate;

        private Long id;
        private Double revenue;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dueDateFrom;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dueDateTo;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDateFrom;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDateTo;
        private Boolean status;

        private Long campaignId;
        private Long dictionaryId;

        private Instant creationDateFrom;
        private Instant creationDateTo;
        private Instant lastModificationDateFrom;
        private Instant lastModificationDateTo;
    }

}
