package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.persistence.model.service.Affiliate;
import it.cleverad.engine.persistence.model.service.AffiliateChannelCommissionCampaign;
import it.cleverad.engine.persistence.model.service.Campaign;
import it.cleverad.engine.persistence.model.service.Commission;
import it.cleverad.engine.persistence.repository.service.*;
import it.cleverad.engine.service.MailService;
import it.cleverad.engine.web.dto.AffiliateChannelCommissionCampaignDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import it.cleverad.engine.web.exception.PostgresDeleteCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional
public class AffiliateChannelCommissionCampaignBusiness {

    @Autowired
    private AffiliateChannelCommissionCampaignRepository repository;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private AffiliateRepository affiliateRepository;
    @Autowired
    private CampaignRepository campaignRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private ChannelBusiness channelBusiness;
    @Autowired
    private CommissionRepository commissionRepository;
    @Autowired
    private CampaignAffiliateBusiness campaignAffiliateBusiness;
    @Autowired
    private MailService mailService;
    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public AffiliateChannelCommissionCampaignDTO create(BaseCreateRequest request) {
        if (request.getChannelId() == 0) {
            log.info(request.toString());
            channelBusiness.getbyIdAffiliateAllActive(request.getAffiliateId()).forEach(idCanale -> {
                AffiliateChannelCommissionCampaign map = mapper.map(request, AffiliateChannelCommissionCampaign.class);
                map.setCreationDate(LocalDateTime.now());
                map.setLastModificationDate(LocalDateTime.now());

                Affiliate affiliate = affiliateRepository.findById(request.getAffiliateId()).orElseThrow(() -> new ElementCleveradException("Affilirte", request.getAffiliateId()));
                map.setAffiliate(affiliate);
                Commission commission = commissionRepository.findById(request.getCommissionId()).orElseThrow(() -> new ElementCleveradException("Commission", request.getCommissionId()));
                map.setCommission(commission);
                Campaign campaign = campaignRepository.findById(request.getCampaignId()).orElseThrow(() -> new ElementCleveradException("Campaign", request.getCampaignId()));
                map.setCampaign(campaign);
                map.setChannel(channelRepository.findById(idCanale).orElseThrow(() -> new ElementCleveradException("Channel", idCanale)));
                AffiliateChannelCommissionCampaign dto = repository.save(map);
            });

        } else {
            AffiliateChannelCommissionCampaign map = mapper.map(request, AffiliateChannelCommissionCampaign.class);
            map.setCreationDate(LocalDateTime.now());
            map.setLastModificationDate(LocalDateTime.now());

            Affiliate affiliate = affiliateRepository.findById(request.getAffiliateId()).orElseThrow(() -> new ElementCleveradException("Affilirte", request.getAffiliateId()));
            map.setAffiliate(affiliate);
            Commission commission = commissionRepository.findById(request.getCommissionId()).orElseThrow(() -> new ElementCleveradException("Commission", request.getCommissionId()));
            map.setCommission(commission);
            Campaign campaign = campaignRepository.findById(request.getCampaignId()).orElseThrow(() -> new ElementCleveradException("Campaign", request.getCampaignId()));
            map.setCampaign(campaign);
            //canale Singolo
            map.setChannel(channelRepository.findById(request.getChannelId()).orElseThrow(() -> new ElementCleveradException("Channel", request.getChannelId())));
            AffiliateChannelCommissionCampaignDTO.from(repository.save(map));
        }

        if (campaignAffiliateBusiness.searchByAffiliateIdAndCampaignId(request.getAffiliateId(), request.getCampaignId()).getTotalElements() == 0) {
            // assicio affiliate a campagna
            campaignAffiliateBusiness.create(new CampaignAffiliateBusiness.BaseCreateRequest(request.getCampaignId(), request.getAffiliateId(), null, null));
            // inoltro mail di invito a  camapgna
            MailService.BaseCreateRequest reqMail = new MailService.BaseCreateRequest();
            reqMail.setAffiliateId(request.getAffiliateId());
            reqMail.setCampaignId(request.getCampaignId());
            mailService.invitoCampagna(reqMail);
        }
        return null;
    }

