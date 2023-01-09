package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.Dealer;
import it.cleverad.engine.persistence.repository.CampaignRepository;
import it.cleverad.engine.persistence.repository.DealerRepository;
import it.cleverad.engine.web.dto.DealerDTO;
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
public class DealerBusiness {

    @Autowired
    private DealerRepository repository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public DealerDTO create(BaseCreateRequest request) {
        Dealer map = mapper.map(request, Dealer.class);
        //map.setCampaign(campaignRepository.findById(request.campaignId).orElseThrow(() -> new ElementCleveradException("Campaign", request.campaignId)));
        return DealerDTO.from(repository.save(map));
    }

    // GET BY ID
    public DealerDTO findById(Long id) {
        Dealer entity = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Dealer", id));
        return DealerDTO.from(entity);
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
    public Page<DealerDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<Dealer> page = repository.findAll(getSpecification(request), pageable);
        return page.map(DealerDTO::from);
    }

    public Page<DealerDTO> findByIdCampaign(Long id) {
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Order.asc("id")));
        Filter request = new Filter();
       // request.setCampaignId(id);
        Page<Dealer> page = repository.findAll(getSpecification(request), pageable);
        return page.map(DealerDTO::from);
    }


    // UPDATE
    public DealerDTO update(Long id, Filter filter) {
        Dealer channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Dealer", id));
        DealerDTO campaignDTOfrom = DealerDTO.from(channel);

        mapper.map(filter, campaignDTOfrom);

        Dealer mappedEntity = mapper.map(channel, Dealer.class);
        mapper.map(campaignDTOfrom, mappedEntity);

        ///mappedEntity.setCampaign(campaignRepository.findById(filter.campaignId).orElseThrow(() -> new ElementCleveradException("Campaign", filter.campaignId)));
        return DealerDTO.from(repository.save(mappedEntity));
    }

    /**
     * ============================================================================================================
     **/
    
    private Specification<Dealer> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }
            if (request.getName() != null) {
                predicates.add(cb.equal(root.get("name"), request.getName()));
            }
            if (request.getSurname() != null) {
                predicates.add(cb.equal(root.get("surname"), request.getSurname()));
            }
            if (request.getEmail() != null) {
                predicates.add(cb.equal(root.get("email"), request.getEmail()));
            }
            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }
//            if (request.getCampaignId() != null) {
//                predicates.add(cb.equal(root.get("campaign").get("id"), request.getCampaignId()));
//            }
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
        private String surname;
        private String email;
        private String phonePrefix;
        private String phone;
        private String mobilePrefix;
        private String mobile;

        //private Long campaignId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;

        private String name;
        private String surname;
        private String email;
        private String phonePrefix;
        private String phone;
        private String mobilePrefix;
        private String mobile;

      //  private Long campaignId;

        private Boolean status;
    }

}
