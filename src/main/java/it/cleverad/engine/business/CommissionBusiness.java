package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.Commission;
import it.cleverad.engine.persistence.model.service.TransactionCPC;
import it.cleverad.engine.persistence.model.service.TransactionCPL;
import it.cleverad.engine.persistence.model.service.TransactionCPM;
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

import jakarta.persistence.criteria.Predicate;
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
    private TransactionCPMBusiness transactionCPMBusiness;
    @Autowired
    private TransactionCPLBusiness transactionCPLBusiness;
    @Autowired
    private TransactionCPCBusiness transactionCPCBusiness;

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
        if(map.getDictionary().getId().equals(50L)) {
            map.setValue(map.getValue()/1000);
        }
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

    public Page<CommissionDTO> search(Filter request) {
        Page<Commission> page = repository.findAll(getSpecification(request), PageRequest.of(0, 100, Sort.by(Sort.Order.asc("id"))));
        return page.map(CommissionDTO::from);
    }

    // UPDATE
    public CommissionDTO update(Long id, Filter filter) {
        Commission com = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Commission", id));
        mapper.map(filter, com);

        com.setLastModificationDate(LocalDateTime.now());
        com.setCampaign(campaignRepository.findById(filter.campaignId).orElseThrow(() -> new ElementCleveradException("Campaign", filter.campaignId)));
        com.setDictionary(dictionaryRepository.findById(filter.dictionaryId).orElseThrow(() -> new ElementCleveradException("Dictionary", filter.dictionaryId)));

        //aggiorno transazioni :: cerco tutte le transazioni con quella commissione
        //CPC
        for (TransactionCPC tcps : com.getTransactionCPCS()) {
            transactionCPCBusiness.updateCPCValue(tcps.getClickNumber() * com.getValue(), tcps.getId());
        }
        //CPL
        for (TransactionCPL tcpl : com.getTransactionCPLS()) {
            transactionCPLBusiness.updateCPLValue(1 * com.getValue(), tcpl.getId());
        }
        //CPM
        for (TransactionCPM tcpm :  com.getTransactionCPMS()){
            transactionCPMBusiness.updateCPMValue(tcpm.getImpressionNumber() * com.getValue(), tcpm.getId());
        }

        return CommissionDTO.from(repository.save(com));
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
        Filter request = new Filter();
        request.setCampaignId(id);
        Page<Commission> page = repository.findAll(getSpecification(request), PageRequest.of(0, 100, Sort.by(Sort.Order.desc("value"))));
        return page.map(CommissionDTO::from);
    }

    public Page<CommissionDTO> getByIdCampaignAttive(Long id) {
        Filter request = new Filter();
        request.setCampaignId(id);
        request.setStatus(true);
        Page<Commission> page = repository.findAll(getSpecification(request), PageRequest.of(0, 100, Sort.by(Sort.Order.desc("value"))));
        return page.map(CommissionDTO::from);
    }

    public List<CommissionDTO> getCommissionToDisable() {
        Filter request = new Filter();
        request.setDisableDueDateTo(LocalDate.now().minusDays(1));
        request.setStatus(true);
        Page<Commission> page = repository.findAll(getSpecification(request), PageRequest.of(0, Integer.MAX_VALUE));
        return page.map(CommissionDTO::from).toList();
    }

    /**
     * ============================================================================================================
     **/
    private Specification<Commission> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }

            if (request.getName() != null) {
                predicates.add(cb.like(cb.upper(root.get("name")), "%" + request.getName().toUpperCase() + "%"));
            }

            if (request.getValue() != null) {
                predicates.add(cb.equal(root.get("value"), request.getValue()));
            }
            if (request.getDescription() != null) {
                predicates.add(cb.like(cb.upper(root.get("description")), "%" + request.getDescription().toUpperCase() + "%"));
            }
            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }
            if (request.getBase() != null) {
                predicates.add(cb.equal(root.get("base"), request.getBase()));
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
                predicates.add(cb.lessThanOrEqualTo(root.get("startDate"), (request.getStartDateTo().plusDays(1))));
            }
            if (request.getDueDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dueDate"), (request.getDueDateFrom())));
            }
            if (request.getDueDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dueDate"), (request.getDueDateTo().plusDays(1))));
            }
            if (request.getDisableDueDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dueDate"), request.getDisableDueDateTo()));
            }
            if (request.getAction() != null) {
                predicates.add(cb.equal(root.get("action"), request.getAction()));
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
        private Double value;
        private String description;
        private Boolean status;
        private Boolean base;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dueDate;

        private Long campaignId;
        private Long dictionaryId;

        private String action;
        private Double sale;
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
        private Double value;
        private String description;
        private Boolean status;
        private Boolean base;
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

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate disableDueDateTo;

        private String action;

        private Double sale;
    }

}