    // DELETE BY ID
    public void delete(Long id) {
        try {
            AffiliateChannelCommissionCampaign affiliateChannelCommissionCampaign = repository.findById(id).orElseThrow(() -> new ElementCleveradException("AffiliateChannelCommissionCampaign", id));
            repository.deleteById(affiliateChannelCommissionCampaign.getId());
            // se non ci sono altre commissioni per la campagna e l'affiliato cancello
            if (searchByCampaignIdAffiliateId(affiliateChannelCommissionCampaign.getCampaign().getId(), affiliateChannelCommissionCampaign.getAffiliate().getId(), PageRequest.of(0, Integer.MAX_VALUE)).getTotalElements() == 0)
                campaignAffiliateBusiness.deleteByCampaignIdAndAffiliateId(affiliateChannelCommissionCampaign.getCampaign().getId(), affiliateChannelCommissionCampaign.getAffiliate().getId());
        } catch (ConstraintViolationException ex) {
            throw ex;
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    public void deletebyCampaignAndCommission(Long campaignId, Long commissionId) {
        try {
            Filter request = new Filter();
            request.setCampaignId(campaignId);
            request.setCommissionId(commissionId);
            Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("id")));
            Page<AffiliateChannelCommissionCampaign> page = repository.findAll(getSpecification(request), pageable);
            page.stream().spliterator().forEachRemaining(affiliateChannelCommissionCampaign -> repository.deleteById(affiliateChannelCommissionCampaign.getId()));
        } catch (ConstraintViolationException ex) {
            throw ex;
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    // GET BY ID
    public AffiliateChannelCommissionCampaignDTO findById(Long id) {
        AffiliateChannelCommissionCampaign affiliateChannelCommissionCampaign = repository.findById(id).orElseThrow(() -> new ElementCleveradException("AffiliateChannelCommissionCampaign", id));
        return AffiliateChannelCommissionCampaignDTO.from(affiliateChannelCommissionCampaign);
    }

    // GET BY ID CAMPAIGN
    public Page<AffiliateChannelCommissionCampaignDTO> searchByCampaignId(Long campaignId, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Filter filter = new Filter();
        filter.setCampaignId(campaignId);
        Page<AffiliateChannelCommissionCampaign> page = repository.findAll(getSpecification(filter), pageable);
        return page.map(AffiliateChannelCommissionCampaignDTO::from);
    }

//    public Page<AffiliateChannelCommissionCampaignDTO> searchByCampaignIdDistinctAffiliate(Long campaignId, Pageable pageableRequest) {
//        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
//        Filter filter = new Filter();
//        filter.setCampaignId(campaignId);
//        Page<AffiliateChannelCommissionCampaign> page = repository.findAll(getSpecification(filter), pageable);
//
//        Map<Long, String> list = new HashMap<>();
//        page.stream().forEach(affiliateChannelCommissionCampaign -> list.put(affiliateChannelCommissionCampaign.getAffiliate().getId(), affiliateChannelCommissionCampaign.getAffiliate().getName()));
//
//        page.stream().filter(affiliateChannelCommissionCampaign -> affiliateChannelCommissionCampaign.getAffiliate().getId())
//
//        return page.map(AffiliateChannelCommissionCampaignDTO::from);
//    }

    public Page<AffiliateChannelCommissionCampaignDTO> searchByCampaignIdAndType(Long campaignId, Long typeDictId, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Filter filter = new Filter();
        filter.setCampaignId(campaignId);
        filter.setCommissionDicId(typeDictId);
        Page<AffiliateChannelCommissionCampaign> page = repository.findAll(getSpecification(filter), pageable);
        return page.map(AffiliateChannelCommissionCampaignDTO::from);
    }

    public Page<AffiliateChannelCommissionCampaignDTO> searchByCampaignIdAffiliateNotZero(Long campaignId, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Filter filter = new Filter();
        filter.setCampaignId(campaignId);
        if (!jwtUserDetailsService.isAdmin())
            filter.setAffiliateId(jwtUserDetailsService.getAffiliateID());
        filter.setNotzero(true);
        Page<AffiliateChannelCommissionCampaign> page = repository.findAll(getSpecification(filter), pageable);
        return page.map(AffiliateChannelCommissionCampaignDTO::from);
    }

    public Page<AffiliateChannelCommissionCampaignDTO> searchByCampaignIdAffiliateId(Long campaignId, Long affiliateId, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Filter filter = new Filter();
        filter.setCampaignId(campaignId);
        filter.setAffiliateId(affiliateId);
        Page<AffiliateChannelCommissionCampaign> page = repository.findAll(getSpecification(filter), pageable);
        return page.map(AffiliateChannelCommissionCampaignDTO::from);
    }

    public Page<AffiliateChannelCommissionCampaignDTO> searchByCampaignIdAffiliate(Long campaignId, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Filter filter = new Filter();
        filter.setCampaignId(campaignId);
        Page<AffiliateChannelCommissionCampaign> page = repository.findAll(getSpecification(filter), pageable);
        return page.map(AffiliateChannelCommissionCampaignDTO::from);
    }

    // SEARCH searchScheduledActivities
    public AffiliateChannelCommissionCampaign searchScheduledActivities(Filter request) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("id")));
        Page<AffiliateChannelCommissionCampaign> page = repository.findAll(getSpecification(request), pageable);

        if (page.getTotalPages() > 0) {
            return page.getContent().get(0);
        } else return null;
    }

    public Page<AffiliateChannelCommissionCampaignDTO> search(Filter request, Pageable pageableR) {
        Pageable pageable = PageRequest.of(pageableR.getPageNumber(), pageableR.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<AffiliateChannelCommissionCampaign> page = repository.findAll(getSpecification(request), pageable);
        return page.map(AffiliateChannelCommissionCampaignDTO::from);
    }

    public Page<AffiliateChannelCommissionCampaignDTO> search(Filter request) {
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Order.asc("id")));
        Page<AffiliateChannelCommissionCampaign> page = repository.findAll(getSpecification(request), pageable);
        return page.map(AffiliateChannelCommissionCampaignDTO::from);
    }

    /**
     * ============================================================================================================
     **/
    private Specification<AffiliateChannelCommissionCampaign> getSpecification(AffiliateChannelCommissionCampaignBusiness.Filter request) {
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
            if (request.getChannelId() != null) {
                predicates.add(cb.equal(root.get("channel").get("id"), request.getChannelId()));
            }
            if (request.getCommissionDicId() != null) {
                predicates.add(cb.equal(root.get("commission").get("dictionary").get("id"), request.getCommissionDicId()));
            }
            if (request.getCommissionId() != null) {
                predicates.add(cb.equal(root.get("commission").get("id"), request.getCommissionId()));
            }
            if (request.getNotzero() != null) {
                predicates.add(cb.notEqual(root.get("commission").get("value"), "0"));
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
    @ToString
    public static class BaseCreateRequest {
        private Long campaignId;
        private Long affiliateId;
        private Long channelId;
        private Long commissionId;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private Long campaignId;
        private Long affiliateId;
        private Long channelId;
        private Long commissionId;
        private Boolean notzero;
        private Long commissionDicId;
    }

}