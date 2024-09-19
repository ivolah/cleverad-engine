package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.persistence.model.service.Affiliate;
import it.cleverad.engine.persistence.model.service.Campaign;
import it.cleverad.engine.persistence.model.service.Channel;
import it.cleverad.engine.persistence.model.service.ClickMultipli;
import it.cleverad.engine.persistence.model.tracking.Cpc;
import it.cleverad.engine.persistence.repository.service.AffiliateRepository;
import it.cleverad.engine.persistence.repository.service.CampaignRepository;
import it.cleverad.engine.persistence.repository.service.ChannelRepository;
import it.cleverad.engine.persistence.repository.tracking.CpcRepository;
import it.cleverad.engine.web.dto.tracking.CpcDTO;
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
        if (jwtUserDetailsService.isAdmin()) {
            Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
            Page<CpcDTO> page = repository.findAll(getSpecification(request), pageable).map(CpcDTO::from);
            List<CpcDTO> exp = new ArrayList<>();
            page.stream().forEach(cpcDTO -> {
                if (cpcDTO.getCampaignId() != null) {
                    Campaign campaign = campaignRepository.findById(cpcDTO.getCampaignId()).orElse(null);
                    if (cpcDTO.getCampaignId() != null && campaign != null && campaign.getName() != null)
                        cpcDTO.setCampaignName(campaign.getName());
                }
                if (cpcDTO.getAffiliateId() != null) {
                    Affiliate affiliate = affiliateRepository.findById(cpcDTO.getAffiliateId()).orElse(null);
                    if (cpcDTO.getAffiliateId() != null && affiliate != null && affiliate.getName() != null)
                        cpcDTO.setAffiliateName(affiliate.getName());
                }
                if (cpcDTO.getChannelId() != null) {
                    Channel channel = channelRepository.findById(cpcDTO.getChannelId()).orElse(null);
                    if (cpcDTO.getChannelId() != null && channel != null && channel.getName() != null)
                        cpcDTO.setChannelName(channel.getName());
                }
                exp.add(cpcDTO);
            });
            return new PageImpl<>(exp, pageable, page.getTotalElements());
        } else {
            return null;
        }
    }

    // UPDATE
    public CpcDTO update(Long id, Filter filter) {
        Cpc cpc = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Cpc", id));
        mapper.map(filter, cpc);
        return CpcDTO.from(repository.save(cpc));
    }

    public Page<CpcDTO> getUnreadDayNotBlackilset() {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc("refferal")));
        Filter request = new Filter();
        request.setRead(false);
        request.setBlacklisted(false);
        LocalDateTime oraSpaccata = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        request.setDatetimeFrom(oraSpaccata.toLocalDate().atStartOfDay());
        request.setDatetimeTo(LocalDateTime.now());
        Page<Cpc> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CpcDTO::from);
    }

    public Page<CpcDTO> getUnreadBlacklisted() {
        Filter request = new Filter();
        request.setRead(false);
        request.setBlacklisted(true);
        LocalDateTime oraSpaccata = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        request.setDatetimeFrom(oraSpaccata.toLocalDate().atStartOfDay());
        request.setDatetimeTo(LocalDateTime.now());
        Page<Cpc> page = repository.findAll(getSpecification(request), PageRequest.of(0, Integer.MAX_VALUE));
        return page.map(CpcDTO::from);
    }

    public Page<CpcDTO> findByIp24HoursBefore(String ip, LocalDateTime dateTime, String referral) {
        Filter request = new Filter();
        request.setIp(ip);
        request.setDatetimeFrom(dateTime.minusHours(24));
        request.setDatetimeTo(dateTime);
        request.setRefferalCheckRight(referral);
        Page<Cpc> page = repository.findAll(getSpecification(request), PageRequest.of(0, Integer.MAX_VALUE));
        return page.map(CpcDTO::from);
    }

    public List<Cpc> findByIp1HoursBeforeNoIp(LocalDateTime dateTime, String referral) {
        Filter request = new Filter();
        request.setDatetimeFrom(dateTime.minusHours(1));
        request.setDatetimeTo(dateTime);
        request.setRefferalCheckRight(referral);
        return repository.findAll(getSpecification(request), Sort.by(Sort.Order.desc("id")));
    }

    public Page<CpcDTO> getAllDayBefore() {
        Filter request = new Filter();
        request.setDateFrom(LocalDate.now().minusDays(1));
        request.setDateTo(LocalDate.now().minusDays(1));
        Page<Cpc> page = repository.findAll(getSpecification(request), PageRequest.of(0, Integer.MAX_VALUE));
        log.info("UNREAD CPC :: {}", page.getTotalElements());
        return page.map(CpcDTO::from);
    }

    public Page<CpcDTO> getAllByDay(LocalDate dataFrom, LocalDate datato, Boolean blacklisted, Long affiliateId, Long campaignId) {
        Filter request = new Filter();
        request.setDateFrom(dataFrom);
        request.setDateTo(datato);
        request.setBlacklisted(blacklisted);
        if (affiliateId != null) request.setAffiliateid(affiliateId);
        if (campaignId != null) request.setCampaignid(campaignId);
        Page<Cpc> page = repository.findAll(getSpecification(request), PageRequest.of(0, Integer.MAX_VALUE));
        return page.map(CpcDTO::from);
    }

    public List<ClickMultipli> getListaClickMultipliDaDisabilitare(LocalDate dateFrom, LocalDate dateTo, Long affiliateId, Long campaignId) {
        return repository.getListaClickMultipliDaDisabilitare(dateFrom, dateTo.plusDays(1), affiliateId, campaignId);
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
            Predicate completePredicate;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }

            if (request.getRefferal() != null) {
                predicates.add(cb.like(root.get("refferal"), "%" + request.getRefferal() + "%"));
            }
            if (request.getRefferalCheckRight() != null) {
                predicates.add(cb.like(root.get("refferal"), request.getRefferalCheckRight() + "%"));
            }

            if (request.getRead() != null) {
                predicates.add(cb.equal(root.get("read"), request.getRead()));
            }
            if (request.getIp() != null) {
                predicates.add(cb.like(root.get("ip"), '%' + request.getIp() + "%"));
            }
            if (request.getDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), request.getDateFrom().atStartOfDay()));
            }
            if (request.getDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), LocalDateTime.of(request.getDateTo(), LocalTime.MAX)));
            }

            if (request.getDatetimeFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), request.getDatetimeFrom()));
            }
            if (request.getDatetimeTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), request.getDatetimeTo()));
            }

            if (request.getAffiliateid() != null) {
                predicates.add(cb.equal(root.get("affiliateId"), request.getAffiliateid()));
            }
            if (request.getCampaignid() != null) {
                predicates.add(cb.equal(root.get("campaignId"), request.getCampaignid()));
            }
            if (request.getChannelId() != null) {
                predicates.add(cb.equal(root.get("channelId"), request.getChannelId()));
            }

            if (request.getBlacklisted() != null && request.getBlacklisted()) {
                predicates.add(cb.isTrue(root.get("blacklisted")));
            }
            if (request.getBlacklisted() != null && !request.getBlacklisted()) {
                predicates.add(cb.isFalse(root.get("blacklisted")));
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
        private String htmlReferral;
        private String info;
        private String country;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Filter {

        private String refferal;
        private String ip;
        private String agent;
        private LocalDateTime date;
        private Boolean read;
        private String htmlReferral;
        private String info;
        private String country;
        private String id;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateFrom;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateTo;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDateTime datetimeFrom;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDateTime datetimeTo;

        //dati referral
        private Long mediaId;
        private Long campaignid;
        private Long affiliateid;
        private Long channelId;
        private Long targetId;

        private Boolean blacklisted;

        private String refferalCheckRight;

    }

}