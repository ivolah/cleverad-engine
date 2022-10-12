package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.RevenueFactor;
import it.cleverad.engine.persistence.repository.CampaignRepository;
import it.cleverad.engine.persistence.repository.DictionaryRepository;
import it.cleverad.engine.persistence.repository.RevenueFactorRepository;
import it.cleverad.engine.web.dto.DictionaryDTO;
import it.cleverad.engine.web.dto.RevenueFactorDTO;
import it.cleverad.engine.web.exception.PostgresCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
public class RevenueFactorBusiness {

    @Autowired
    private RevenueFactorRepository repository;

    @Autowired
    private Mapper mapper;

    @Autowired
    private DictionaryBusiness dictionaryBusiness;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private DictionaryRepository dictionaryRepository;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public RevenueFactorDTO create(BaseCreateRequest request) {
        RevenueFactor map = mapper.map(request, RevenueFactor.class);
        map.setCampaign(campaignRepository.findById(request.campaignId).orElseThrow());
        map.setDictionary(dictionaryRepository.findById(request.idType).orElseThrow());
        map.setCreationDate(LocalDateTime.now());
        map.setLastModificationDate(LocalDateTime.now());
        return RevenueFactorDTO.from(repository.save(map));
    }

    // GET BY ID
    public RevenueFactorDTO findById(Long id) {
        try {
            RevenueFactor entity = repository.findById(id).orElseThrow(Exception::new);
            return RevenueFactorDTO.from(entity);
        } catch (Exception e) {
            log.error("Errore in findById", e);
            return null;
        }
    }

    // DELETE BY ID
    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ee) {
            log.warn("Impossibile cancellare RevenueFactore.");
            throw new PostgresCleveradException("Impossibile cancellare RevenueFactore ");
        }
    }

    // SEARCH PAGINATED
    public Page<RevenueFactorDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<RevenueFactor> page = repository.findAll(getSpecification(request), pageable);

        return page.map(RevenueFactorDTO::from);
    }

    // UPDATE
    public RevenueFactorDTO update(Long id, Filter filter) {
        try {
            RevenueFactor ommission = repository.findById(id).orElseThrow(Exception::new);
            RevenueFactorDTO campaignDTOfrom = RevenueFactorDTO.from(ommission);

            mapper.map(filter, campaignDTOfrom);

            RevenueFactor mappedEntity = mapper.map(ommission, RevenueFactor.class);
            mappedEntity.setLastModificationDate(LocalDateTime.now());
            mapper.map(campaignDTOfrom, mappedEntity);

            return RevenueFactorDTO.from(repository.save(mappedEntity));
        } catch (Exception e) {
            log.error("Errore in update", e);
            return null;
        }
    }

    public Page<RevenueFactorDTO> getbyIdCampaign(Long id, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Filter request = new Filter();
        request.setCampaignId(id);
        Page<RevenueFactor> page = repository.findAll(getSpecification(request), pageable);
        return page.map(RevenueFactorDTO::from);
    }

    //  GET TIPI
    public Page<DictionaryDTO> getTypes() {
        return dictionaryBusiness.getTypeCommission();
    }


    /**
     * ============================================================================================================
     **/
    private Specification<RevenueFactor> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }

            if (request.getCampaignId() != null) {
                predicates.add(cb.equal(root.get("campaign").get("id"), request.getCampaignId()));
            }
            if (request.getTypeId() != null) {
                predicates.add(cb.equal(root.get("dictionary").get("id"), request.getTypeId()));
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
        private Long revenue;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dueDate;
        private Boolean status;
        private Long campaignId;
        private Long idType;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private Long revenue;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dueDate;
        private Boolean status;

        private Long campaignId;
        private Long typeId;

        private Instant creationDateFrom;
        private Instant creationDateTo;
        private Instant lastModificationDateFrom;
        private Instant lastModificationDateTo;
    }

}

