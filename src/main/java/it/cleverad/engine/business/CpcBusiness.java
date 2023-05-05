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
import it.cleverad.engine.service.RefferalService;
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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional
public class CpcBusiness {

    @Autowired
    private CpcRepository repository;
    @Autowired
    private RefferalService refferalService;
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
    public Page<CpcDTO> searchWithRefferal(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        Page<Cpc> page = repository.findAll(getSpecification(request), pageable);
        Page<CpcDTO> res = page.map(CpcDTO::from);
        List<CpcDTO> exp = new ArrayList<>();
        res.stream().forEach(cpc -> {
            if (StringUtils.isNotBlank(cpc.getRefferal()) && !cpc.getRefferal().contains("{{refferalId}}")) {
                Refferal refferal = refferalService.decodificaRefferal(cpc.getRefferal());

                Campaign campaign = campaignRepository.findById(refferal.getCampaignId()).orElse(null);
                if (refferal.getCampaignId() != null && campaign != null && campaign.getName() != null) {
                    cpc.setCampaignName(campaign.getName());
                    cpc.setCampaignId(refferal.getCampaignId());
                }

                if (cpc.getRefferal().length() > 3) {
                    Affiliate affiliate = affiliateRepository.findById(refferal.getAffiliateId()).orElse(null);
                    if (refferal.getAffiliateId() != null && affiliate != null && affiliate.getName() != null) {
                        cpc.setAffiliateName(affiliate.getName());
                        cpc.setAffiliateId(refferal.getAffiliateId());
                    }

                    Channel channel = channelRepository.findById(refferal.getChannelId()).orElse(null);
                    if (refferal.getChannelId() != null && channel != null && channel.getName() != null) {
                        cpc.setChannelName(channel.getName());
                        cpc.setChannelId(refferal.getChannelId());
                    }
                }
            } else {
                cpc.setRefferal("");
            }

            exp.add(cpc);
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
        Pageable pageable = PageRequest.of(0, 1000, Sort.by(Sort.Order.desc("id")));
        Filter request = new Filter();
        request.setRead(false);
        Page<Cpc> page = repository.findAll(getSpecification(request), pageable);
        log.trace("UNREAD {}", page.getTotalElements());
        return page.map(CpcDTO::from);
    }

    public Page<CpcDTO> getUnreadDayBefore() {
        Pageable pageable = PageRequest.of(0, 1000, Sort.by(Sort.Order.desc("id")));
        Filter request = new Filter();
        request.setRead(false);
        request.setDateFrom(LocalDate.now().minusDays(1));
        request.setDateTo(LocalDate.now().minusDays(1));
        Page<Cpc> page = repository.findAll(getSpecification(request), pageable);
        log.trace("UNREAD {}", page.getTotalElements());
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
