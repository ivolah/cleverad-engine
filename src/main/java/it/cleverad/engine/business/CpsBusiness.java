package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.tracking.Cps;
import it.cleverad.engine.persistence.repository.tracking.CpsRepository;
import it.cleverad.engine.web.dto.tracking.CpsDTO;
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
public class CpsBusiness {

    @Autowired
    private CpsRepository repository;
    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public CpsDTO create(BaseCreateRequest request) {
        Cps map = mapper.map(request, Cps.class);
        map.setDate(LocalDateTime.now());
        map.setRead(false);
        map.setBlacklisted(false);
        return CpsDTO.from(repository.save(map));
    }

    // GET BY ID
    public CpsDTO findById(Long id) {
        Cps channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Cps", id));
        return CpsDTO.from(channel);
    }

    // UPDATE
    public CpsDTO update(Long id, Filter filter) {
        Cps channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Cps", id));
        mapper.map(filter, channel);
        return CpsDTO.from(repository.save(channel));
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
    public Page<CpsDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        Page<Cps> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CpsDTO::from);
    }

    // >>>>>>>>>>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    public void setRead(long id) {
        Cps media = repository.findById(id).get();
        media.setRead(true);
        repository.save(media);
    }

    public void setCpcId(Long id, Long cpcId) {
        Cps cps = repository.findById(id).get();
        cps.setCpcId(cpcId);
        repository.save(cps);
    }

    // >>>>>>>>>>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    public Page<CpsDTO> getUnreadOneHourBefore() {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc("id")));
        Filter request = new Filter();
        request.setRead(false);
        request.setBlacklisted(false);
        request.setDatetimeFrom(LocalDate.now().atStartOfDay());
        request.setDatetimeTo(LocalDateTime.now());
        Page<Cps> page = repository.findAll(getSpecification(request), pageable);
        if (page.getTotalElements() > 0)
            log.trace("\n\n\n >>>>>>>>>>>>>>>>>>>>>> UNREAD CPS  BEFORE :: {}:{} = {}", request.getDatetimeFrom(), request.getDatetimeTo(), page.getTotalElements());
        return page.map(CpsDTO::from);
    }

    public Page<CpsDTO> getAllDayBefore() {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc("id")));
        Filter request = new Filter();
        request.setDateFrom(LocalDate.now().minusDays(1));
        request.setDateTo(LocalDate.now().minusDays(1));
        Page<Cps> page = repository.findAll(getSpecification(request), pageable);
        log.trace("UNREAD CPs :: {}", page.getTotalElements());
        return page.map(CpsDTO::from);
    }

    public Page<CpsDTO> getAllDay(Integer anno, Integer mese, Integer giorno, Long affilaiteId) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc("id")));
        Filter request = new Filter();
        request.setDateFrom(LocalDate.of(anno, mese, giorno));
        request.setDateTo(LocalDate.of(anno, mese, giorno));
        if (affilaiteId != null) request.setAffiliateid(affilaiteId);
        Page<Cps> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CpsDTO::from);
    }

    public Page<CpsDTO> findByIp24HoursBefore(String ip, LocalDateTime dateTime) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc("id")));
        Filter request = new Filter();
        request.setIp(ip);
        request.setDatetimeFrom(dateTime.minusDays(1));
        request.setDatetimeTo(dateTime);
        Page<Cps> page = repository.findAll(getSpecification(request), pageable);
        log.info("FIND IP CPS :: {}", page.getTotalElements());
        return page.map(CpsDTO::from);
    }

    public Page<CpsDTO> getUnreadBlacklisted() {
        Filter request = new Filter();
        request.setRead(false);
        request.setBlacklisted(true);
        LocalDateTime oraSpaccata = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        request.setDatetimeFrom(oraSpaccata.toLocalDate().atStartOfDay().minusHours(3));
        request.setDatetimeTo(LocalDateTime.now());
        Page<Cps> page = repository.findAll(getSpecification(request), PageRequest.of(0, Integer.MAX_VALUE));
        return page.map(CpsDTO::from);
    }

    /**
     * ============================================================================================================
     **/
    private Specification<Cps> getSpecification(Filter request) {
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