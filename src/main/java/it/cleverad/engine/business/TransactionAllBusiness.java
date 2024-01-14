package it.cleverad.engine.business;

import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.persistence.model.service.ViewTransactionStatus;
import it.cleverad.engine.persistence.repository.service.ViewTransactionStatusRepository;
import it.cleverad.engine.web.dto.TransactionStatusDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@Transactional
public class TransactionAllBusiness {

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
            transaction = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Transaction ALL", id));
        }
        return TransactionStatusDTO.from(transaction);
    }

    // SEARCH PAGINATED
    public Page<TransactionStatusDTO> searchPrefiltrato(Filter filter, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("dateTime")));

        // gestione api .../all/affiliate
        if (!jwtUserDetailsService.getRole().equals("Admin")) {
            filter.setValueNotZero(true);
            filter.setForAffiliate(true);
            filter.setAffiliateId(jwtUserDetailsService.getAffiliateID());
        }

        Page<ViewTransactionStatus> page = repository.findAll(getSpecification(filter), pageable);
        return page.map(TransactionStatusDTO::from);
    }

    public Page<TransactionStatusDTO> searchPrefiltratoInterno(Filter filter) {
        Page<ViewTransactionStatus> page = repository.findAll(getSpecification(filter), PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc("dateTime"))));
        return page.map(TransactionStatusDTO::from);
    }

    public Page<TransactionStatusDTO> searchStatusIdAndDate(Long statusId, LocalDate dataDaGestireStart, LocalDate dataDaGestireEnd, String tipo, Long affiliateId, Long campaignId) {
        Filter request = new Filter();
        request.setCreationDateFrom(dataDaGestireStart);
        request.setCreationDateTo(dataDaGestireEnd);
        request.setTipo(tipo);
        request.setStatusId(statusId);
        request.setAffiliateId(affiliateId);
        request.setCampaignId(campaignId);
        Page<ViewTransactionStatus> page = repository.findAll(getSpecification(request), PageRequest.of(0, Integer.MAX_VALUE));
        return page.map(TransactionStatusDTO::from);
    }

    public Page<TransactionStatusDTO> searchStatusIdAndDicIdAndDate(Long statusId, Long dicId, LocalDate dataDaGestireStart, LocalDate dataDaGestireEnd, String tipo, Long affiliateId, Long campaignId) {
        Filter request = new Filter();
        request.setCreationDateFrom(dataDaGestireStart);
        request.setCreationDateTo(dataDaGestireEnd);
        request.setTipo(tipo);
        request.setStatusId(statusId);
        request.setDictionaryId(dicId);
        request.setAffiliateId(affiliateId);
        request.setCampaignId(campaignId);
        Page<ViewTransactionStatus> page = repository.findAll(getSpecification(request), PageRequest.of(0, Integer.MAX_VALUE));
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
            if (request.getAgent() != null) {
                predicates.add(cb.equal(root.get("agent"), request.getAgent()));
            }
            if (request.getApproved() != null) {
                predicates.add(cb.equal(root.get("approved"), request.getApproved()));
            }
            if (request.getIp() != null) {
                predicates.add(cb.equal(root.get("ip"), request.getIp()));
            }
            if (request.getNote() != null) {
                predicates.add(cb.equal(root.get("note"), request.getNote()));
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
            if (request.getRefferal() != null) {
                predicates.add(cb.equal(root.get("refferal"), request.getRefferal()));
            }
            if (request.getCompanyId() != null) {
                predicates.add(cb.equal(root.get("companyId"), request.getCompanyId()));
            }
            if (request.getAdvertiserId() != null) {
                predicates.add(cb.equal(root.get("advertiserId"), request.getAdvertiserId()));
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

            if (request.getCreationDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dateTime"), request.getCreationDateFrom()));
            }
            if (request.getCreationDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dateTime"), request.getCreationDateTo()));
            }

            if (request.getDateTimeFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dateTime"), request.getDateTimeFrom()));
            }
            if (request.getDateTimeTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dateTime"), request.getDateTimeTo()));
            }

            if (request.getValueNotZero() != null && request.getValueNotZero()) {
                predicates.add(cb.notEqual(root.get("value"), "0"));
            }

            if (request.getPayoutPresent() != null) {
                predicates.add(cb.equal(root.get("payoutPresent"), request.getPayoutPresent()));
            }

            if (request.getDictionaryIdIn() != null) {
                CriteriaBuilder.In<Long> inClause = cb.in(root.get("dictionaryId"));
                for (Long id : request.getDictionaryIdIn()) {
                    inClause.value(id);
                }
                predicates.add(inClause);
            }

            if (request.getStatusIdIn() != null) {
                CriteriaBuilder.In<Long> inClause = cb.in(root.get("statusId"));
                for (Long id : request.getStatusIdIn()) {
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

            if (request.getNotInStatusId() != null) {
                CriteriaBuilder.In<Long> inClauseNot = cb.in(root.get("statusId"));
                for (Long id : request.getNotInStatusId()) {
                    inClauseNot.value(id);
                }
                predicates.add(inClauseNot.not());
            }

            if (request.getDataList() != null && request.getDataList().length() > 3) {
                log.info("Dentro :: ", request.getDataList());
                CriteriaBuilder.In<String> inClause = cb.in(cb.upper(root.get("data")));
                Arrays.stream(request.getDataList().split(",")).distinct().forEach(s -> {
                    inClause.value(s.toUpperCase());
                });
                predicates.add((inClause));
            }

            // filtro solo per affiliati transazioni non mostrate
            if (request.getForAffiliate()) {
                // prendo gli approvati
                // o i pending + pending // pending + approvato // pending + manuale
                Predicate apporvato = cb.equal(root.get("statusId"), 73L);
                Predicate pendingApprovato = cb.and(cb.equal(root.get("statusId"), 72L), cb.equal(root.get("dictionaryId"), 39L));
                Predicate pendingPending = cb.and(cb.equal(root.get("statusId"), 72L), cb.equal(root.get("dictionaryId"), 42L));
                Predicate pendingManuale = cb.and(cb.equal(root.get("statusId"), 72L), cb.equal(root.get("dictionaryId"), 68L));
                predicates.add(cb.or(apporvato, pendingApprovato, pendingPending, pendingManuale));
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
        public List<Long> notInStatusId;
        public List<Long> notInDictionaryId;
        public List<Long> statusIdIn;
        public List<Long> dictionaryIdIn;
        private Long id;
        private String agent;
        private Boolean approved;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate creationDateFrom;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate creationDateTo;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateTimeFrom;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateTimeTo;
        private String ip;
        private String note;
        private String payoutReference;
        private Double value;
        private Long affiliateId;
        private Long campaignId;
        private Long channelId;
        private Long commissionId;
        private Long payoutId;
        private Long walletId;
        private Long mediaId;
        private Long clickNumber;
        private String refferal;
        private Long companyId;
        private Long advertiserId;
        private String data;
        private String tipo;
        private Long dictionaryId;
        private Long statusId;
        private Boolean valueNotZero;
        private Boolean payoutPresent;
        private String dataList;
        private Boolean forAffiliate = false;

    }

}