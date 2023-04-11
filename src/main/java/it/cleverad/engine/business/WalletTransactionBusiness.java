package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.WalletTransaction;
import it.cleverad.engine.persistence.repository.service.WalletTransactionRepository;
import it.cleverad.engine.web.dto.WalletDTO;
import it.cleverad.engine.web.dto.WalletTransactionDTO;
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
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional
public class WalletTransactionBusiness {

    @Autowired
    private WalletTransactionRepository repository;
    @Autowired
    private WalletBusiness walletBusiness;
    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public WalletTransactionDTO create(BaseCreateRequest request) {
        WalletTransaction map = mapper.map(request, WalletTransaction.class);
        return WalletTransactionDTO.from(repository.save(map));
    }

    // GET BY ID
    public WalletTransactionDTO findById(Long id) {
        WalletTransaction channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("WalletTransaction", id));
        return WalletTransactionDTO.from(channel);
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
    public Page<WalletTransactionDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<WalletTransaction> page = repository.findAll(getSpecification(request), pageable);
        return page.map(WalletTransactionDTO::from);
    }

    public Page<WalletTransactionDTO> findByIdAffilaite(Long id) {
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Order.asc("id")));
        Filter request = new Filter();
        WalletDTO dto = walletBusiness.findByIdAffilaite(id).stream().findFirst().get();
        request.setWalletId(dto.getId());
        Page<WalletTransaction> page = repository.findAll(getSpecification(request), pageable);
        return page.map(WalletTransactionDTO::from);
    }

    // UPDATE
    public WalletTransactionDTO update(Long id, Filter filter) {
        WalletTransaction channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("WalletTransaction", id));
        WalletTransactionDTO campaignDTOfrom = WalletTransactionDTO.from(channel);

        mapper.map(filter, campaignDTOfrom);

        WalletTransaction mappedEntity = mapper.map(channel, WalletTransaction.class);
        mapper.map(campaignDTOfrom, mappedEntity);

        return WalletTransactionDTO.from(repository.save(mappedEntity));
    }

    /**
     * ============================================================================================================
     **/
    private Specification<WalletTransaction> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
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
        private Long id;
        private Double totalBefore;
        private Double payedBefore;
        private Double residualBefore;
        private Double totalAfter;
        private Double payedAfter;
        private Double residualAfter;
        private Long walletId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;

        private Double totalBefore;
        private Double payedBefore;
        private Double residualBefore;
        private Double totalAfter;
        private Double payedAfter;
        private Double residualAfter;

        private Long walletId;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateFrom;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateTo;
    }

}
