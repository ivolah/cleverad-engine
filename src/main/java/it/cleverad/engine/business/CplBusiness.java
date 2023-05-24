package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.config.model.Refferal;
import it.cleverad.engine.persistence.model.service.Affiliate;
import it.cleverad.engine.persistence.model.service.Campaign;
import it.cleverad.engine.persistence.model.service.Channel;
import it.cleverad.engine.persistence.model.tracking.Cpl;
import it.cleverad.engine.persistence.repository.service.AffiliateRepository;
import it.cleverad.engine.persistence.repository.service.CampaignRepository;
import it.cleverad.engine.persistence.repository.service.ChannelRepository;
import it.cleverad.engine.persistence.repository.tracking.CplRepository;
import it.cleverad.engine.service.JwtUserDetailsService;
import it.cleverad.engine.service.ReferralService;
import it.cleverad.engine.web.dto.CplDTO;
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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional
public class CplBusiness {

    @Autowired
    private CplRepository repository;
    @Autowired
    private ReferralService referralService;
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
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        Page<Cpl> page = repository.findAll(getSpecification(request), pageable);
        Page<CplDTO> res = page.map(CplDTO::from);
        List<CplDTO> exp = new ArrayList<>();
        res.stream().forEach(cplDTO -> {
            if (StringUtils.isNotBlank(cplDTO.getRefferal()) && !cplDTO.getRefferal().contains("{")) {

                Refferal refferal = referralService.decodificaReferral(cplDTO.getRefferal());

                Campaign campaign = campaignRepository.findById(refferal.getCampaignId()).orElse(null);
                if (refferal.getCampaignId() != null && campaign != null && campaign.getName() != null) {
                    cplDTO.setCampaignName(campaign.getName());
                    cplDTO.setCampaignId(refferal.getCampaignId());
                }

                if (cplDTO.getRefferal().length() > 5) {
                    if (refferal.getAffiliateId() != null) {
                        Affiliate affiliate = affiliateRepository.findById(refferal.getAffiliateId()).orElse(null);
                        if (affiliate != null && affiliate.getName() != null) {
                            cplDTO.setAffiliateName(affiliate.getName());
                            cplDTO.setAffiliateId(refferal.getAffiliateId());
                        }
                    }
                    Channel channel = channelRepository.findById(refferal.getChannelId()).orElse(null);
                    if (refferal.getChannelId() != null && channel != null && channel.getName() != null) {
                        cplDTO.setChannelName(channel.getName());
                        cplDTO.setChannelId(refferal.getChannelId());
                    }
                }

            } else {
                cplDTO.setRefferal("");
            }

            if (jwtUserDetailsService.isAdmin()) {
                exp.add(cplDTO);
            } else if (!cplDTO.getRefferal().equals("") && cplDTO.getAffiliateId().equals(jwtUserDetailsService.getAffiliateID())) {
                exp.add(cplDTO);
            }

        });
        Page<CplDTO> pages = new PageImpl<CplDTO>(exp, pageable, page.getTotalElements());
        return pages;
    }


    // UPDATE
    public CplDTO update(Long id, Filter filter) {
        Cpl channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Cpl", id));
        CplDTO campaignDTOfrom = CplDTO.from(channel);

        mapper.map(filter, campaignDTOfrom);

        Cpl mappedEntity = mapper.map(channel, Cpl.class);
        mapper.map(campaignDTOfrom, mappedEntity);

        return CplDTO.from(repository.save(mappedEntity));
    }

    public void setRead(long id) {
        Cpl media = repository.findById(id).get();
        media.setRead(true);
        repository.save(media);
    }

    public Page<CplDTO> getUnreadOneHourBefore() {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc("id")));
        Filter request = new Filter();
        request.setRead(false);
        LocalDateTime oraSpaccata = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        request.setDatetimeFrom(oraSpaccata.minusHours(24));
       // request.setDatetimeFrom(oraSpaccata.minusHours(240));
        request.setDatetimeTo(oraSpaccata);
        Page<Cpl> page = repository.findAll(getSpecification(request), pageable);
        log.info("\n\n\n >>>>>>>>>>>>>>>>>>>>>> UNREAD CPL HOUR BEFORE :: {}", page.getTotalElements());
        return page.map(CplDTO::from);
    }

    public Page<CplDTO> getUnreadDayBefore() {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc("id")));
        Filter request = new Filter();
        request.setRead(false);
        request.setDateFrom(LocalDate.now().minusDays(1));
        request.setDateTo(LocalDate.now().minusDays(1));
        Page<Cpl> page = repository.findAll(getSpecification(request), pageable);
        log.info("UNREAD CPL :: {}", page.getTotalElements());
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

    /**
     * ============================================================================================================
     **/
    private Specification<Cpl> getSpecification(Filter request) {
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
        private String data;
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
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateFrom;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateTo;
        private LocalDateTime datetimeFrom;
        private LocalDateTime datetimeTo;
    }

}
