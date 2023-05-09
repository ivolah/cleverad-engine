package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.config.model.Refferal;
import it.cleverad.engine.persistence.model.service.Affiliate;
import it.cleverad.engine.persistence.model.service.Campaign;
import it.cleverad.engine.persistence.model.service.Channel;
import it.cleverad.engine.persistence.model.tracking.Cpm;
import it.cleverad.engine.persistence.repository.service.AffiliateRepository;
import it.cleverad.engine.persistence.repository.service.CampaignRepository;
import it.cleverad.engine.persistence.repository.service.ChannelRepository;
import it.cleverad.engine.persistence.repository.tracking.CpmRepository;
import it.cleverad.engine.service.JwtUserDetailsService;
import it.cleverad.engine.service.ReferralService;
import it.cleverad.engine.web.dto.CpmDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import it.cleverad.engine.web.exception.PostgresDeleteCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional
public class CpmBusiness {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private CpmRepository repository;
    @Autowired
    private Mapper mapper;
    @Autowired
    private CampaignRepository campaignRepository;
    @Autowired
    private AffiliateRepository affiliateRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private ReferralService referralService;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public CpmDTO create(BaseCreateRequest request) {
        Cpm map = mapper.map(request, Cpm.class);
        map.setRead(false);
        map.setDate(LocalDateTime.now());
        if (request.getRefferal() != null) {
            Refferal refferal = referralService.decodificaReferral(request.getRefferal());
            map.setImageId(refferal.getCampaignId());
            map.setMediaId(refferal.getMediaId());
        }
        return CpmDTO.from(repository.save(map));
    }

    // GET BY ID
    public CpmDTO findById(Long id) {
        Cpm channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Cpm", id));
        return CpmDTO.from(channel);
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
    public Page<CpmDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        Page<Cpm> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CpmDTO::from);
    }

    public Page<CpmDTO> searchWithReferral(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        Page<Cpm> page = repository.findAll(getSpecification(request), pageable);
        Page<CpmDTO> res = page.map(CpmDTO::from);
        List<CpmDTO> exp = new ArrayList<>();
        res.stream().forEach(cpm -> {
            if (!cpm.getRefferal().contains("{{refferalId}}")) {
                Refferal refferal = referralService.decodificaReferral(cpm.getRefferal());

                Affiliate affiliate = affiliateRepository.findById(refferal.getAffiliateId()).orElse(null);
                if (refferal.getAffiliateId() != null && affiliate != null && affiliate.getName() != null) {
                    cpm.setAffiliateName(affiliate.getName());
                    cpm.setAffiliateId(refferal.getAffiliateId());
                }

                Campaign campaign = campaignRepository.findById(refferal.getCampaignId()).orElse(null);
                if (refferal.getCampaignId() != null && campaign != null && campaign.getName() != null) {
                    cpm.setCampaignName(campaign.getName());
                    cpm.setCampaignId(refferal.getCampaignId());
                }

                Channel channel = channelRepository.findById(refferal.getChannelId()).orElse(null);
                if (refferal.getChannelId() != null && channel != null && channel.getName() != null) {
                    cpm.setChannelName(channel.getName());
                    cpm.setChannelId(refferal.getChannelId());
                }
            } else {
                cpm.setRefferal("");
            }

            if (jwtUserDetailsService.isAdmin()) {
                exp.add(cpm);
            } else if (!cpm.getRefferal().equals("") && cpm.getAffiliateId().equals(jwtUserDetailsService.getAffiliateID())) {
                exp.add(cpm);
            }
        });
        Page<CpmDTO> pages = new PageImpl<CpmDTO>(exp, pageable, page.getTotalElements());
        return pages;
    }

    // UPDATE
    public CpmDTO update(Long id, Filter filter) {
        Cpm channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Cpm", id));
        CpmDTO campaignDTOfrom = CpmDTO.from(channel);

        mapper.map(filter, campaignDTOfrom);

        Cpm mappedEntity = mapper.map(channel, Cpm.class);
        mapper.map(campaignDTOfrom, mappedEntity);

        return CpmDTO.from(repository.save(mappedEntity));
    }

    public Page<CpmDTO> getUnread() {
        Pageable pageable = PageRequest.of(0, 1000, Sort.by(Sort.Order.desc("id")));
        Filter request = new Filter();
        request.setRead(false);
        Page<Cpm> page = repository.findAll(getSpecification(request), pageable);
        log.trace("UNREAD {}", page.getTotalElements());
        return page.map(CpmDTO::from);
    }

    public void setRead(long id) {
        Cpm cpm = repository.findById(id).get();
        cpm.setRead(true);
        repository.save(cpm);
    }

    public Page<CpmDTO> getUnreadDayBefore() {
        Filter request = new Filter();
        request.setRead(false);
        request.setDateFrom(LocalDate.now().minusDays(1));
        request.setDateTo(LocalDate.now().minusDays(1));
        Page<Cpm> page = repository.findAll(getSpecification(request), PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc("refferal"))));
        log.info("UNREAD CPM :: {}", page.getTotalElements());
        return page.map(CpmDTO::from);
    }

    public Page<CpmDTO> getAllDayBefore() {
        Filter request = new Filter();
        request.setDateFrom(LocalDate.now().minusDays(1));
        request.setDateTo(LocalDate.now().minusDays(1));
        Page<Cpm> page = repository.findAll(getSpecification(request), PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc("refferal"))));
        log.info("UNREAD CPM :: {}", page.getTotalElements());
        return page.map(CpmDTO::from);
    }


    /**
     * ============================================================================================================
     **/
    private Specification<Cpm> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getRefferal() != null) {
                predicates.add(cb.equal(root.get("refferal"), request.getRefferal()));
            }
            if (request.getRead() != null) {
                predicates.add(cb.equal(root.get("read"), request.getRead()));
            }
            if (request.getIp() != null) {
                predicates.add(cb.equal(root.get("ip"), request.getIp()));
            }
            if (request.getDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), request.getDateFrom().atStartOfDay()));
            }
            if (request.getDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), request.getDateTo().plus(1, ChronoUnit.DAYS).atStartOfDay()));
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
        private String refferal;
        private String ip;
        private String agent;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;

        private Long campaignId;
        private Long imageId;
        private Long mediaId;

        private String refferal;
        private String ip;
        private String agent;

        private Boolean read;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateFrom;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateTo;
    }

}
