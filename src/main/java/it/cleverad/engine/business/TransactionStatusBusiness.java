package it.cleverad.engine.business;

import it.cleverad.engine.persistence.model.service.ViewTransactionStatus;
import it.cleverad.engine.persistence.repository.service.ViewTransactionStatusRepository;
import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.web.dto.TransactionStatusDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@Transactional
public class TransactionStatusBusiness {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private ViewTransactionStatusRepository repository;

    /**
     * ============================================================================================================
     **/


    // GET BY ID
    public TransactionStatusDTO findById(Long id) {
        ViewTransactionStatus transaction = null;
        if (jwtUserDetailsService.getRole().equals("Admin")) {
            transaction = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Transaction Status", id));
        }
        return TransactionStatusDTO.from(transaction);
    }

    // SEARCH PAGINATED
    public Page<TransactionStatusDTO> searchPrefiltrato(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("dateTime")));
        if (!jwtUserDetailsService.getRole().equals("Admin"))
            request.setAffiliateId(jwtUserDetailsService.getAffiliateID());
        Page<ViewTransactionStatus> page = repository.findAll(getSpecification(request), pageable);

        return page.map(TransactionStatusDTO::from);
    }

    /**
     * ============================================================================================================
     **/

    private Specification<ViewTransactionStatus> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getPayoutReference() != null) {
                predicates.add(cb.equal(root.get("payoutReference"), request.getPayoutReference()));
            }
            if (request.getValue() != null) {
                predicates.add(cb.equal(root.get("value"), request.getValue()));
            }
            if (request.getAffiliateId() != null) {
                predicates.add(cb.equal(root.get("affiliateId"), request.getAffiliateId()));
            }
            if (request.getCampaignId() != null) {
                predicates.add(cb.equal(root.get("campaignId"), request.getCampaignId()));
            }
            if (request.getChannelId() != null) {
                predicates.add(cb.equal(root.get("channelId"), request.getChannelId()));
            }
            if (request.getCommissionId() != null) {
                predicates.add(cb.equal(root.get("commissionId"), request.getCommissionId()));
            }
            if (request.getPayoutId() != null) {
                predicates.add(cb.equal(root.get("payoutId"), request.getPayoutId()));
            }
            if (request.getWalletId() != null) {
                predicates.add(cb.equal(root.get("walletId"), request.getWalletId()));
            }
            if (request.getMediaId() != null) {
                predicates.add(cb.equal(root.get("mediaId"), request.getMediaId()));
            }
            if (request.getClickNumber() != null) {
                predicates.add(cb.equal(root.get("clickNumber"), request.getClickNumber()));
            }
            if (request.getData() != null) {
                predicates.add(cb.like(cb.upper(root.get("data")), "%" + request.getData().toUpperCase() + "%"));
            }
            if (request.getTipo() != null) {
                predicates.add(cb.equal(root.get("tipo"), request.getTipo()));
            }
            if (request.getDictionaryId() != null) {
                predicates.add(cb.equal(root.get("dictionaryId"), request.getDictionaryId()));
            }
            if (request.getStatusId() != null) {
                predicates.add(cb.equal(root.get("statusId"), request.getStatusId()));
            }

            if (request.getPayoutPresent() != null) {
                predicates.add(cb.equal(root.get("payoutPresent"), request.getPayoutPresent()));
            }

            if (request.getValueNotZero() != null && request.getValueNotZero()) {
                predicates.add(cb.notEqual(root.get("value"), "0"));
            }


            if (request.getInDictionaryId() != null) {
                CriteriaBuilder.In<Long> inClause = cb.in(root.get("dictionaryId"));
                for (Long id : request.getInDictionaryId()) {
                    inClause.value(id);
                }
                predicates.add(inClause);
            }

            if (request.getInStausId() != null) {
                CriteriaBuilder.In<Long> inClause = cb.in(root.get("statusId"));
                for (Long id : request.getInStausId()) {
                    inClause.value(id);
                }
                predicates.add(inClause);
            }

            if (request.getNotInDictionaryId() != null) {
                CriteriaBuilder.In<Long> inClauseNot = cb.in(root.get("dictionaryId"));
                for (Long id : request.getNotInDictionaryId()) {
                    inClauseNot.value(id);
                }
                predicates.add(inClauseNot.not());
            }

            if (request.getNotInStausId() != null) {
                CriteriaBuilder.In<Long> inClauseNot = cb.in(root.get("statusId"));
                for (Long id : request.getNotInStausId()) {
                    inClauseNot.value(id);
                }
                predicates.add(inClauseNot.not());
            }

            if (request.getCreationDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dateTime"), request.getCreationDateFrom()));
            }
            if (request.getCreationDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dateTime"), request.getCreationDateTo()));
            }

            if (request.getDataList() != null && request.getDataList().length()>3) {
                CriteriaBuilder.In<String> inClause = cb.in(root.get("data"));
                Arrays.stream(request.getDataList().split(",")).distinct().forEach(s -> {
                    inClause.value(StringUtils.trim(s));
                });
                predicates.add(inClause);
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
    public static class Filter {

        private Long id;
        private String tipo;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate creationDateFrom;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate creationDateTo;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateTimeFrom;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateTimeTo;

        private Long statusId;
        private String statusName;
        private Long dictionaryId;
        private String dictionaryName;

        private Long affiliateId;
        private String affiliateName;
        private Long channelId;
        private String channelName;
        private Long campaignId;
        private String campaignName;
        private Long mediaId;
        private String mediaName;
        private Long commissionId;
        private String commissionName;
        private Double commissionValue;

        private Double value;
        private Long revenueId;
        private Long revenue;

        private Long clickNumber;
        private Long impressionNumber;
        private Long leadNumber;
        private String data;
        private Long walletId;
        private Boolean payoutPresent;
        private Long payoutId;
        private String payoutReference;

        private Boolean valueNotZero;
        public List<Long> inDictionaryId;
        public List<Long> inStausId;
        public List<Long> notInDictionaryId;
        public List<Long> notInStausId;

        private String dataList;
    }

}
