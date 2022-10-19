package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.Wallet;
import it.cleverad.engine.persistence.repository.AffiliateRepository;
import it.cleverad.engine.persistence.repository.WalletRepository;
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
    private AffiliateRepository affiliateRepository;

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

