package it.cleverad.engine.business;

import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.persistence.model.service.TopCampagne;
import it.cleverad.engine.persistence.repository.service.TransactionCPLRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
@Transactional
public class StatCPLBusiness {

    @Autowired
    private TransactionCPLRepository repository;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    /**
     * ===========================================================================================================
     * >>> CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL <<<
     * ===========================================================================================================
     **/

    public String getStatTotaleGiorno(StatCPCBusiness.FilterStatistics request) {
        if (jwtUserDetailsService.isAffiliate()) {
            request.setAffiliateId(jwtUserDetailsService.getAffiliateId());
        } else if (jwtUserDetailsService.isAdvertiser()) {
            request.setAdvertiserId(jwtUserDetailsService.getAdvertiserId());
        }
        JSONObject mainObj = new JSONObject();
        JSONArray data = new JSONArray();
        JSONArray xSeries = new JSONArray();
        for (int i = request.getDays(); i >= 0; i--) {
            Long nu = repository.totaleGiorno(i, request.getAffiliateId(), request.getAdvertiserId()).stream().mapToLong(TopCampagne::gettotale).sum();
            if (nu == null)
                nu = 0L;
            data.put(nu);
            xSeries.put(LocalDate.now().minusDays(i).toString());
        }
        AtomicReference<Long> totale = new AtomicReference<>(0L);
        data.forEach(elem -> totale.updateAndGet(v -> v + (Long) elem));
        mainObj.put("totale", totale.get());
        mainObj.put("data", data);
        mainObj.put("xSeries", xSeries);
        log.trace("CPL {}", mainObj);
        return mainObj.toString();
    }

    public String get10DayTotal() {
        StatCPCBusiness.FilterStatistics request = new StatCPCBusiness.FilterStatistics();
        request.setDays(10);
        if (jwtUserDetailsService.isAffiliate()) {
            request.setAffiliateId(jwtUserDetailsService.getAffiliateId());
        } else if (jwtUserDetailsService.isAdvertiser()) {
            request.setAdvertiserId(jwtUserDetailsService.getAdvertiserId());
        }
        JSONArray jsonArray = new JSONArray();
        for (int i = request.getDays(); i >= 0; i--) {
            Long nu = repository.totaleGiorno(i, request.getAffiliateId(), request.getAdvertiserId()).stream().mapToLong(TopCampagne::gettotale).sum();
            if (nu == null)
                nu = 0L;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(LocalDate.now().minusDays(i).toString(), nu);
            jsonArray.put(jsonObject);
        }
        return jsonArray.toString();
    }

    public List<TopCampagne> getTopCampaignsDayRange(StatCPCBusiness.FilterStatistics request) {
        if (jwtUserDetailsService.isAffiliate()) {
            request.setAffiliateId(jwtUserDetailsService.getAffiliateId());
        } else if (jwtUserDetailsService.isAdvertiser()) {
            request.setAdvertiserId(jwtUserDetailsService.getAdvertiserId());
        }
        return repository.listaTopCampagneTotale(request.getDays(), request.getAffiliateId(), request.getAdvertiserId());
    }

    public List<TopCampagne> getValueTopCampaignsDayRange(StatCPCBusiness.FilterStatistics request) {
        if (jwtUserDetailsService.isAffiliate()) {
            request.setAffiliateId(jwtUserDetailsService.getAffiliateId());
        } else if (jwtUserDetailsService.isAdvertiser()) {
            request.setAdvertiserId(jwtUserDetailsService.getAdvertiserId());
        }
        return repository.listaTopCampagneValore(request.getDays(), request.getAffiliateId(), request.getAdvertiserId());
    }

    /**
     * ============================================================================================================
     **/

//    public String getWidgetCampaignsDayCpl() {
//
//        Filter request = new Filter();
//        request.setDoyMenoDieci(LocalDate.now().getDayOfYear() - 11);
//        request.setYear((long) LocalDate.now().getYear());
//        if (!jwtUserDetailsService.isAdmin()) request.setAffiliateId(jwtUserDetailsService.getAffiliateId());
//        Page<WidgetCampaignDayCpl> tutto = widgetCampaignDayCplRepository.findAll(getSpecificationCampaignDayCpl(request), PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("doy"))));
//
//        Set<Long> doys = tutto.stream().map(WidgetCampaignDayCpl::getDoy).collect(Collectors.toSet());
//        Set<String> camps = tutto.stream().map(WidgetCampaignDayCpl::getCampaign).collect(Collectors.toSet());
//
//        Set<Long> doysDaVerificare = new HashSet<>();
//        if (!doys.isEmpty() && doys.size() > 0)
//            for (Long i = doys.stream().min(Long::compareTo).get(); i <= doys.stream().max(Long::compareTo).get(); i++)
//                doysDaVerificare.add(i);
//
//        JSONObject mainObj = new JSONObject();
//        JSONArray arr = new JSONArray();
//        camps.stream().distinct().forEach(campagna -> {
//            JSONObject ele = new JSONObject();
//            ele.put("name", campagna);
//            JSONArray ja = new JSONArray();
//
//            doysDaVerificare.stream().sorted().forEach(gg -> {
//                Double dd = 0D;
//                Filter filter = new Filter();
//                filter.setDoy(gg);
//                filter.setCampaign(campagna);
//                if (!jwtUserDetailsService.isAdmin()) filter.setAffiliateId(jwtUserDetailsService.getAffiliateId());
//                List<WidgetCampaignDayCpl> giornato = widgetCampaignDayCplRepository.findAll(getSpecificationCampaignDayCpl(filter), PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("doy")))).stream().collect(Collectors.toList());
//                for (WidgetCampaignDayCpl w : giornato)
//                    dd = dd + w.getValore();
//                ja.put(dd);
//            });
//
//            ele.put("data", ja);
//            arr.put(ele);
//            //log.info(aLong + " :: " + dd);
//        });
//
//        JSONArray xSeries = new JSONArray();
//        LocalDate today = LocalDate.now();
//        doysDaVerificare.stream().sorted().forEach(gg -> {
//            xSeries.put(today.withDayOfYear(Math.toIntExact(gg)).toString());
//        });
//
//        mainObj.put("series", arr);
//        mainObj.put("xSeries", xSeries);
//
//        return mainObj.toString();
//    }

    /**
     * ============================================================================================================
     **/


}