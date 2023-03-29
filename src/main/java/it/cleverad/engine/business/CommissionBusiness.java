package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.Commission;
import it.cleverad.engine.persistence.repository.service.CampaignRepository;
import it.cleverad.engine.persistence.repository.service.CommissionRepository;
import it.cleverad.engine.persistence.repository.service.DictionaryRepository;
import it.cleverad.engine.web.dto.CommissionDTO;
import it.cleverad.engine.web.dto.DictionaryDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class CommissionBusiness {

    @Autowired
    private CommissionRepository repository;

    @Autowired
    private DictionaryBusiness dictionaryBusiness;
    @Autowired
    private AffiliateChannelCommissionCampaignBusiness affiliateChannelCommissionCampaignBusiness;
    @Autowired
    private DictionaryRepository dictionaryRepository;
    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public CommissionDTO create(BaseCreateRequest request) {
        Commission map = mapper.map(request, Commission.class);
        map.setCreationDate(LocalDateTime.now());
        map.setLastModificationDate(LocalDateTime.now());
        map.setCampaign(campaignRepository.findById(request.campaignId).orElseThrow(() -> new ElementCleveradException("Campaign", request.campaignId)));
        map.setDictionary(dictionaryRepository.findById(request.dictionaryId).orElseThrow(() -> new ElementCleveradException("Dictionary", request.dictionaryId)));
        return CommissionDTO.from(repository.save(map));
    }

    // GET BY ID
    public CommissionDTO findById(Long id) {
        Commission commission = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Commission", id));
        return CommissionDTO.from(commission);
    }

    // DELETE BY ID
    public void delete(Long campaignId, Long commissionId) {
        affiliateChannelCommissionCampaignBusiness.deletebyCampaignAndCommission(campaignId, commissionId);
        repository.deleteById(commissionId);
    }

    // SEARCH PAGINATED
    public Page<CommissionDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<Commission> page = repository.findAll(getSpecification(request), pageable);

        return page.map(CommissionDTO::from);
    }

    // UPDATE
    public CommissionDTO update(Long id, Filter filter) {
        Commission ommission = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Commission", id));
        CommissionDTO campaignDTOfrom = CommissionDTO.from(ommission);

        mapper.map(filter, campaignDTOfrom);

        Commission mappedEntity = mapper.map(ommission, Commission.class);
        mappedEntity.setLastModificationDate(LocalDateTime.now());
        mappedEntity.setCampaign(campaignRepository.findById(filter.campaignId).orElseThrow(() -> new ElementCleveradException("Campaign", filter.campaignId)));
        mappedEntity.setDictionary(dictionaryRepository.findById(filter.dictionaryId).orElseThrow(() -> new ElementCleveradException("Dictionary", filter.dictionaryId)));
        mapper.map(campaignDTOfrom, mappedEntity);

        return CommissionDTO.from(repository.save(mappedEntity));
    }

    public CommissionDTO disable(Long id) {
        Commission commission = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Commission", id));
        commission.setStatus(false);
        return CommissionDTO.from(repository.save(commission));
    }

    public CommissionDTO enable(Long id) {
        Commission commission = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Commission", id));
        commission.setStatus(true);
        return CommissionDTO.from(repository.save(commission));
    }


    //  GET TIPI
    public Page<DictionaryDTO> getTypes() {
        return dictionaryBusiness.getTypeCommission();
    }

    // GET CCOMMISION BY CAMPAIGN
    public Page<CommissionDTO> getByIdCampaign(Long id) {
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Order.asc("id")));
        Filter request = new Filter();
        request.setCampaignId(id);
        Page<Commission> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CommissionDTO::from);
    }

    public Page<CommissionDTO> getByIdCampaignAttive(Long id) {
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Order.asc("id")));
        Filter request = new Filter();
        request.setCampaignId(id);
        request.setStatus(true);
        Page<Commission> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CommissionDTO::from);
    }


    public List<CommissionDTO> getCommissionToDisable() {
        Pageable pageable = PageRequest.of(0, 1000, Sort.by(Sort.Order.asc("id")));
        Filter request = new Filter();
        request.setDueDateTo(LocalDate.now());
        request.setStatus(true);
        Page<Commission> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CommissionDTO::from).toList();
    }


    /**
     * ============================================================================================================
     **/
    private Specification<Commission> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }

            if (request.getName() != null) {
                predicates.add(cb.like(root.get("name"), "%" + request.getName() + "%"));
            }

            if (request.getValue() != null) {
                predicates.add(cb.equal(root.get("value"), request.getValue()));
            }
            if (request.getDescription() != null) {
                predicates.add(cb.like(root.get("description"), "%" + request.getDescription() + "%"));
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

        private String name;
        private String value;
        private String description;
        private Boolean status;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dueDate;

        private Long campaignId;
        private Long dictionaryId;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dueDate;

        private String name;
        private String value;
        private String description;
        private Boolean status;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dueDateFrom;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dueDateTo;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDateFrom;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDateTo;
        private Long campaignId;
        private Long dictionaryId;

        private Instant creationDateFrom;
        private Instant creationDateTo;
        private Instant lastModificationDateFrom;
        private Instant lastModificationDateTo;
    }

}
