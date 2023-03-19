package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.TransactionAll;
import it.cleverad.engine.persistence.repository.service.TransactionAllRepository;
import it.cleverad.engine.service.JwtUserDetailsService;
import it.cleverad.engine.web.dto.TransactionAllDTO;
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
public class TransactionAllBusiness {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private TransactionAllRepository repository;

    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public TransactionAllDTO create(BaseCreateRequest request) {
        TransactionAll map = mapper.map(request, TransactionAll.class);
        return TransactionAllDTO.from(repository.save(map));
    }

    // GET BY ID
    public TransactionAllDTO findById(Long id) {
        TransactionAll transaction = null;
        if (jwtUserDetailsService.getRole().equals("Admin")) {
            transaction = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Transaction ALL", id));
        }
        return TransactionAllDTO.from(transaction);
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
    public Page<TransactionAllDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        request.setPayoutId(null);
        Page<TransactionAll> page = repository.findAll(getSpecification(request), pageable);
        return page.map(TransactionAllDTO::from);
    }

    public Page<TransactionAllDTO> searchPrefiltrato(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        request.setPayoutId(null);
        if (jwtUserDetailsService.getRole().equals("Admin")) {
            Page<TransactionAll> page = repository.findAll(getSpecification(request), pageable);
            return page.map(TransactionAllDTO::from);
        } else {
            request.setAffiliateId(jwtUserDetailsService.getAffiliateID());
            Page<TransactionAll> page = repository.findAll(getSpecification(request), pageable);
            return page.map(TransactionAllDTO::from);
        }
    }


    // UPDATE
    //    public TransactionDTO update(Long id, Filter filter) {
    //        Transaction channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Transaction", id));
    //        TransactionDTO campaignDTOfrom = TransactionDTO.from(channel);
    //
    //        mapper.map(filter, campaignDTOfrom);
    //
    //        Transaction mappedEntity = mapper.map(channel, Transaction.class);
    //        mapper.map(campaignDTOfrom, mappedEntity);
    //
    //        return TransactionDTO.from(repository.save(mappedEntity));
    //    }

    /**
     * ============================================================================================================
     **/

    private Specification<TransactionAll> getSpecification(Filter request) {
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
                predicates.add(cb.equal(root.get("data"), request.getData()));
            }
            if (request.getTipo() != null) {
                predicates.add(cb.equal(root.get("tipo"), request.getTipo()));
            }


            if (request.getCreationDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("creationDate"), LocalDateTime.ofInstant(request.getCreationDateFrom(), ZoneOffset.UTC)));
            }
            if (request.getCreationDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("creationDate"), LocalDateTime.ofInstant(request.getCreationDateTo().plus(1, ChronoUnit.DAYS), ZoneOffset.UTC)));
            }

            if (request.getCreationDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dateTime"), LocalDateTime.ofInstant(request.getDateTimeFrom(), ZoneOffset.UTC)));
            }
            if (request.getCreationDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dateTime"), LocalDateTime.ofInstant(request.getDateTimeTo().plus(1, ChronoUnit.DAYS), ZoneOffset.UTC)));
            }


            predicates.add(cb.isNull(root.get("payoutId")));

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
        private Long id;
        private String agent;
        private Boolean approved;
        private LocalDateTime creationDate;
        private LocalDateTime dateTime;
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
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private String agent;
        private Boolean approved;
        private Instant creationDateFrom;
        private Instant creationDateTo;
        private Instant dateTimeFrom;
        private Instant dateTimeTo;
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
    }

}
