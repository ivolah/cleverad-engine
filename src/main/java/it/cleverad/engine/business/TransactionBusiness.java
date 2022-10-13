package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.Transaction;
import it.cleverad.engine.persistence.repository.TransactionRepository;
import it.cleverad.engine.web.dto.TransactionDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import it.cleverad.engine.web.exception.PostgresDeleteCleveradException;
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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional
public class TransactionBusiness {

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public TransactionDTO create(BaseCreateRequest request) {
        Transaction map = mapper.map(request, Transaction.class);
        return TransactionDTO.from(repository.save(map));
    }

    // GET BY ID
    public TransactionDTO findById(Long id) {
        try {
            Transaction channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException(id));
            return TransactionDTO.from(channel);
        } catch (Exception e) {
            log.error("Errore in findById", e);
            return null;
        }
    }

    // DELETE BY ID
    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    // SEARCH PAGINATED
    public Page<TransactionDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<Transaction> page = repository.findAll(getSpecification(request), pageable);
        return page.map(TransactionDTO::from);
    }

    // UPDATE
    public TransactionDTO update(Long id, Filter filter) {
        try {
            Transaction channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException(id));
            TransactionDTO campaignDTOfrom = TransactionDTO.from(channel);

            mapper.map(filter, campaignDTOfrom);

            Transaction mappedEntity = mapper.map(channel, Transaction.class);
            mapper.map(campaignDTOfrom, mappedEntity);

            return TransactionDTO.from(repository.save(mappedEntity));
        } catch (Exception e) {
            log.error("Errore in update", e);
            return null;
        }
    }


    /**
     * ============================================================================================================
     **/
    private Specification<Transaction> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }

            if (request.getAffiliateId() != null) {
                predicates.add(cb.equal(root.get("affiliate").get("id"), request.getAffiliateId()));
            }
            if (request.getCampaignId() != null) {
                predicates.add(cb.equal(root.get("campaign").get("id"), request.getCampaignId()));
            }
            if (request.getCommissionId() != null) {
                predicates.add(cb.equal(root.get("commission").get("id"), request.getCommissionId()));
            }
            if (request.getChannelId() != null) {
                predicates.add(cb.equal(root.get("channel").get("id"), request.getChannelId()));
            }
            if (request.getWalletId() != null) {
                predicates.add(cb.equal(root.get("wallet").get("id"), request.getWalletId()));
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
        private Long commissionId;
        private Long channelId;
        private Long walletId;

        private LocalDateTime dateTime;
        private String type;
        private Double value;
        private Boolean approved;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;

        private Long affiliateId;
        private Long campaignId;
        private Long commissionId;
        private Long channelId;
        private Long walletId;
        private String type;
        private Boolean status;
    }

}

