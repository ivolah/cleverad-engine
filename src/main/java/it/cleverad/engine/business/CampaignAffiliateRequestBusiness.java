package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.CampaignAffiliateRequest;
import it.cleverad.engine.persistence.repository.service.AffiliateRepository;
import it.cleverad.engine.persistence.repository.service.CampaignAffiliateRequestRepository;
import it.cleverad.engine.persistence.repository.service.CampaignRepository;
import it.cleverad.engine.persistence.repository.service.DictionaryRepository;
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
    private AffiliateRepository affiliateRepository;
    @Autowired
    private CampaignRepository campaignRepository;
    @Autowired
    private DictionaryRepository dictionaryRepository;
    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public CampaignAffiliateRequestDTO create(BaseCreateRequest request) {
        CampaignAffiliateRequest map = mapper.map(request, CampaignAffiliateRequest.class);

        map.setAffiliate(affiliateRepository.findById(request.affiliateId).orElseThrow(() -> new ElementCleveradException("Affiliate", request.affiliateId)));
        map.setCampaign(campaignRepository.findById(request.campaignId).orElseThrow(() -> new ElementCleveradException("Campaign", request.campaignId)));
        map.setDictionaryStatusCampaignAffiliateRequest(dictionaryRepository.findById(request.statusId).orElseThrow(() -> new ElementCleveradException("Dictionary", request.statusId)));

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
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("requestDate")));
        Page<CampaignAffiliateRequest> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CampaignAffiliateRequestDTO::from);
    }

    public Page<CampaignAffiliateRequestDTO> searchByCampaignID(Long campaignId, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("requestDate")));
        Filter request = new Filter();
        request.setCampaignId(campaignId);
        Page<CampaignAffiliateRequest> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CampaignAffiliateRequestDTO::from);
    }

    public Page<CampaignAffiliateRequestDTO> searchByAffiliateID(Long affiliateId, Pageable pageable) {
        Filter request = new Filter();
        request.setAffiliateId(affiliateId);
        Page<CampaignAffiliateRequest> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CampaignAffiliateRequestDTO::from);
    }

    // UPDATE
    public CampaignAffiliateRequestDTO update(Long id, Filter filter) {
        CampaignAffiliateRequest campaignAffiliateRequest = repository.findById(id).orElseThrow(() -> new ElementCleveradException("CampaignAffiliateRequest", id));
        filter.setId(id);
        mapper.map(filter, campaignAffiliateRequest);
        if (filter.getAffiliateId() != null)
            campaignAffiliateRequest.setAffiliate(affiliateRepository.findById(filter.affiliateId).orElseThrow(() -> new ElementCleveradException("Affiliate", filter.affiliateId)));
        if (filter.getCampaignId() != null)
            campaignAffiliateRequest.setCampaign(campaignRepository.findById(filter.campaignId).orElseThrow(() -> new ElementCleveradException("Campaign", filter.campaignId)));
        if (filter.getStatusId() != null)
            campaignAffiliateRequest.setDictionaryStatusCampaignAffiliateRequest(dictionaryRepository.findById(filter.statusId).orElseThrow(() -> new ElementCleveradException("Dictionary", filter.statusId)));
        return CampaignAffiliateRequestDTO.from(repository.save(campaignAffiliateRequest));
    }

    public Page<DictionaryDTO> getTypes() {
        return dictionaryBusiness.getAffiliateCampaignRequestStatusTypes();
    }

    /**
     * ============================================================================================================
     **/

    private Specification<CampaignAffiliateRequest> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate;
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