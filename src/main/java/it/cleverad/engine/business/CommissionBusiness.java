package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.Commission;
import it.cleverad.engine.persistence.repository.CampaignRepository;
import it.cleverad.engine.persistence.repository.CommissionRepository;
import it.cleverad.engine.web.dto.CommissionDTO;
import it.cleverad.engine.web.dto.DictionaryDTO;
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
    private CampaignRepository campaignRepository;

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
        map.setCampaign(campaignRepository.findById(request.campaignId).orElseThrow());
        return CommissionDTO.from(repository.save(map));
    }

    // GET BY ID
    public CommissionDTO findById(Long id) {
        Commission commission = repository.findById(id).orElseThrow(() -> new ElementCleveradException(id));
        return CommissionDTO.from(commission);
    }

    // DELETE BY ID
    public void delete(Long id) {
        try {
            repository.deleteById(id);
        }  catch (ConstraintViolationException ex) {
            throw ex;
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    // SEARCH PAGINATED
    public Page<CommissionDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<Commission> page = repository.findAll(getSpecification(request), pageable);

        return page.map(CommissionDTO::from);
    }

    // UPDATE
    public CommissionDTO update(Long id, Filter filter) {
        try {
            Commission ommission = repository.findById(id).orElseThrow(() -> new ElementCleveradException(id));
            CommissionDTO campaignDTOfrom = CommissionDTO.from(ommission);

            mapper.map(filter, campaignDTOfrom);

            Commission mappedEntity = mapper.map(ommission, Commission.class);
            mappedEntity.setLastModificationDate(LocalDateTime.now());
            mapper.map(campaignDTOfrom, mappedEntity);

            return CommissionDTO.from(repository.save(mappedEntity));
        } catch (Exception e) {
            log.error("Errore in update", e);
            return null;
        }
    }

    //  GET TIPI
    public Page<DictionaryDTO> getTypes() {
        return dictionaryBusiness.getTypeCommission();
    }

    // GET CCOMMISION BY CAMPAIGN
    public Page<CommissionDTO> getByIdCampaign(Long id) {
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Order.asc("id")));
        Filter request = new Filter();
        request.setCampaignId(id);
        Page<Commission> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CommissionDTO::from);
    }

    /**
     * ============================================================================================================
     **/
    private Specification<Commission> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }

            if (request.getName() != null) {
                predicates.add(cb.equal(root.get("name"), request.getName()));
            }

            if (request.getValue() != null) {
                predicates.add(cb.equal(root.get("value"), request.getValue()));
            }
            if (request.getDescription() != null) {
                predicates.add(cb.equal(root.get("description"), request.getDescription()));
            }
            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }

            if (request.getCampaignId() != null) {
                predicates.add(cb.equal(root.get("campaign").get("id"), request.getCampaignId()));
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
        private String value;
        private String description;
        private Boolean status;
        private String idType;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dueDate;
        private Long campaignId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;

        private String name;
        private String value;
        private String description;
        private Boolean status;
        private String idType;
        private LocalDate dueDate;
        private Long campaignId;

        private Instant creationDateFrom;
        private Instant creationDateTo;
        private Instant lastModificationDateFrom;
        private Instant lastModificationDateTo;
    }

}

