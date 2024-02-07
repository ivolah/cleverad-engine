package it.cleverad.engine.business;

import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.persistence.model.service.WidgetCampaignDayCpl;
import it.cleverad.engine.persistence.repository.service.TransactionCPLRepository;
import it.cleverad.engine.persistence.repository.service.WidgetCampaignDayCplRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.threeten.extra.YearWeek;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional
public class StatCPLBusiness {

    @Autowired
    private WidgetCampaignDayCplRepository widgetCampaignDayCplRepository;
    @Autowired
    private TransactionCPLRepository repository;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    /**
     * ===========================================================================================================
     * >>> CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL <<<
     * ===========================================================================================================
     **/

    public String getStatTotaleDayCpl(Filter request) {
        JSONObject mainObj = new JSONObject();
        JSONArray data = new JSONArray();
        JSONArray xSeries = new JSONArray();

        for (int i = 6; i >= 0; i--) {
            Long nu = repository.totaleGiorno(i).get(0).gettotale();
            data.put(nu);
            xSeries.put(LocalDate.now().minusDays(i).toString());
        }

        Long totale = 0L;
        for (Object tot : data)
            totale += (Long) tot;

        mainObj.put("totale", totale);
        mainObj.put("data", data);
        mainObj.put("xSeries", xSeries);

        return mainObj.toString();
    }

    public Page<WidgetCampaignDayCpl> getStatCampaignDayCpl(Filter request, Pageable pageableRequest) {
        if (!jwtUserDetailsService.isAdmin()) request.setAffiliateId(jwtUserDetailsService.getAffiliateID());
        return widgetCampaignDayCplRepository.findAll(getSpecificationCampaignDayCpl(request), PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id"))));
    }

    public Page<WidgetCampaignDayCpl> getTopCampaignsDayCpl() {
        Filter request = new Filter();
        LocalDate oggi = LocalDate.now();
        Integer week = YearWeek.from(LocalDate.of(oggi.getYear(), oggi.getMonth(), oggi.getDayOfMonth())).getWeek();
        request.setWeek(week);
        request.setWeekMeno(week - 1);
        if (!jwtUserDetailsService.isAdmin()) request.setAffiliateId(jwtUserDetailsService.getAffiliateID());
        return widgetCampaignDayCplRepository.findAll(getSpecificationCampaignDayCpl(request), PageRequest.of(0, 10, Sort.by(Sort.Order.desc("valore"))));
    }

    public String getWidgetCampaignsDayCpl() {

        Filter request = new Filter();
        request.setDoyMenoDieci(LocalDate.now().getDayOfYear() - 11);
        request.setYear((long) LocalDate.now().getYear());
        if (!jwtUserDetailsService.isAdmin()) request.setAffiliateId(jwtUserDetailsService.getAffiliateID());
        Page<WidgetCampaignDayCpl> tutto = widgetCampaignDayCplRepository.findAll(getSpecificationCampaignDayCpl(request), PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("doy"))));

        Set<Long> doys = tutto.stream().map(WidgetCampaignDayCpl::getDoy).collect(Collectors.toSet());
        Set<String> camps = tutto.stream().map(WidgetCampaignDayCpl::getCampaign).collect(Collectors.toSet());

        Set<Long> doysDaVerificare = new HashSet<>();
        if (!doys.isEmpty() && doys.size() > 0)
            for (Long i = doys.stream().min(Long::compareTo).get(); i <= doys.stream().max(Long::compareTo).get(); i++)
                doysDaVerificare.add(i);

        JSONObject mainObj = new JSONObject();
        JSONArray arr = new JSONArray();
        camps.stream().distinct().forEach(campagna -> {
            JSONObject ele = new JSONObject();
            ele.put("name", campagna);
            JSONArray ja = new JSONArray();

            doysDaVerificare.stream().sorted().forEach(gg -> {
                Double dd = 0D;
                Filter filter = new Filter();
                filter.setDoy(gg);
                filter.setCampaign(campagna);
                if (!jwtUserDetailsService.isAdmin()) filter.setAffiliateId(jwtUserDetailsService.getAffiliateID());
                List<WidgetCampaignDayCpl> giornato = widgetCampaignDayCplRepository.findAll(getSpecificationCampaignDayCpl(filter), PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("doy")))).stream().collect(Collectors.toList());
                for (WidgetCampaignDayCpl w : giornato)
                    dd = dd + w.getValore();
                ja.put(dd);
            });

            ele.put("data", ja);
            arr.put(ele);
            //log.info(aLong + " :: " + dd);
        });

        JSONArray xSeries = new JSONArray();
        LocalDate today = LocalDate.now();
        doysDaVerificare.stream().sorted().forEach(gg -> {
            xSeries.put(today.withDayOfYear(Math.toIntExact(gg)).toString());
        });

        mainObj.put("series", arr);
        mainObj.put("xSeries", xSeries);

        return mainObj.toString();
    }

    public Specification<WidgetCampaignDayCpl> getSpecificationCampaignDayCpl(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getCampaignId() != null) {
                predicates.add(cb.equal(root.get("campaignId"), request.getCampaignId()));
            }
            if (request.getCampaign() != null) {
                predicates.add(cb.equal(root.get("campaign"), request.getCampaign()));
            }
            if (request.getAffiliateId() != null) {
                predicates.add(cb.equal(root.get("affiliateId"), request.getAffiliateId()));
            }
            if (request.getWeekMeno() != null) {
                predicates.add(cb.or(cb.equal(root.get("week"), request.getWeek()), cb.equal(root.get("week"), request.getWeekMeno())));
            }
            if (request.getDoy() != null) {
                predicates.add(cb.equal(root.get("doy"), request.getDoy()));
            }
            if (request.getDoyMenoDieci() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("doy"), request.getDoyMenoDieci()));
            }
            if (request.getYear() != null) {
                predicates.add(cb.equal(root.get("year"), request.getYear()));
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
    public static class Filter {
        private Long id;
        private Long affiliateId;
        private String name;
        private String description;

        private Long campaignId;
        private String campaign;
        private Long year;
        private Long month;
        private Long day;
        private Long doy;
        private Integer week;
        private Integer weekMeno;
        private Integer doyMenoDieci;

    }

}