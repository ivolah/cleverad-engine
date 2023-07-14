package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.Wallet;
import it.cleverad.engine.persistence.repository.service.WalletRepository;
import it.cleverad.engine.web.dto.WalletDTO;
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
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional
public class WalletBusiness {

    @Autowired
    private WalletRepository repository;

    @Autowired
    private WalletTransactionBusiness walletTransactionBusiness;

    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public WalletDTO create(BaseCreateRequest request) {
        Wallet map = mapper.map(request, Wallet.class);
        return WalletDTO.from(repository.save(map));
    }

    // GET BY ID
    public WalletDTO findById(Long id) {
        Wallet channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Wallet", id));
        return WalletDTO.from(channel);
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
    public Page<WalletDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<Wallet> page = repository.findAll(getSpecification(request), pageable);
        return page.map(WalletDTO::from);
    }

    public Page<WalletDTO> findByIdAffilaite(Long id) {
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Order.asc("id")));
        Filter request = new Filter();
        request.setAffiliateId(id);
        Page<Wallet> page = repository.findAll(getSpecification(request), pageable);
        return page.map(WalletDTO::from);
    }


    // UPDATE
    public WalletDTO update(Long id, Filter filter) {
        Wallet channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Wallet", id));
        WalletDTO campaignDTOfrom = WalletDTO.from(channel);

        mapper.map(filter, campaignDTOfrom);

        Wallet mappedEntity = mapper.map(channel, Wallet.class);
        mapper.map(campaignDTOfrom, mappedEntity);

        return WalletDTO.from(repository.save(mappedEntity));
    }

    public WalletDTO incement(Long id, Double value) {
        Wallet wallet = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Wallet", id));

        log.trace("Increment {} by :: {}", id, value);

        // savo storicizzazione wallet
        WalletTransactionBusiness.BaseCreateRequest req = new WalletTransactionBusiness.BaseCreateRequest();
        req.setWalletId(id);
        req.setTotalBefore(wallet.getTotal());
        req.setPayedBefore(wallet.getPayed());
        req.setResidualBefore(wallet.getResidual());
        req.setPayedAfter(wallet.getPayed());

        Double totale = wallet.getTotal() + value;
        Double residual = wallet.getResidual() + value;
        //  if ((!wallet.getTotal().equals(totale)) || (!wallet.getResidual().equals(residual))) {
        req.setTotalAfter(totale);
        req.setResidualAfter(residual);
        walletTransactionBusiness.create(req);
        //  }

        wallet.setResidual(residual);
        wallet.setTotal(totale);
        return WalletDTO.from(repository.save(wallet));
    }

    public WalletDTO decrement(Long id, Double value) {
        Wallet wallet = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Wallet", id));

        log.trace("Decrement by :: {}", value);

        WalletTransactionBusiness.BaseCreateRequest req = new WalletTransactionBusiness.BaseCreateRequest();
        req.setWalletId(id);
        req.setTotalBefore(wallet.getTotal());
        req.setResidualBefore(wallet.getResidual());
        req.setPayedBefore(wallet.getPayed());

        Double residual = wallet.getResidual() - value;
        Double payed = wallet.getPayed() + value;
        // if (!wallet.getPayed().equals(payed) || !wallet.getResidual().equals(residual)) {
        req.setPayedAfter(payed);
        req.setTotalAfter(wallet.getTotal() - value);
        req.setResidualAfter(residual);
        walletTransactionBusiness.create(req);
        //   }

        wallet.setPayed(payed);
        wallet.setTotal(residual);


        return WalletDTO.from(repository.saveAndFlush(wallet));
    }

    /**
     * ============================================================================================================
     **/
    private Specification<Wallet> getSpecification(Filter request) {
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
        private String nome;
        private String description;

        private Double total;
        private Double payed;
        private Double residual;

        private Boolean status;

        private Long affiliateId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;

        private String nome;
        private String description;

        private Double total;
        private Double payed;
        private Double residual;

        private Boolean status;

        private Long affiliateId;
    }

}
