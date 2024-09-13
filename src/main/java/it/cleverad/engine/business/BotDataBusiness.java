package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.BotData;
import it.cleverad.engine.persistence.repository.service.BotDataRepository;
import it.cleverad.engine.web.dto.BotDataDTO;
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
import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional
public class BotDataBusiness {

    @Autowired
    private BotDataRepository repository;
    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // Create a new BotData entry
    public BotDataDTO create(BaseCreateRequest request) {
        BotData map = mapper.map(request, BotData.class);
        return BotDataDTO.from(repository.save(map));
    }

    // Retrieve a BotData entry by ID
    public BotDataDTO findById(Long id) {
        return BotDataDTO.from(repository.findById(id).orElseThrow(() -> new ElementCleveradException("BotData", id)));
    }

    // Retrieve all BotData entries
    public List<BotDataDTO> getAll() {
        return repository.findAll().stream().map(BotDataDTO::from).collect(Collectors.toList());
    }

    // SEARCH PAGINATED
    public Page<BotDataDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        Page<BotData> page = repository.findAll(getSpecification(request), pageable);
        return page.map(BotDataDTO::from);
    }

    // Update an existing BotData entry
    public BotDataDTO update(Long id, Filter updatedData) {
        return BotDataDTO.from(repository.save(repository.findById(id).map(existingData -> {
            existingData.setCap(updatedData.getCap());
            existingData.setTelefono(updatedData.getTelefono());
            existingData.setIp(updatedData.getIp());
            existingData.setTs(updatedData.getTs());
            existingData.setCampaignName(updatedData.getCampaignName());
            existingData.setCampaignReferral(updatedData.getCampaignReferral());
            existingData.setReferral(updatedData.getReferral());
            existingData.setEmail(updatedData.getEmail());
            existingData.setPrivacy1(updatedData.getPrivacy1());
            existingData.setPrivacy2(updatedData.getPrivacy2());
            return repository.save(existingData);
        }).orElseThrow(() -> new RuntimeException("BotData with ID " + id + " not found"))));
    }

    // Delete a BotData entry by ID
    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (ConstraintViolationException ex) {
            throw ex;
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    /**
     * ============================================================================================================
     **/
    private Specification<BotData> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getCampaignName() != null) {
                predicates.add(cb.like(cb.upper(root.get("campaignName")), "%" + request.getCampaignName().toUpperCase() + "%"));
            }
            if (request.getCampaignReferral() != null) {
                predicates.add(cb.like(root.get("campaignReferral"), "%" + request.getCampaignReferral() + "%"));
            }
            if (request.getIp() != null) {
                predicates.add(cb.equal(root.get("ip"), request.getIp()));
            }
            if (request.getTsFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("ts"), LocalDateTime.ofInstant(request.getTsFrom(), ZoneOffset.UTC)));
            }
            if (request.getTsTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("ts"), LocalDateTime.ofInstant(request.getTsTo().plus(1, ChronoUnit.DAYS), ZoneOffset.UTC)));
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
        private String cap;
        private String telefono;
        private String ip;
        private String campaignName;
        private String campaignReferral;
        private String referral;
        private String email;
        private Boolean privacy1;
        private Boolean privacy2;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private String cap;
        private String telefono;
        private String ip;
        private LocalDateTime ts;
        private Instant tsFrom;
        private Instant tsTo;
        private String campaignName;
        private String campaignReferral;
        private String referral;
        private String email;
        private Boolean privacy1;
        private Boolean privacy2;
    }

}