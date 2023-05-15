package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.config.model.Refferal;
import it.cleverad.engine.persistence.model.service.Affiliate;
import it.cleverad.engine.persistence.model.service.Campaign;
import it.cleverad.engine.persistence.model.service.Channel;
import it.cleverad.engine.persistence.model.tracking.Cpc;
import it.cleverad.engine.persistence.repository.service.AffiliateRepository;
import it.cleverad.engine.persistence.repository.service.CampaignRepository;
import it.cleverad.engine.persistence.repository.service.ChannelRepository;
import it.cleverad.engine.persistence.repository.tracking.CpcRepository;
import it.cleverad.engine.service.JwtUserDetailsService;
import it.cleverad.engine.service.ReferralService;
import it.cleverad.engine.web.dto.CpcDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import it.cleverad.engine.web.exception.PostgresDeleteCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional
public class CpcBusiness {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private CpcRepository repository;
    @Autowired
    private ReferralService referralService;
    @Autowired
    private CampaignRepository campaignRepository;
    @Autowired
    private AffiliateRepository affiliateRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public CpcDTO create(BaseCreateRequest request) {
        Cpc map = mapper.map(request, Cpc.class);
        map.setDate(LocalDateTime.now());
        map.setRead(false);
        return CpcDTO.from(repository.save(map));
    }

    // GET BY ID
    public CpcDTO findById(Long id) {
        Cpc channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Cpc", id));
        return CpcDTO.from(channel);
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
    public Page<CpcDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        Page<Cpc> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CpcDTO::from);
    }

    // SEARCH PAGINATED
    public Page<CpcDTO> searchWithReferral(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        Page<Cpc> page = repository.findAll(getSpecification(request), pageable);
        Page<CpcDTO> res = page.map(CpcDTO::from);
        List<CpcDTO> exp = new ArrayList<>();
        res.stream().forEach(cpcDTO -> {
            if (StringUtils.isNotBlank(cpcDTO.getRefferal()) && !cpcDTO.getRefferal().contains("{{refferalId}}")) {
                Refferal referral = referralService.decodificaReferral(cpcDTO.getRefferal());

                Campaign campaign = campaignRepository.findById(referral.getCampaignId()).orElse(null);
                if (referral.getCampaignId() != null && campaign != null && campaign.getName() != null) {
                    cpcDTO.setCampaignName(campaign.getName());
                    cpcDTO.setCampaignId(referral.getCampaignId());
                }

                if (referral.getAffiliateId() != null) {
                    Affiliate affiliate = affiliateRepository.findById(referral.getAffiliateId()).orElse(null);
                    if (referral.getAffiliateId() != null && affiliate != null && affiliate.getName() != null) {
                        cpcDTO.setAffiliateName(affiliate.getName());
                        cpcDTO.setAffiliateId(referral.getAffiliateId());
                    }
                }
                if (referral.getChannelId() != null) {
                    Channel channel = channelRepository.findById(referral.getChannelId()).orElse(null);
                    if (referral.getChannelId() != null && channel != null && channel.getName() != null) {
                        cpcDTO.setChannelName(channel.getName());
                        cpcDTO.setChannelId(referral.getChannelId());
                    }
                }
            } else {
                cpcDTO.setRefferal("");
            }

            if (jwtUserDetailsService.isAdmin()) {
                exp.add(cpcDTO);
            } else if (!cpcDTO.getRefferal().equals("") && cpcDTO.getAffiliateId().equals(jwtUserDetailsService.getAffiliateID())) {
                exp.add(cpcDTO);
            }

        });
        Page<CpcDTO> pages = new PageImpl<CpcDTO>(exp, pageable, page.getTotalElements());
        return pages;
    }

    // UPDATE
    public CpcDTO update(Long id, Filter filter) {
        Cpc channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Cpc", id));
        CpcDTO campaignDTOfrom = CpcDTO.from(channel);
        mapper.map(filter, campaignDTOfrom);
        Cpc mappedEntity = mapper.map(channel, Cpc.class);
        mapper.map(campaignDTOfrom, mappedEntity);
        return CpcDTO.from(repository.save(mappedEntity));
    }

    public Page<CpcDTO> getUnread() {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc("refferal")));
        Filter request = new Filter();
        request.setRead(false);
        Page<Cpc> page = repository.findAll(getSpecification(request), pageable);
        log.trace("UNREAD {}", page.getTotalElements());
        return page.map(CpcDTO::from);
    }

    public Page<CpcDTO> getUnreadDayBefore() {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc("refferal")));
        Filter request = new Filter();
        request.setRead(false);
        request.setDateFrom(LocalDate.now().minusDays(1));
        request.setDateTo(LocalDate.now().minusDays(1));
        Page<Cpc> page = repository.findAll(getSpecification(request), pageable);
        log.info("UNREAD CPC :: {}", page.getTotalElements());
        return page.map(CpcDTO::from);
    }

    public Page<CpcDTO> getUnreadOneHourBefore() {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc("refferal")));
        Filter request = new Filter();
        request.setRead(false);
        LocalDateTime oraSpaccata = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        request.setDatetimeFrom(oraSpaccata.minusHours(1));
        request.setDatetimeTo(oraSpaccata);
        Page<Cpc> page = repository.findAll(getSpecification(request), pageable);
        log.info("\n\n\n >>>>>>>>>>>>>>>>>>>>>> UNREAD CPC HOUR BEFORE :: {}", page.getTotalElements());
        return page.map(CpcDTO::from);
    }

    public Page<CpcDTO> findByIp24HoursBefore(String ip, LocalDateTime dateTime) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc("refferal")));
        Filter request = new Filter();
        request.setIp(ip);
        request.setDatetimeFrom(dateTime.minusDays(1));
        request.setDatetimeTo(dateTime);
        Page<Cpc> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CpcDTO::from);
    }
    public Page<CpcDTO> findByIpTwoHoursBefore(String ip, LocalDateTime dateTime) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc("refferal")));
        Filter request = new Filter();
        request.setIp(ip);
        request.setDatetimeFrom(dateTime.minusHours(2));
        request.setDatetimeTo(dateTime);
        Page<Cpc> page = repository.findAll(getSpecification(request), pageable);
        log.trace("FIND IP CPC :: {}", page.getTotalElements());
        return page.map(CpcDTO::from);
    }

    public Page<CpcDTO> getAllDayBefore() {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc("refferal")));
        Filter request = new Filter();
        request.setDateFrom(LocalDate.now().minusDays(1));
        request.setDateTo(LocalDate.now().minusDays(1));
        Page<Cpc> page = repository.findAll(getSpecification(request), pageable);
        log.info("UNREAD CPC :: {}", page.getTotalElements());
        return page.map(CpcDTO::from);
    }


    public void setRead(long id) {
        Cpc cpc = repository.findById(id).get();
        cpc.setRead(true);
        repository.save(cpc);
    }

    /**
     * ============================================================================================================
     **/
    private Specification<Cpc> getSpecification(Filter request) {
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
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), request.getDateTo().atTime(LocalTime.MAX)));
            }

            if (request.getDatetimeFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), request.getDatetimeFrom()));
            }
            if (request.getDatetimeTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), request.getDatetimeTo()));
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
        private String refferal;
        private String ip;
        private String agent;
        private Boolean read;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateFrom;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateTo;
        private LocalDateTime datetimeFrom;
        private LocalDateTime datetimeTo;
    }

}
