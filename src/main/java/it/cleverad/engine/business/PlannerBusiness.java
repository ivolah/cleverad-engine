package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.Planner;
import it.cleverad.engine.persistence.repository.service.CampaignRepository;
import it.cleverad.engine.persistence.repository.service.PlannerRepository;
import it.cleverad.engine.web.dto.PlannerDTO;
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
public class PlannerBusiness {

    @Autowired
    private PlannerRepository repository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public PlannerDTO create(BaseCreateRequest request) {
        Planner map = mapper.map(request, Planner.class);
//        map.setCampaign(campaignRepository.findById(request.campaignId).orElseThrow(() -> new ElementCleveradException("Campaign", request.campaignId)));
        return PlannerDTO.from(repository.save(map));
    }

    // GET BY ID
    public PlannerDTO findById(Long id) {
        Planner entity = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Planner", id));
        return PlannerDTO.from(entity);
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
    public Page<PlannerDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<Planner> page = repository.findAll(getSpecification(request), pageable);
        return page.map(PlannerDTO::from);
    }

    public Page<PlannerDTO> findByIdCampaign(Long id) {
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Order.asc("id")));
        Filter request = new Filter();
//        request.setCampaignId(id);
        Page<Planner> page = repository.findAll(getSpecification(request), pageable);
        return page.map(PlannerDTO::from);
    }

    // UPDATE
    public PlannerDTO update(Long id, Filter filter) {
        Planner channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Planner", id));
        PlannerDTO campaignDTOfrom = PlannerDTO.from(channel);

        mapper.map(filter, campaignDTOfrom);

        Planner mappedEntity = mapper.map(channel, Planner.class);
        mapper.map(campaignDTOfrom, mappedEntity);

//        mappedEntity.setCampaign(campaignRepository.findById(filter.campaignId).orElseThrow(() -> new ElementCleveradException("Campaign", filter.campaignId)));
        return PlannerDTO.from(repository.save(mappedEntity));
    }

    /**
     * ============================================================================================================
     **/
    
    private Specification<Planner> getSpecification(Filter request) {
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

//        private Long campaignId;
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

//        private Long campaignId;

        private Boolean status;
    }

}
