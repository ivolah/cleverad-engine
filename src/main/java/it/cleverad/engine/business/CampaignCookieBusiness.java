package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.Campaign;
import it.cleverad.engine.persistence.model.service.CampaignCookie;
import it.cleverad.engine.persistence.model.service.Cookie;
import it.cleverad.engine.persistence.repository.service.CampaignCookieRepository;
import it.cleverad.engine.web.dto.CampaignCookieDTO;
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
public class CampaignCookieBusiness {

    @Autowired
    private CampaignCookieRepository repository;

    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public CampaignCookieDTO create(BaseCreateRequest request) {
        CampaignCookie map = mapper.map(request, CampaignCookie.class);
        map.setCreationDate(LocalDateTime.now());
        map.setLastModificationDate(LocalDateTime.now());

        Cookie cat = new Cookie();
        cat.setId(request.getCookieId());
        map.setCookie(cat);

        Campaign campaign = new Campaign();
        campaign.setId(request.getCampaignId());
        map.setCampaign(campaign);

        return CampaignCookieDTO.from(repository.save(map));
    }

    // GET BY ID
    public CampaignCookieDTO findById(Long id) {
        CampaignCookie channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("CampaignCookie", id));
        return CampaignCookieDTO.from(channel);
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
    public Page<CampaignCookieDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<CampaignCookie> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CampaignCookieDTO::from);
    }

    // UPDATE
    public CampaignCookieDTO update(Long id, Filter filter) {

        CampaignCookie channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("CampaignCookie", id));
        CampaignCookieDTO campaignDTOfrom = CampaignCookieDTO.from(channel);

        mapper.map(filter, campaignDTOfrom);

        CampaignCookie mappedEntity = mapper.map(channel, CampaignCookie.class);
        mappedEntity.setLastModificationDate(LocalDateTime.now());
        mapper.map(campaignDTOfrom, mappedEntity);

        return CampaignCookieDTO.from(repository.save(mappedEntity));
    }

    /**
     * ============================================================================================================
     **/

    private Specification<CampaignCookie> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }

            if (request.getCookieId() != null) {
                predicates.add(cb.equal(root.get("cookieId"), request.getCookieId()));
            }
            if (request.getCampaignId() != null) {
                predicates.add(cb.equal(root.get("campaignId"), request.getCampaignId()));
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
        private Long campaignId;
        private Long cookieId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;

        private Long campaignId;
        private Long cookieId;

        private Instant creationDateFrom;
        private Instant creationDateTo;
        private Instant lastModificationDateFrom;
        private Instant lastModificationDateTo;
    }

}