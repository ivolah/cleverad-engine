package it.cleverad.engine.business;

import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.persistence.model.service.TopCampagneCPM;
import it.cleverad.engine.persistence.model.service.WidgetCampaignDayCpm;
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
public class StatCPMBusiness {

    @Autowired
    private WidgetCampaignDayCpmRepository widgetCampaignDayCpmRepository;
    @Autowired
    private CampaignBusiness campaignBusiness;
    @Autowired
    private WidgetTopCPMRepository widgetTopCPMRepository;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    /**
     * ===========================================================================================================
     * >>> CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM <<<
     * ===========================================================================================================
     **/

    public String getStatTotaleDayCpm(Filter request) {

        request.setDoyMenoDieci(LocalDate.now().getDayOfYear() - 6);
        request.setYear((long) LocalDate.now().getYear());
        if (!jwtUserDetailsService.isAdmin()) request.setAffiliateId(jwtUserDetailsService.getAffiliateID());
        List<WidgetCampaignDayCpm> tutto = widgetCampaignDayCpmRepository.findAll(getSpecificationCampaignDayCpm(request), PageRequest.of(0, Integer.MAX_VALUE)).stream().collect(Collectors.toList());

        Set<Long> doys = tutto.stream().map(WidgetCampaignDayCpm::getDoy).collect(Collectors.toSet());
        Set<Long> doysDaVerificare = new HashSet<>();
        if (!doys.isEmpty() && doys.size() > 0)
            for (Long i = doys.stream().min(Long::compareTo).get(); i <= doys.stream().max(Long::compareTo).get(); i++)
                doysDaVerificare.add(i);

        LocalDate today = LocalDate.now();
        JSONObject mainObj = new JSONObject();
        JSONArray harej = new JSONArray();
        JSONArray xSeries = new JSONArray();
        doysDaVerificare.stream().sorted().forEach(gg -> {
            Double dd = 0D;

            Filter filter = new Filter();
            filter.setDoy(gg);
            if (!jwtUserDetailsService.isAdmin()) filter.setAffiliateId(jwtUserDetailsService.getAffiliateID());
            List<WidgetCampaignDayCpm> giornato = widgetCampaignDayCpmRepository.findAll(getSpecificationCampaignDayCpm(filter), PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("doy")))).stream().collect(Collectors.toList());
            for (WidgetCampaignDayCpm w : giornato)
                dd = dd + w.getTotale();

            xSeries.put(today.withDayOfYear(Math.toIntExact(gg)).toString());
            harej.put(dd);
        });

        Double totale = 0D;
        for (Object o : harej)
            totale += (Double) o;

        mainObj.put("totale", totale);
        mainObj.put("data", harej);
        mainObj.put("xSeries", xSeries);

        return mainObj.toString();
    }

    public Page<WidgetCampaignDayCpm> getStatCampaignDayCpm(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("totale")));
        if (!jwtUserDetailsService.isAdmin()) request.setAffiliateId(jwtUserDetailsService.getAffiliateID());
        return widgetCampaignDayCpmRepository.findAll(getSpecificationCampaignDayCpm(request), pageable);
    }

    public Page<WidgetCampaignDayCpm> getTopCampaignsDayCpm() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("totale")));

        LocalDate oggi = LocalDate.now();
        Integer week = YearWeek.from(LocalDate.of(oggi.getYear(), oggi.getMonth(), oggi.getDayOfMonth())).getWeek();

        Filter request = new Filter();
        request.setWeek(week + 1);
        request.setWeekMeno(week - 1);
        if (!jwtUserDetailsService.isAdmin()) request.setAffiliateId(jwtUserDetailsService.getAffiliateID());
        return widgetCampaignDayCpmRepository.findAll(getSpecificationCampaignDayCpm(request), pageable);
    }

    public String getWidgetCampaignsDayCpm() {

        Filter request = new Filter();
        request.setDoyMenoDieci(LocalDate.now().getDayOfYear() - 6);
        request.setYear((long) LocalDate.now().getYear());

        if (!jwtUserDetailsService.isAdmin()) request.setAffiliateId(jwtUserDetailsService.getAffiliateID());
        Page<WidgetCampaignDayCpm> tutto = widgetCampaignDayCpmRepository.findAll(getSpecificationCampaignDayCpm(request), PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc("totale"))));

        Set<Long> doys = tutto.stream().map(WidgetCampaignDayCpm::getDoy).collect(Collectors.toSet());

        List<Long> listaTop0 = widgetTopCPMRepository.listaTopCampagne(request.getAffiliateId(), 0).stream().filter(topCampagneCPM -> topCampagneCPM.getimpression() > 100L).map(TopCampagneCPM::getcampaignid).collect(Collectors.toList());
        // Set<Long> listaTop3 = widgetTopCPMRepository.listaTopCampagne(request.getAffiliateId(), 3).stream().filter(topCampagneCPM -> topCampagneCPM.getimpression() > 100L).map(TopCampagneCPM::getcampaignid).collect(Collectors.toSet());
        List<Long> listaTop5 = widgetTopCPMRepository.listaTopCampagne(request.getAffiliateId(), 5).stream().filter(topCampagneCPM -> topCampagneCPM.getimpression() > 100L).map(TopCampagneCPM::getcampaignid).collect(Collectors.toList());
        // Set<Long> listaTop10 = widgetTopCPMRepository.listaTopCampagne(request.getAffiliateId(), 10).stream().filter(topCampagneCPM -> topCampagneCPM.getimpression() > 100L).map(TopCampagneCPM::getcampaignid).collect(Collectors.toSet());

        // tolgo dalla lista gli elementi già presenti in lista 0
        listaTop5.removeAll(listaTop0);
        log.trace(listaTop5.size() + " - di 5");
        listaTop5.forEach(aLong -> log.trace(">>>> 5 :: {}", aLong));
        log.trace(listaTop0.size() + " - di 0");
        listaTop0.forEach(aLong -> log.trace(">>>> 0 :: {}", aLong));

        if (listaTop0.size() < 6 && listaTop5.size() > 0) {
            listaTop0.addAll(listaTop5);
            listaTop0.forEach(aLong -> log.trace("NEW 0 :: {}", aLong));
        }
        listaTop0.subList(0, listaTop0.size());

        log.trace("CAMAPGNE {}", listaTop0.size());

        Set<Long> doysDaVerificare = new HashSet<>();
        if (!doys.isEmpty() && doys.size() > 0)
            for (Long i = doys.stream().min(Long::compareTo).get(); i <= doys.stream().max(Long::compareTo).get(); i++)
                doysDaVerificare.add(i);

        JSONObject mainObj = new JSONObject();
        JSONArray tutti = new JSONArray();
        listaTop0.stream().distinct().forEach(campagnaId -> {

            JSONObject ele = new JSONObject();
            String campagna = campaignBusiness.findById(campagnaId).getName();
            ele.put("name", campagna);
            JSONArray arrray = new JSONArray();

            doysDaVerificare.stream().sorted().forEach(gg -> {
                Double dd = 0D;
                Filter filter = new Filter();
                filter.setDoy(gg);
                filter.setCampaign(campagna);
                if (!jwtUserDetailsService.isAdmin()) filter.setAffiliateId(jwtUserDetailsService.getAffiliateID());
                List<WidgetCampaignDayCpm> giornato = widgetCampaignDayCpmRepository.findAll(getSpecificationCampaignDayCpm(filter), PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("doy")))).stream().collect(Collectors.toList());
                for (WidgetCampaignDayCpm w : giornato)
                    dd = dd + w.getTotale();

                log.trace("campagna :: {}  ({}) :: {} :: {}", campagna, campagnaId, gg, dd);
                arrray.put(dd);
            });

            ele.put("data", arrray);
            tutti.put(ele);
        });

        JSONArray xSeries = new JSONArray();
        LocalDate today = LocalDate.now();
        doysDaVerificare.stream().sorted().forEach(gg -> {
            xSeries.put(today.withDayOfYear(Math.toIntExact(gg)).toString());
        });

        mainObj.put("series", tutti);
        mainObj.put("xSeries", xSeries);
        return mainObj.toString();
    }

    private Specification<WidgetCampaignDayCpm> getSpecificationCampaignDayCpm(Filter request) {
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