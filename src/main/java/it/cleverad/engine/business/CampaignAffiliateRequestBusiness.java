package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.Affiliate;
import it.cleverad.engine.persistence.model.service.Campaign;
import it.cleverad.engine.persistence.model.service.CampaignAffiliateRequest;
import it.cleverad.engine.persistence.model.service.Dictionary;
import it.cleverad.engine.persistence.repository.service.CampaignAffiliateRequestRepository;
import it.cleverad.engine.web.dto.CampaignAffiliateRequestDTO;
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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional
public class CampaignAffiliateRequestBusiness {

    @Autowired
    private CampaignAffiliateRequestRepository repository;

    @Autowired
    private DictionaryBusiness dictionaryBusiness;

    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public CampaignAffiliateRequestDTO create(BaseCreateRequest request) {
        CampaignAffiliateRequest map = mapper.map(request, CampaignAffiliateRequest.class);

        Affiliate cat = new Affiliate();
        cat.setId(request.getAffiliateId());
        map.setAffiliate(cat);

        Campaign campaign = new Campaign();
        campaign.setId(request.getCampaignId());
        map.setCampaign(campaign);

        Dictionary dict = new Dictionary();
        dict.setId(request.getStatusId());
        map.setDictionaryStatusCampaignAffiliateRequest(dict);

        return CampaignAffiliateRequestDTO.from(repository.save(map));
    }

    // GET BY ID
    public CampaignAffiliateRequestDTO findById(Long id) {
        CampaignAffiliateRequest campaignAffiliateRequest = repository.findById(id).orElseThrow(() -> new ElementCleveradException("CampaignAffiliateRequest", id));
        return CampaignAffiliateRequestDTO.from(campaignAffiliateRequest);
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
    public Page<CampaignAffiliateRequestDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<CampaignAffiliateRequest> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CampaignAffiliateRequestDTO::from);
    }

    public Page<CampaignAffiliateRequestDTO> searchByAffiliateID(Long affiliateId) {
        Pageable pageable = PageRequest.of(0, 1000, Sort.by(Sort.Order.desc("id")));
        Filter request = new Filter();
        request.setAffiliateId(affiliateId);
        Page<CampaignAffiliateRequest> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CampaignAffiliateRequestDTO::from);
    }

    public Page<CampaignAffiliateRequestDTO> searchByCampaignID(Long campaignId, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Filter request = new Filter();
        request.setCampaignId(campaignId);
        Page<CampaignAffiliateRequest> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CampaignAffiliateRequestDTO::from);
    }

    public Page<CampaignAffiliateRequestDTO> searchByAffiliateIdAndCampaignId(Long affiliateId, Long campaignId) {
        Pageable pageable = PageRequest.of(0, 1000, Sort.by(Sort.Order.desc("id")));
        Filter request = new Filter();
        request.setAffiliateId(affiliateId);
        request.setCampaignId(campaignId);
        Page<CampaignAffiliateRequest> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CampaignAffiliateRequestDTO::from);
    }

    // UPDATE
    public CampaignAffiliateRequestDTO update(Long id, Filter filter) {
        CampaignAffiliateRequest channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("CampaignAffiliateRequest", id));
        CampaignAffiliateRequestDTO campaignDTOfrom = CampaignAffiliateRequestDTO.from(channel);

        mapper.map(filter, campaignDTOfrom);

        CampaignAffiliateRequest mappedEntity = mapper.map(channel, CampaignAffiliateRequest.class);
        mapper.map(campaignDTOfrom, mappedEntity);

        return CampaignAffiliateRequestDTO.from(repository.save(mappedEntity));
    }

    public Page<DictionaryDTO> getTypes() {
        return dictionaryBusiness.getAffiliateCampaignRequestStatusTypes();
    }

    /**
     * ============================================================================================================
     **/

    private Specification<CampaignAffiliateRequest> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getAffiliateId() != null) {
                predicates.add(cb.equal(root.get("affiliate").get("id"), request.getAffiliateId()));
            }
            if (request.getCampaignId() != null) {
                predicates.add(cb.equal(root.get("campaign").get("id"), request.getCampaignId()));
            }
            if (request.getStatusId() != null) {
                predicates.add(cb.equal(root.get("dictionary").get("id"), request.getStatusId()));
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
        private Long affiliateId;
        private Long statusId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private Long campaignId;
        private Long affiliateId;
        private Long statusId;
    }

}
