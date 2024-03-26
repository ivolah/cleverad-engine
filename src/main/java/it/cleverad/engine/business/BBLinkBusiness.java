package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.persistence.model.service.BBLink;
import it.cleverad.engine.persistence.model.service.Commission;
import it.cleverad.engine.persistence.repository.service.AffiliateRepository;
import it.cleverad.engine.persistence.repository.service.BBLinkRepository;
import it.cleverad.engine.persistence.repository.service.CampaignRepository;
import it.cleverad.engine.persistence.repository.service.CommissionRepository;
import it.cleverad.engine.service.ReferralService;
import it.cleverad.engine.service.tinyurl.TinyData;
import it.cleverad.engine.service.tinyurl.TinyUrlService;
import it.cleverad.engine.web.dto.BBLinkDTO;
import it.cleverad.engine.web.exception.BrandBuddiesMediaTargetException;
import it.cleverad.engine.web.exception.ElementCleveradException;
import it.cleverad.engine.web.exception.PostgresDeleteCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
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
import java.util.concurrent.atomic.AtomicReference;

/**
 * ============================================================================================================
 **/

@Slf4j
@Component
@Transactional
public class BBLinkBusiness {

    @Autowired
    private BBLinkRepository repository;
    @Autowired
    private CampaignRepository campaignRepository;
    @Autowired
    private AffiliateRepository affiliateRepository;
    @Autowired
    private MediaBusiness mediaBusiness;
    @Autowired
    private ChannelBusiness channelBusiness;
    @Autowired
    private CommissionRepository commissionRepository;
    @Autowired
    private TinyUrlService tinyUrlService;
    @Autowired
    private ReferralService referralService;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public BBLinkDTO create(BaseCreateRequest request) {

        final BBLinkDTO[] dto = {new BBLinkDTO()};

        AtomicReference<Boolean> salta = new AtomicReference<>(true);
        // trova media
        mediaBusiness.searchBB().stream().forEach(mediaDTO -> {

            // trova target
            List<Long> chanelBBs = channelBusiness.getBrandBuddies(jwtUserDetailsService.getAffiliateId());
            String channelId = "0";
            if (chanelBBs.size() > 0) channelId = String.valueOf(chanelBBs.get(0));

            // pulisco indirizzi
            String link = request.getLink().replace("http://", "").replace("https://", "").replace("www.", "");
            String url = mediaDTO.getTarget().replace("http://", "").replace("https://", "").replace("www.", "");

            //verifica se link viene accettato
            if (link.startsWith(url)) {
                //log.info("LINK {} :: >>> TARGET :: {}", link, url);
                Long campaignId = mediaDTO.getCampaignId();
                String referral = referralService.creaEncoding(Long.toString(campaignId), String.valueOf(mediaDTO.getId()), String.valueOf(jwtUserDetailsService.getAffiliateId()), channelId, "0");
                //generazione short link
                String alias = "BB-" + RandomStringUtils.randomAlphanumeric(6);
                TinyData tinyUrlData = tinyUrlService.createShort(alias, "https://tracking.cleveradserver.com/click?refId=" + referral + "&urlRef=" + request.getLink());
                if (tinyUrlData != null) {

                    BBLink map = mapper.map(request, BBLink.class);
                    map.setGenerated(tinyUrlData.getData().getTinyUrl());
                    map.setLink(request.getLink());
                    map.setReferral(referral);
                    map.setCreationDate(LocalDateTime.now());

                    if (campaignRepository.findByIdAndCommissionCampaigns_Dictionary_Id(campaignId, 84L).size() > 0 || campaignRepository.findByIdAndCommissionCampaigns_Dictionary_Id(campaignId, 85L).size() > 0) {
                        Long comid = null;
                        comid = campaignRepository.findById(campaignId).get().getCommissionCampaigns().stream().filter(commission -> {
                            if (commission.getDictionary().getId().equals(84L) || commission.getDictionary().getId().equals(85L))
                                return true;
                            else return false;
                        }).findFirst().get().getId();
                        Commission commission = commissionRepository.findById(comid).get();
                        map.setCommission(commission);
                    }

                    map.setAffiliate(affiliateRepository.findById(jwtUserDetailsService.getAffiliateId()).orElseThrow(() -> new ElementCleveradException("Affilaite", jwtUserDetailsService.getAffiliateId())));
                    map.setCampaign(campaignRepository.findById(campaignId).orElseThrow(() -> new ElementCleveradException("Campaign", campaignId)));

                    dto[0] = BBLinkDTO.from(repository.save(map));
                    salta.set(false);

                } else {
                    log.warn("TINYURL VUOTO {}:{}", link, url);
                    throw new BrandBuddiesMediaTargetException("TINYURL VUOTO " + link + ":" + url);
                }// else
            } // if
        });

        if (salta.get())
            throw new BrandBuddiesMediaTargetException("LINK " + request.link + " != TUTTI TARGET");

        return dto[0];
    }

    // GET BY ID
    public BBLinkDTO findById(Long id) {
        BBLink platform = repository.findById(id).orElseThrow(() -> new ElementCleveradException("BBLink", id));
        return BBLinkDTO.from(platform);
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
    public Page<BBLinkDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("creationDate")));
        Page<BBLink> page = repository.findAll(getSpecification(request), pageable);
        return page.map(BBLinkDTO::from);
    }

    /**
     * ============================================================================================================
     **/

    private Specification<BBLink> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getLink() != null) {
                predicates.add(cb.equal(root.get("name"), request.getLink()));
            }
            if (request.getCampaignId() != null) {
                predicates.add(cb.equal(root.get("campaign").get("id"), request.getCampaignId()));
            }
            if (request.getBrandbuddiesId() != null) {
                predicates.add(cb.equal(root.get("affiliate").get("id"), request.getBrandbuddiesId()));
            }

            completePredicate = cb.and(predicates.toArray(new Predicate[0]));

            return completePredicate;
        };
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BaseCreateRequest {
        private String link;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private String link;
        private String generated;
        private Long campaignId;
        private Long brandbuddiesId;
    }

}