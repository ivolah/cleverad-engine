package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.Affiliate;
import it.cleverad.engine.persistence.model.Campaign;
import it.cleverad.engine.persistence.model.CampaignAffiliate;
import it.cleverad.engine.persistence.repository.CampaignAffiliateRepository;
import it.cleverad.engine.web.dto.CampaignAffiliateDTO;
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
public class CampaignAffiliateBusiness {

    @Autowired
    private CampaignAffiliateRepository repository;

    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public CampaignAffiliateDTO create(BaseCreateRequest request) {
        CampaignAffiliate map = mapper.map(request, CampaignAffiliate.class);


        Affiliate cat = new Affiliate();
        cat.setId(request.getAffiliateId());
        map.setAffiliate(cat);

        Campaign campaign = new Campaign();
        campaign.setId(request.getCampaignId());
        map.setCampaign(campaign);

        return CampaignAffiliateDTO.from(repository.save(map));
    }

    public CampaignAffiliate createEntity(BaseCreateRequest request) {
        CampaignAffiliate map = mapper.map(request, CampaignAffiliate.class);

        Affiliate cat = new Affiliate();
        cat.setId(request.getAffiliateId());
        map.setAffiliate(cat);

        Campaign campaign = new Campaign();
        campaign.setId(request.getCampaignId());
        map.setCampaign(campaign);

        return repository.save(map);
    }


    // GET BY ID
    public CampaignAffiliateDTO findById(Long id) {
        CampaignAffiliate channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("CampaignAffiliate", id));
        return CampaignAffiliateDTO.from(channel);
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

    public void deleteByCampaignID(Long id) {
        Filter request = new Filter();
        request.setCampaignId(id);
        Page<CampaignAffiliate> page = repository.findAll(getSpecification(request), PageRequest.of(0, 1000, Sort.by(Sort.Order.asc("id"))));
        try {
            page.stream().forEach(campaignAffiliate -> repository.deleteById(campaignAffiliate.getId()));
        } catch (javax.validation.ConstraintViolationException ex) {
            throw ex;
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    public void deleteByCampaignIdAndAffiliateId(Long campaignId, Long affiliateID) {
        repository.findByAffiliateIdAndCampaignId(affiliateID, campaignId).stream().forEach(campaignAffiliate -> repository.deleteById(campaignAffiliate.getId()));
    }

    // SEARCH PAGINATED
    public Page<CampaignAffiliateDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<CampaignAffiliate> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CampaignAffiliateDTO::from);
    }

    public Page<CampaignAffiliateDTO> searchByAffiliateID(Long affiliateId) {
        Pageable pageable = PageRequest.of(0, 1000, Sort.by(Sort.Order.desc("id")));
        Filter request = new Filter();
        request.setAffiliateId(affiliateId);
        Page<CampaignAffiliate> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CampaignAffiliateDTO::from);
        // List<CampaignAffiliate> page = repository.findByAffiliateId(affiliateId);
        // return null;
    }

    public Page<CampaignAffiliateDTO> searchByCampaignID(Long campaignId, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Filter request = new Filter();
        request.setCampaignId(campaignId);
        Page<CampaignAffiliate> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CampaignAffiliateDTO::from);
        // List<CampaignAffiliate> page = repository.findByAffiliateId(affiliateId);
        // return null;
    }

    public Page<CampaignAffiliateDTO> searchByAffiliateIdAndCampaignId(Long affiliateId, Long campaignId) {
        Pageable pageable = PageRequest.of(0, 1000, Sort.by(Sort.Order.desc("id")));
        Filter request = new Filter();
        request.setAffiliateId(affiliateId);
        request.setCampaignId(campaignId);
        Page<CampaignAffiliate> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CampaignAffiliateDTO::from);
    }

    // UPDATE
    public CampaignAffiliateDTO update(Long id, Filter filter) {
        CampaignAffiliate channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("CampaignAffiliate", id));
        CampaignAffiliateDTO campaignDTOfrom = CampaignAffiliateDTO.from(channel);

        mapper.map(filter, campaignDTOfrom);

        CampaignAffiliate mappedEntity = mapper.map(channel, CampaignAffiliate.class);
        mapper.map(campaignDTOfrom, mappedEntity);

        return CampaignAffiliateDTO.from(repository.save(mappedEntity));
    }

    /**
     * ============================================================================================================
     **/

    private Specification<CampaignAffiliate> getSpecification(Filter request) {
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
        private String followThrough;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private String followThrough;
        private Long campaignId;
        private Long affiliateId;
    }

}
