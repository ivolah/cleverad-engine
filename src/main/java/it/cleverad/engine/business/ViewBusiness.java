package it.cleverad.engine.business;

import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.persistence.model.service.WidgetCampaignDayCps;
import it.cleverad.engine.persistence.repository.service.WidgetCampaignDayCpmRepository;
import it.cleverad.engine.persistence.repository.service.WidgetCampaignDayCpsRepository;
import it.cleverad.engine.persistence.repository.service.WidgetTopCPMRepository;
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
public class ViewBusiness {

    @Autowired
    private WidgetCampaignDayCpsRepository widgetCampaignDayCpsRepository;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    /**
     * ===========================================================================================================
     * >>> CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS <<<
     * ===========================================================================================================
     **/

    public Page<WidgetCampaignDayCps> getStatCampaignDayCps(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        if (!jwtUserDetailsService.isAdmin()) request.setAffiliateId(jwtUserDetailsService.getAffiliateID());
        return widgetCampaignDayCpsRepository.findAll(getSpecificationCampaignDayCps(request), pageable);
    }

    public Page<WidgetCampaignDayCps> getTopCampaignsDayCps() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("valore")));

        LocalDate oggi = LocalDate.now();
        Integer week = YearWeek.from(LocalDate.of(oggi.getYear(), oggi.getMonth(), oggi.getDayOfMonth())).getWeek();

        Filter request = new Filter();
        request.setWeek(week);
        request.setWeekMeno(week - 1);
        if (!jwtUserDetailsService.isAdmin()) request.setAffiliateId(jwtUserDetailsService.getAffiliateID());
        return widgetCampaignDayCpsRepository.findAll(getSpecificationCampaignDayCps(request), pageable);
    }

    public String getWidgetCampaignsDayCps() {
        Filter request = new Filter();
        request.setDoyMenoDieci(LocalDate.now().getDayOfYear() - 11);
        if (!jwtUserDetailsService.isAdmin()) request.setAffiliateId(jwtUserDetailsService.getAffiliateID());
        Page<WidgetCampaignDayCps> tutto = widgetCampaignDayCpsRepository.findAll(getSpecificationCampaignDayCps(request), PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("doy"))));

        Set<Long> doys = tutto.stream().map(WidgetCampaignDayCps::getDoy).collect(Collectors.toSet());
        Set<String> camps = tutto.stream().map(WidgetCampaignDayCps::getCampaign).collect(Collectors.toSet());

        Set<Long> doysDaVerificare = new HashSet<>();
        for (Long i = doys.stream().min(Long::compareTo).get(); i <= doys.stream().max(Long::compareTo).get(); i++) {
            doysDaVerificare.add(i);
        }

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
                List<WidgetCampaignDayCps> giornato = widgetCampaignDayCpsRepository.findAll(getSpecificationCampaignDayCps(filter), PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("doy")))).stream().collect(Collectors.toList());
                if (giornato.size() > 0) {
                    dd = giornato.get(0).getValore();
                }

                ja.put(dd);
            });

            ele.put("data", ja);
            arr.put(ele);
            //log.info(aLong + " :: " + dd);
        });

        mainObj.put("series", arr);
        return mainObj.toString();
    }

    private Specification<WidgetCampaignDayCps> getSpecificationCampaignDayCps(Filter request) {
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