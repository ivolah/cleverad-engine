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
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

        // cerco se multiplo affiliato canale campagna già presente e lo blocco
        Filter req = new Filter();
        req.setBlocked(false);
        req.setAffiliateId(request.getAffiliateId());
        req.setCampaignId(request.getCampaignId());
        req.setChannelId(request.getChannelId());
        List<Long> ids = this.search(req).stream().mapToLong(AffiliateChannelCommissionCampaignDTO::getId).boxed().collect(Collectors.toList());

        Boolean blocco = false;
        if (request.getChannelId() == 0) {
            log.info(request.toString());
            channelBusiness.getbyIdAffiliateAllActive(request.getAffiliateId()).forEach(idCanale -> {
                AffiliateChannelCommissionCampaign map = mapper.map(request, AffiliateChannelCommissionCampaign.class);
                map.setCreationDate(LocalDateTime.now());
                Affiliate affiliate = affiliateRepository.findById(request.getAffiliateId()).orElseThrow(() -> new ElementCleveradException("Affilirte", request.getAffiliateId()));
                map.setAffiliate(affiliate);
                Commission commission = commissionRepository.findById(request.getCommissionId()).orElseThrow(() -> new ElementCleveradException("Commission", request.getCommissionId()));
                map.setCommission(commission);
                Campaign campaign = campaignRepository.findById(request.getCampaignId()).orElseThrow(() -> new ElementCleveradException("Campaign", request.getCampaignId()));
                map.setCampaign(campaign);
                map.setChannel(channelRepository.findById(idCanale).orElseThrow(() -> new ElementCleveradException("Channel", idCanale)));
                map.setBlocked(false);
                repository.save(map);
            });
            blocco = true;
        } else {
            AffiliateChannelCommissionCampaign map = mapper.map(request, AffiliateChannelCommissionCampaign.class);
            map.setCreationDate(LocalDateTime.now());
            Affiliate affiliate = affiliateRepository.findById(request.getAffiliateId()).orElseThrow(() -> new ElementCleveradException("Affilirte", request.getAffiliateId()));
            map.setAffiliate(affiliate);
            Commission commission = commissionRepository.findById(request.getCommissionId()).orElseThrow(() -> new ElementCleveradException("Commission", request.getCommissionId()));
            map.setCommission(commission);
            Campaign campaign = campaignRepository.findById(request.getCampaignId()).orElseThrow(() -> new ElementCleveradException("Campaign", request.getCampaignId()));
            map.setCampaign(campaign);
            //canale Singolo
            map.setChannel(channelRepository.findById(request.getChannelId()).orElseThrow(() -> new ElementCleveradException("Channel", request.getChannelId())));
            map.setBlocked(false);
            repository.save(map);
            blocco = true;
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

        if (blocco)
            ids.stream().forEach(id ->{
                log.info("id  = " + id);
                this.block(id);
            });

        return null;
    }

    /**
     * ============================================================================================================
     **/

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

    /**
     * ============================================================================================================
     **/

    // BLOCK
    public AffiliateChannelCommissionCampaignDTO block(Long id) {
        AffiliateChannelCommissionCampaign accc = repository.findById(id).orElseThrow(() -> new ElementCleveradException("AffiliateChannelCommissionCampaign", id));
        accc.setBlocked(true);
        return AffiliateChannelCommissionCampaignDTO.from(repository.save(accc));
    }

    // UNBLOCK
    public AffiliateChannelCommissionCampaignDTO unblock(Long id) {
        AffiliateChannelCommissionCampaign accc = repository.findById(id).orElseThrow(() -> new ElementCleveradException("AffiliateChannelCommissionCampaign", id));
        accc.setBlocked(false);
        return AffiliateChannelCommissionCampaignDTO.from(repository.save(accc));
    }

    /**
     * ============================================================================================================
     **/

    // GET BY ID
    public AffiliateChannelCommissionCampaignDTO findById(Long id) {
        AffiliateChannelCommissionCampaign affiliateChannelCommissionCampaign = repository.findById(id).orElseThrow(() -> new ElementCleveradException("AffiliateChannelCommissionCampaign", id));
        return AffiliateChannelCommissionCampaignDTO.from(affiliateChannelCommissionCampaign);
    }

    // GET BY ID CAMPAIGN
    public Page<AffiliateChannelCommissionCampaignDTO> searchByCampaignId(Long campaignId, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize());
        Filter filter = new Filter();
        filter.setCampaignId(campaignId);
        Page<AffiliateChannelCommissionCampaign> page = repository.findAll(getSpecification(filter), Pageable.unpaged());
        List<AffiliateChannelCommissionCampaignDTO> ll = page.map(AffiliateChannelCommissionCampaignDTO::from).toList();
        List<AffiliateChannelCommissionCampaignDTO> modifiableList = new ArrayList<>(ll);
        modifiableList = modifiableList.stream().sorted(Comparator.comparing(AffiliateChannelCommissionCampaignDTO::getAffilateName)).collect(Collectors.toList());
        return new PageImpl<>(modifiableList, pageableRequest, page.getTotalElements());
    }

    // GET BY ID CAMPAIGN
    public Page<AffiliateChannelCommissionCampaignDTO> searchByCampaignIdAffiliateBrandBuddies(Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Filter filter = new Filter();
        if (Boolean.FALSE.equals(jwtUserDetailsService.isAdmin()))
            filter.setAffiliateId(jwtUserDetailsService.getAffiliateId());
        filter.setBb(true);
        Page<AffiliateChannelCommissionCampaign> page = repository.findAll(getSpecification(filter), pageable);
        return page.map(AffiliateChannelCommissionCampaignDTO::from);
    }

    public Page<AffiliateChannelCommissionCampaignDTO> searchByCampaignIdAndType(Long campaignId, Long affiliateId, Long typeDictId, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Filter filter = new Filter();
        filter.setCampaignId(campaignId);
        filter.setCommissionDicId(typeDictId);
        filter.setAffiliateId(affiliateId);
        Page<AffiliateChannelCommissionCampaign> page = repository.findAll(getSpecification(filter), pageable);
        return page.map(AffiliateChannelCommissionCampaignDTO::from);
    }

    public Page<AffiliateChannelCommissionCampaignDTO> searchByCampaignIdAffiliateNotZero(Long campaignId, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Filter filter = new Filter();
        filter.setCampaignId(campaignId);
        if (Boolean.FALSE.equals(jwtUserDetailsService.isAdmin()))
            filter.setAffiliateId(jwtUserDetailsService.getAffiliateId());
        filter.setNotzero(true);

        Page<AffiliateChannelCommissionCampaign> page = repository.findAll(getSpecification(filter), Pageable.unpaged());
        List<AffiliateChannelCommissionCampaignDTO> ll = page.map(AffiliateChannelCommissionCampaignDTO::from).toList();

        List<AffiliateChannelCommissionCampaignDTO> modifiableList = new ArrayList<>(ll);
        modifiableList = modifiableList.stream().sorted(Comparator.comparing(AffiliateChannelCommissionCampaignDTO::getAffilateName)).collect(Collectors.toList());

        return new PageImpl<>(modifiableList, pageableRequest, page.getTotalElements());
    }

    public Page<AffiliateChannelCommissionCampaignDTO> searchByCampaignIdAffiliateId(Long campaignId, Long affiliateId, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Filter filter = new Filter();
        filter.setCampaignId(campaignId);
        filter.setAffiliateId(affiliateId);
        Page<AffiliateChannelCommissionCampaign> page = repository.findAll(getSpecification(filter), pageable);
        return page.map(AffiliateChannelCommissionCampaignDTO::from);
    }

    public Page<AffiliateChannelCommissionCampaignDTO> search(Filter request, Pageable pageableR) {
        Pageable pageable = PageRequest.of(pageableR.getPageNumber(), pageableR.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<AffiliateChannelCommissionCampaign> page = repository.findAll(getSpecification(request), pageable);
        return page.map(AffiliateChannelCommissionCampaignDTO::from);
    }

    public Page<AffiliateChannelCommissionCampaignDTO> search(Filter request) {
        Pageable pageable = PageRequest.of(0, 300, Sort.by(Sort.Order.asc("id")));
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

            if (request.getBb() != null) {
                predicates.add(cb.or(
                        cb.equal(root.get("commission").get("dictionary").get("id"), 84L),
                        cb.equal(root.get("commission").get("dictionary").get("id"), 85L)
                ));
                predicates.add(cb.equal(root.get("campaign").get("status"), true));
            }

            if (request.getBlocked() != null) {
                predicates.add(cb.equal(root.get("blocked"), request.getBlocked()));
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
    @ToString
    public static class Filter {
        private Long id;
        private Long campaignId;
        private Long affiliateId;
        private Long channelId;
        private Long commissionId;
        private Boolean notzero;
        private Boolean bb;
        private Long commissionDicId;
        private Boolean blocked;
    }

}