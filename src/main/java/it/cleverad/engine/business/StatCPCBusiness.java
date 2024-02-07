package it.cleverad.engine.business;

import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.persistence.model.service.WidgetCampaignDayCpc;
import it.cleverad.engine.persistence.repository.service.TransactionCPCRepository;
import it.cleverad.engine.persistence.repository.service.WidgetCampaignDayCpcRepository;
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

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional
public class StatCPCBusiness {

    @Autowired
    private WidgetCampaignDayCpcRepository widgetCampaignDayCpcRepository;
    @Autowired
    private TransactionCPCRepository repository;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    private Pageable pageableDoy = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("year").descending().and(Sort.by("doy").descending()));

    /**
     * ===========================================================================================================
     * >>> CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC <<<
     * =================================================================<==========================================
     **/

    public String getStatTotaleDayCpc() {
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


    /// CPC CAMPAIGN DAY
    public Page<WidgetCampaignDayCpc> getStatCampaignDayCpc(Filter request, Pageable pageableRequest) {
        if (Boolean.FALSE.equals(jwtUserDetailsService.isAdmin()))
            request.setAffiliateId(jwtUserDetailsService.getAffiliateID());
        return widgetCampaignDayCpcRepository.findAll(getSpecificationCampaignDayCpc(request), PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("doy"))));
    }

    public Page<WidgetCampaignDayCpc> getTopCampaignsDayCpc(Integer giorni) {
        Filter request = new Filter();
        request.setDoyMenoDieci(LocalDate.now().getDayOfYear() - giorni);
        request.setYear((long) LocalDate.now().getYear());
        if (Boolean.FALSE.equals(jwtUserDetailsService.isAdmin()))
            request.setAffiliateId(jwtUserDetailsService.getAffiliateID());
        return widgetCampaignDayCpcRepository.findAll(getSpecificationCampaignDayCpc(request), PageRequest.of(0, 100, Sort.by(Sort.Order.asc("doy"))));
    }

    public String getWidgetCampaignsDayCpc() {

        Filter request = new Filter();
        request.setDoyMenoDieci(LocalDate.now().getDayOfYear() - 11);
        request.setYear((long) LocalDate.now().getYear());
        if (Boolean.FALSE.equals(jwtUserDetailsService.isAdmin()))
            request.setAffiliateId(jwtUserDetailsService.getAffiliateID());
        Page<WidgetCampaignDayCpc> tutto = widgetCampaignDayCpcRepository.findAll(getSpecificationCampaignDayCpc(request), pageableDoy);

        Set<Long> doys = tutto.stream().map(WidgetCampaignDayCpc::getDoy).collect(Collectors.toSet());
        Set<String> camps = tutto.stream().map(WidgetCampaignDayCpc::getCampaign).collect(Collectors.toSet());
        Set<Long> doysDaVerificare = new HashSet<>();
        if (!doys.isEmpty())
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
                if (Boolean.FALSE.equals(jwtUserDetailsService.isAdmin()))
                    filter.setAffiliateId(jwtUserDetailsService.getAffiliateID());
                List<WidgetCampaignDayCpc> giornato = widgetCampaignDayCpcRepository.findAll(getSpecificationCampaignDayCpc(filter), pageableDoy).stream().collect(Collectors.toList());
                for (WidgetCampaignDayCpc w : giornato)
                    dd = dd + w.getTotale();
                ja.put(dd);
            });

            ele.put("data", ja);
            arr.put(ele);
        });

        JSONArray xSeries = new JSONArray();
        LocalDate today = LocalDate.now();
        doysDaVerificare.stream().sorted().forEach(gg -> xSeries.put(today.withDayOfYear(Math.toIntExact(gg)).toString()));

        mainObj.put("series", arr);
        mainObj.put("xSeries", xSeries);

        return mainObj.toString();
    }

    public String get10DayTotal() {

        LocalDate today = LocalDate.now();

        Filter request = new Filter();
        request.setDoyMenoDieci(LocalDate.now().getDayOfYear() - 11);
        request.setYear((long) LocalDate.now().getYear());
        if (jwtUserDetailsService.isAdmin().equals(Boolean.FALSE))
            request.setAffiliateId(jwtUserDetailsService.getAffiliateID());

        Map<Integer, Integer> dayOfYearMap = new HashMap<>();
        for (int i = 0; i <= 9; i++) {
            LocalDate previousDay = today.minusDays(i);
            dayOfYearMap.put(previousDay.getDayOfYear(), previousDay.getYear());
            log.info(" -- {} - {}", previousDay.getYear(), previousDay.getDayOfYear());
        }
        log.info("----- " + dayOfYearMap.size());

//        Set<Long> dayOfYearList = new HashSet<>();
//        Set<Long> doys = widgetCampaignDayCpcRepository.findAll(getSpecificationCampaignDayCpc(request), pageableDoy).stream().map(WidgetCampaignDayCpc::getDoy).collect(Collectors.toSet());
//        if (!doys.isEmpty())
//            for (Long i = doys.stream().min(Long::compareTo).get(); i <= doys.stream().max(Long::compareTo).get(); i++) {
//                doysDaVerificare.add(i);
//            }

        JSONArray jsonArray = new JSONArray();
        dayOfYearMap.forEach((doy, year) -> {
            Double dd = 0D;
            log.info("{} - {}", year, doy);
            Filter filter = new Filter();
            filter.setDoy(Long.valueOf(doy));
            filter.setYear(Long.valueOf(year));
            if (Boolean.FALSE.equals(jwtUserDetailsService.isAdmin()))
                filter.setAffiliateId(jwtUserDetailsService.getAffiliateID());
            for (WidgetCampaignDayCpc w : widgetCampaignDayCpcRepository.findAll(getSpecificationCampaignDayCpc(filter), pageableDoy))
                dd = dd + w.getTotale();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(today.withDayOfYear(Math.toIntExact(doy)).toString(), dd);
            jsonArray.put(jsonObject);
        });

//        JSONObject mainObj = new JSONObject();
//        mainObj.put("data", jsonArray);
//        return mainObj.toString();
        return jsonArray.toString();
    }

    public Specification<WidgetCampaignDayCpc> getSpecificationCampaignDayCpc(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate;
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
            if (request.getYear() != null) {
                predicates.add(cb.equal(root.get("year"), request.getYear()));
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