package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.persistence.model.service.Affiliate;
import it.cleverad.engine.persistence.model.service.Campaign;
import it.cleverad.engine.persistence.model.service.Channel;
import it.cleverad.engine.persistence.model.tracking.Cpl;
import it.cleverad.engine.persistence.repository.service.AffiliateRepository;
import it.cleverad.engine.persistence.repository.service.CampaignRepository;
import it.cleverad.engine.persistence.repository.service.ChannelRepository;
import it.cleverad.engine.persistence.repository.tracking.CplRepository;
import it.cleverad.engine.web.dto.tracking.CplDTO;
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

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional
public class CplBusiness {

    @Autowired
    private CplRepository repository;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
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
    public CplDTO create(BaseCreateRequest request) {
        Cpl map = mapper.map(request, Cpl.class);
        map.setDate(LocalDateTime.now());
        map.setRead(false);
        return CplDTO.from(repository.save(map));
    }

    // GET BY ID
    public CplDTO findById(Long id) {
        Cpl channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Cpl", id));
        return CplDTO.from(channel);
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
    public Page<CplDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        Page<Cpl> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CplDTO::from);
    }

    public Page<CplDTO> searchWithReferral(CplBusiness.Filter request, Pageable pageableRequest) {
        if (jwtUserDetailsService.isAdmin()) {
            Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
            Page<CplDTO> page = repository.findAll(getSpecification(request), pageable).map(CplDTO::from);
            List<CplDTO> exp = new ArrayList<>();
            page.stream().parallel().forEach(cplDTO -> {
                if (cplDTO.getCampaignId() != null) {
                    Campaign campaign = campaignRepository.findById(cplDTO.getCampaignId()).orElse(null);
                    if (campaign != null && campaign.getName() != null) cplDTO.setCampaignName(campaign.getName());
                }
                if (cplDTO.getAffiliateId() != null) {
                    Affiliate affiliate = affiliateRepository.findById(cplDTO.getAffiliateId()).orElse(null);
                    if (affiliate != null && affiliate.getName() != null) cplDTO.setAffiliateName(affiliate.getName());
                }
                if (cplDTO.getChannelId() != null) {
                    Channel channel = channelRepository.findById(cplDTO.getChannelId()).orElse(null);
                    if (cplDTO.getChannelId() != null && channel != null && channel.getName() != null)
                        cplDTO.setChannelName(channel.getName());
                }
                exp.add(cplDTO);
            });
            return new PageImpl<>(exp, pageable, page.getTotalElements());
        } else {
            return null;
        }
    }

    // UPDATE
    public CplDTO update(Long id, Filter filter) {
        Cpl channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Cpl", id));
        mapper.map(filter, channel);
        return CplDTO.from(repository.save(channel));
    }

    public void setRead(long id) {
        Cpl media = repository.findById(id).get();
        media.setRead(true);
        repository.save(media);
    }

    public void setCpcId(Long id, Long cpcId) {
        Cpl media = repository.findById(id).get();
        media.setCpcId(cpcId);
        repository.save(media);
    }

    public Page<CplDTO> getUnreadOneHourBefore( ) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc("id")));
        Filter request = new Filter();
        request.setRead(false);
        request.setBlacklisted(false);
        LocalDateTime oraSpaccata = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        request.setDatetimeFrom(oraSpaccata.toLocalDate().atStartOfDay());
        request.setDatetimeTo(LocalDateTime.now());
        Page<Cpl> page = repository.findAll(getSpecification(request), pageable);
        if (page.getTotalElements() > 0)
            log.trace("\n\n\n >>>>>>>>>>>>>>>>>>>>>> UNREAD CPL  BEFORE :: {}:{} = {}", request.getDatetimeFrom(), request.getDatetimeTo(), page.getTotalElements());
        return page.map(CplDTO::from);
    }

    public Page<CplDTO> getAllDayBefore() {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc("id")));
        Filter request = new Filter();
        request.setDateFrom(LocalDate.now().minusDays(1));
        request.setDateTo(LocalDate.now().minusDays(1));
        Page<Cpl> page = repository.findAll(getSpecification(request), pageable);
        log.info("UNREAD CPL :: {}", page.getTotalElements());
        return page.map(CplDTO::from);
    }

    public Page<CplDTO> getAllDay(Integer anno, Integer mese, Integer giorno, Long affilaiteId, Long campaginId) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc("id")));
        Filter request = new Filter();
        request.setDateFrom(LocalDate.of(anno, mese, giorno));
        request.setDateTo(LocalDate.of(anno, mese, giorno));
        if (affilaiteId != null) request.setAffiliateid(affilaiteId);
        if (campaginId != null) request.setCampaignid(campaginId);
        Page<Cpl> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CplDTO::from);
    }

    public Page<CplDTO> findByIp24HoursBefore(String ip, LocalDateTime dateTime) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc("id")));
        Filter request = new Filter();
        request.setIp(ip);
        request.setDatetimeFrom(dateTime.minusDays(1));
        request.setDatetimeTo(dateTime);
        Page<Cpl> page = repository.findAll(getSpecification(request), pageable);
        log.info("FIND IP CPL :: {}", page.getTotalElements());
        return page.map(CplDTO::from);
    }

    public Page<CplDTO> getUnreadBlacklisted() {
        Filter request = new Filter();
        request.setRead(false);
        request.setBlacklisted(true);
        LocalDateTime oraSpaccata = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        request.setDatetimeFrom(oraSpaccata.toLocalDate().atStartOfDay().minusHours(3));
        request.setDatetimeTo(LocalDateTime.now());
        Page<Cpl> page = repository.findAll(getSpecification(request), PageRequest.of(0, Integer.MAX_VALUE));
        return page.map(CplDTO::from);
    }

    /**
     * ============================================================================================================
     **/
    private Specification<Cpl> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getRefferal() != null) {
                predicates.add(cb.like(root.get("refferal"), "%" + request.getRefferal() + "%"));
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
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), LocalDateTime.of(request.getDateTo(), LocalTime.MAX)));
            }
            if (request.getDatetimeFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), request.getDatetimeFrom()));
            }
            if (request.getDatetimeTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), request.getDatetimeTo()));
            }
            if (request.getCountry() != null) {
                predicates.add(cb.equal(root.get("country"), request.getCountry()));
            }
            if (request.getAffiliateid() != null) {
                predicates.add(cb.equal(root.get("affiliateId"), request.getAffiliateid()));
            }
            if (request.getCampaignid() != null) {
                predicates.add(cb.equal(root.get("campaignId"), request.getCampaignid()));
            }
            if (request.getBlacklisted() != null) {
                predicates.add(cb.equal(root.get("blacklisted"), request.getBlacklisted()));
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
        private String data;
        private String info;
        private String country;
        private Boolean blacklisted;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private String refferal;
        private String cid;
        private String ip;
        private String agent;
        private String data;
        private Boolean read;
        private String info;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateFrom;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateTo;
        private LocalDateTime datetimeFrom;
        private LocalDateTime datetimeTo;
        private String country;
        //dati referral
        private Long mediaId;
        private Long campaignid;
        private Long affiliateid;
        private Long channelId;
        private Long targetId;
        private Boolean blacklisted;
    }

}