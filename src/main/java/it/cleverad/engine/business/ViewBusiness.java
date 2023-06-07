package it.cleverad.engine.business;

import it.cleverad.engine.persistence.model.service.*;
import it.cleverad.engine.persistence.repository.service.*;
import it.cleverad.engine.service.JwtUserDetailsService;
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
    private WidgetCampaignDayCpcRepository widgetCampaignDayCpcRepository;
    @Autowired
    private WidgetCampaignDayCpmRepository widgetCampaignDayCpmRepository;
    @Autowired
    private WidgetCampaignDayCplRepository widgetCampaignDayCplRepository;
    @Autowired
    private WidgetCampaignDayCpsRepository widgetCampaignDayCpsRepository;
    @Autowired
    private CampaignBusiness campaignBusiness;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private WidgetTopCPMRepository widgetTopCPMRepository;

    /**
     * ===========================================================================================================
     * >>> CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC <<<
     * =================================================================<==========================================
     **/

    public String getStatTotaleDayCpc(Filter request) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc("id")));
        request.setDoyMenoDieci(LocalDate.now().getDayOfYear() - 6);

        if (!jwtUserDetailsService.isAdmin()) request.setAffiliateId(jwtUserDetailsService.getAffiliateID());
        List<WidgetCampaignDayCpc> tutto = widgetCampaignDayCpcRepository.findAll(getSpecificationCampaignDayCpc(request), pageable).stream().collect(Collectors.toList());

        Set<Long> doys = tutto.stream().map(WidgetCampaignDayCpc::getDoy).collect(Collectors.toSet());
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
            List<WidgetCampaignDayCpc> giornato = widgetCampaignDayCpcRepository.findAll(getSpecificationCampaignDayCpc(filter), PageRequest.of(0, 1000, Sort.by(Sort.Order.asc("doy")))).stream().collect(Collectors.toList());
            for (WidgetCampaignDayCpc w : giornato)
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


    /// CPC CAMPAIGN DAY
    public Page<WidgetCampaignDayCpc> getStatCampaignDayCpc(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("doy")));
        if (!jwtUserDetailsService.isAdmin()) request.setAffiliateId(jwtUserDetailsService.getAffiliateID());
        return widgetCampaignDayCpcRepository.findAll(getSpecificationCampaignDayCpc(request), pageable);
    }

    public Page<WidgetCampaignDayCpc> getTopCampaignsDayCpc(Integer giorni) {
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Order.asc("doy")));
        Filter request = new Filter();
        request.setDoyMenoDieci(LocalDate.now().getDayOfYear() - giorni);
        if (!jwtUserDetailsService.isAdmin()) request.setAffiliateId(jwtUserDetailsService.getAffiliateID());
        return widgetCampaignDayCpcRepository.findAll(getSpecificationCampaignDayCpc(request), pageable);
    }

    public String getWidgetCampaignsDayCpc() {
        Filter request = new Filter();
        log.info(LocalDate.now().getDayOfYear() - 11 + "");
        request.setDoyMenoDieci(LocalDate.now().getDayOfYear() - 11);
        if (!jwtUserDetailsService.isAdmin()) request.setAffiliateId(jwtUserDetailsService.getAffiliateID());
        Page<WidgetCampaignDayCpc> tutto = widgetCampaignDayCpcRepository.findAll(getSpecificationCampaignDayCpc(request), PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("doy"))));

        Set<Long> doys = tutto.stream().map(WidgetCampaignDayCpc::getDoy).collect(Collectors.toSet());
        Set<String> camps = tutto.stream().map(WidgetCampaignDayCpc::getCampaign).collect(Collectors.toSet());

        log.info(doys.stream().min(Long::compareTo).get() + "");
        log.info(doys.stream().max(Long::compareTo).get() + "");
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
                List<WidgetCampaignDayCpc> giornato = widgetCampaignDayCpcRepository.findAll(getSpecificationCampaignDayCpc(filter), PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("doy")))).stream().collect(Collectors.toList());
                for (WidgetCampaignDayCpc w : giornato)
                    dd = dd + w.getTotale();
                ja.put(dd);
            });

            ele.put("data", ja);
            arr.put(ele);
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

    private Specification<WidgetCampaignDayCpc> getSpecificationCampaignDayCpc(Filter request) {
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
            if (request.getDoyMenoDieci() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("doy"), request.getDoyMenoDieci()));
            }

            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
            return completePredicate;
        };
    }

    /**
     * ===========================================================================================================
     * >>> CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM <<<
     * ===========================================================================================================
     **/

    public String getStatTotaleDayCpm(Filter request) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc("id")));
        request.setDoyMenoDieci(LocalDate.now().getDayOfYear() - 6);
        if (!jwtUserDetailsService.isAdmin()) request.setAffiliateId(jwtUserDetailsService.getAffiliateID());
        List<WidgetCampaignDayCpm> tutto = widgetCampaignDayCpmRepository.findAll(getSpecificationCampaignDayCpm(request), pageable).stream().collect(Collectors.toList());

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
        request.setDoyMenoDieci(LocalDate.now().getDayOfYear() - 11);
        if (!jwtUserDetailsService.isAdmin()) request.setAffiliateId(jwtUserDetailsService.getAffiliateID());
        Page<WidgetCampaignDayCpm> tutto = widgetCampaignDayCpmRepository.findAll(getSpecificationCampaignDayCpm(request), PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc("totale"))));

        Set<Long> doys = tutto.stream().map(WidgetCampaignDayCpm::getDoy).collect(Collectors.toSet());

        Set<Long> listaTop0 = widgetTopCPMRepository.listaTopCampagne(request.getAffiliateId(), 0).stream().filter(topCampagneCPM -> topCampagneCPM.getimpression() > 100L).map(TopCampagneCPM::getcampaignid).collect(Collectors.toSet());
        // Set<Long> listaTop3 = widgetTopCPMRepository.listaTopCampagne(request.getAffiliateId(), 3).stream().filter(topCampagneCPM -> topCampagneCPM.getimpression() > 100L).map(TopCampagneCPM::getcampaignid).collect(Collectors.toSet());
        // Set<Long> listaTop5 = widgetTopCPMRepository.listaTopCampagne(request.getAffiliateId(), 5).stream().filter(topCampagneCPM -> topCampagneCPM.getimpression() > 100L).map(TopCampagneCPM::getcampaignid).collect(Collectors.toSet());
        Set<Long> listaTop10 = widgetTopCPMRepository.listaTopCampagne(request.getAffiliateId(), 10).stream().filter(topCampagneCPM -> topCampagneCPM.getimpression() > 100L).map(TopCampagneCPM::getcampaignid).collect(Collectors.toSet());

        listaTop10.removeAll(listaTop0);

        Integer limite = 6;
        if (listaTop10.size() + listaTop0.size() < 6)
            limite = listaTop10.size() + listaTop0.size();

        while (listaTop0.size() < limite && listaTop10.size() > 0) {
            listaTop0.add(listaTop10.stream().collect(Collectors.toList()).get(0));
            listaTop10.remove(0);
        }

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

            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
            return completePredicate;
        };
    }


    /**
     * ===========================================================================================================
     * >>> CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL <<<
     * ===========================================================================================================
     **/

    public String getStatTotaleDayCpl(Filter request) {

        request.setDoyMenoDieci(LocalDate.now().getDayOfYear() - 6);
        if (!jwtUserDetailsService.isAdmin()) request.setAffiliateId(jwtUserDetailsService.getAffiliateID());
        List<WidgetCampaignDayCpl> tutto = widgetCampaignDayCplRepository.findAll(getSpecificationCampaignDayCpl(request), PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc("id")))
        ).stream().collect(Collectors.toList());

        Set<Long> doys = tutto.stream().map(WidgetCampaignDayCpl::getDoy).collect(Collectors.toSet());
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
            List<WidgetCampaignDayCpl> giornato = widgetCampaignDayCplRepository.findAll(getSpecificationCampaignDayCpl(filter), PageRequest.of(0, 1000, Sort.by(Sort.Order.asc("doy")))).stream().collect(Collectors.toList());
            for (WidgetCampaignDayCpl w : giornato) {
                dd = dd + 1;
            }
            xSeries.put(today.withDayOfYear(Math.toIntExact(gg)).toString());
            harej.put(dd);
        });

        Double totale = 0D;
        for (Object o : harej) {
            totale += (Double) o;
        }
        mainObj.put("totale", totale);
        mainObj.put("data", harej);
        mainObj.put("xSeries", xSeries);

        return mainObj.toString();
    }

    public Page<WidgetCampaignDayCpl> getStatCampaignDayCpl(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        if (!jwtUserDetailsService.isAdmin()) request.setAffiliateId(jwtUserDetailsService.getAffiliateID());
        return widgetCampaignDayCplRepository.findAll(getSpecificationCampaignDayCpl(request), pageable);
    }

    public Page<WidgetCampaignDayCpl> getTopCampaignsDayCpl() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("valore")));

        LocalDate oggi = LocalDate.now();
        Integer week = YearWeek.from(LocalDate.of(oggi.getYear(), oggi.getMonth(), oggi.getDayOfMonth())).getWeek();

        Filter request = new Filter();
        request.setWeek(week);
        request.setWeekMeno(week - 1);
        if (!jwtUserDetailsService.isAdmin()) request.setAffiliateId(jwtUserDetailsService.getAffiliateID());
        return widgetCampaignDayCplRepository.findAll(getSpecificationCampaignDayCpl(request), pageable);
    }

    public String getWidgetCampaignsDayCpl() {
        Filter request = new Filter();
        request.setDoyMenoDieci(LocalDate.now().getDayOfYear() - 11);
        if (!jwtUserDetailsService.isAdmin()) request.setAffiliateId(jwtUserDetailsService.getAffiliateID());
        Page<WidgetCampaignDayCpl> tutto = widgetCampaignDayCplRepository.findAll(getSpecificationCampaignDayCpl(request), PageRequest.of(0, 1000, Sort.by(Sort.Order.asc("doy"))));

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
                List<WidgetCampaignDayCpl> giornato = widgetCampaignDayCplRepository.findAll(getSpecificationCampaignDayCpl(filter), PageRequest.of(0, 1000, Sort.by(Sort.Order.asc("doy")))).stream().collect(Collectors.toList());
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

    private Specification<WidgetCampaignDayCpl> getSpecificationCampaignDayCpl(Filter request) {
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
        Page<WidgetCampaignDayCps> tutto = widgetCampaignDayCpsRepository.findAll(getSpecificationCampaignDayCps(request), PageRequest.of(0, 1000, Sort.by(Sort.Order.asc("doy"))));

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
                List<WidgetCampaignDayCps> giornato = widgetCampaignDayCpsRepository.findAll(getSpecificationCampaignDayCps(filter), PageRequest.of(0, 1000, Sort.by(Sort.Order.asc("doy")))).stream().collect(Collectors.toList());
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

//    public Page<StatCPLClickCampaignMediaDay> searchStatCpcClickCampaignMediaDay(Filter request, Pageable pageableRequest) {
//        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
//        Page<StatCpcClickCampaignMediaDay> page = statCpcClickCampaignMediaDayRepository.findAll(getSpecificationCpcCampaignDay(request), pageable);
//        return page;
//    }
//
//    private Specification<StatCpcClickCampaignMediaDay> getSpecificationStatCpcClickCampaignWeek(Filter request) {
//        return (root, query, cb) -> {
//            Predicate completePredicate = null;
//            List<Predicate> predicates = new ArrayList<>();
//
//            if (request.getId() != null) {
//                predicates.add(cb.equal(root.get("id"), request.getId()));
//            }
//
//            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
//            return completePredicate;
//        };
//    }
//
//    /**
//     * ============================================================================================================
//     **/
//
//    public Page<StatCpcValueCampaign> searchStatCpcValueCampaign(Filter request, Pageable pageableRequest) {
//        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
//        Page<StatCpcValueCampaign> page = statCpcValueCampaignRepository.findAll(getSpecificationStatCpcValueCampaign(request), pageable);
//        return page;
//    }
//
//    private Specification<StatCpcValueCampaign> getSpecificationStatCpcValueCampaign(Filter request) {
//        return (root, query, cb) -> {
//            Predicate completePredicate = null;
//            List<Predicate> predicates = new ArrayList<>();
//
//            if (request.getId() != null) {
//                predicates.add(cb.equal(root.get("id"), request.getId()));
//            }
//
//            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
//            return completePredicate;
//        };
//    }
//
//    public Page<StatCpcValueCampaignWeek> searchStatCpcValueCampaignWeek(Filter request, Pageable pageableRequest) {
//        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
//        Page<StatCpcValueCampaignWeek> page = statCpcValueCampaignWeekRepository.findAll(getSpecificationStatCpcValueCampaignWeek(request), pageable);
//        return page;
//    }
//
//    private Specification<StatCpcValueCampaignWeek> getSpecificationStatCpcValueCampaignWeek(Filter request) {
//        return (root, query, cb) -> {
//            Predicate completePredicate = null;
//            List<Predicate> predicates = new ArrayList<>();
//
//            if (request.getId() != null) {
//                predicates.add(cb.equal(root.get("id"), request.getId()));
//            }
//
//            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
//            return completePredicate;
//        };
//    }
//
//    /**
//     * ============================================================================================================
//     **/
//
//    public Page<StatCpcTransactionCampaignMedia> searchStatCpcTransactionCampaign(Filter request, Pageable pageableRequest) {
//        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
//        Page<StatCpcTransactionCampaignMedia> page = statCpcTransactionCampaignRepository.findAll(getSpecificationStatCpcTransactionCampaign(request), pageable);
//        return page;
//    }
//
//    private Specification<StatCpcTransactionCampaignMedia> getSpecificationStatCpcTransactionCampaign(Filter request) {
//        return (root, query, cb) -> {
//            Predicate completePredicate = null;
//            List<Predicate> predicates = new ArrayList<>();
//
//            if (request.getId() != null) {
//                predicates.add(cb.equal(root.get("id"), request.getId()));
//            }
//
//            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
//            return completePredicate;
//        };
//    }
//
//        /**
//     * ============================================================================================================
//     **/
//
//    public Page<StatCplValueCampaign> searchStatCplValueCampaign(Filter request, Pageable pageableRequest) {
//        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
//        Page<StatCplValueCampaign> page = statCplValueCampaignRepository.findAll(getSpecificationStatCplValueCampaign(request), pageable);
//        return page;
//    }
//
//    private Specification<StatCplValueCampaign> getSpecificationStatCplValueCampaign(Filter request) {
//        return (root, query, cb) -> {
//            Predicate completePredicate = null;
//            List<Predicate> predicates = new ArrayList<>();
//
//            if (request.getId() != null) {
//                predicates.add(cb.equal(root.get("id"), request.getId()));
//            }
//
//            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
//            return completePredicate;
//        };
//    }
//
//    public Page<StatCplValueCampaignWeek> searchStatCplValueCampaignWeek(Filter request, Pageable pageableRequest) {
//        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
//        Page<StatCplValueCampaignWeek> page = statCplValueCampaignWeekRepository.findAll(getSpecificationStatCplValueCampaignWeek(request), pageable);
//        return page;
//    }
//
//    private Specification<StatCplValueCampaignWeek> getSpecificationStatCplValueCampaignWeek(Filter request) {
//        return (root, query, cb) -> {
//            Predicate completePredicate = null;
//            List<Predicate> predicates = new ArrayList<>();
//
//            if (request.getId() != null) {
//                predicates.add(cb.equal(root.get("id"), request.getId()));
//            }
//
//            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
//            return completePredicate;
//        };
//    }

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
