package it.cleverad.engine.business;

import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.persistence.model.service.TopCampagne;
import it.cleverad.engine.persistence.repository.service.TransactionCPCRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
@Transactional
public class StatCPCBusiness {

    @Autowired
    private TransactionCPCRepository repository;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    /**
     * ===========================================================================================================
     * >>> CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC <<<
     * =================================================================<==========================================
     **/

    /**
     * Returns a JSON array containing the 10-day total for each day, starting from the current day.
     *
     * @return a JSON array containing the 10-day total for each day, starting from the current day
     */
    public String getStatTotaleGiorno(FilterStatistics request) {
        if (jwtUserDetailsService.isAffiliate()) {
            request.setAffiliateId(jwtUserDetailsService.getAffiliateId());
        } else if (jwtUserDetailsService.isAdvertiser()) {
            request.setAdvertiserId(jwtUserDetailsService.getAdvertiserId());
        }
        JSONObject mainObj = new JSONObject();
        JSONArray data = new JSONArray();
        JSONArray xSeries = new JSONArray();
        for (int i = request.getDays(); i >= 0; i--) {
            Long nu = repository.totaleGiorno(i, request.getAffiliateId(), request.getAdvertiserId()).stream().mapToLong(value -> value.gettotale()).sum();
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
        log.trace("CPC {}", mainObj.toString());
        return mainObj.toString();
    }

    /**
     * Returns a JSON array containing the 10-day total for each day, starting from the current day.
     *
     * @return a JSON array containing the 10-day total for each day, starting from the current day
     */
    public String get10DayTotal() {
        FilterStatistics request = new FilterStatistics();
        request.setDays(10);
        if (jwtUserDetailsService.isAffiliate()) {
            request.setAffiliateId(jwtUserDetailsService.getAffiliateId());
        } else if (jwtUserDetailsService.isAdvertiser()) {
            request.setAdvertiserId(jwtUserDetailsService.getAdvertiserId());
        }
        JSONArray jsonArray = new JSONArray();
        for (int i = request.getDays(); i >= 0; i--) {
            Long nu = repository.totaleGiorno(i, request.getAffiliateId(), request.getAdvertiserId()).stream().mapToLong(value -> value.gettotale()).sum();
            if (nu == null)
                nu = 0L;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(LocalDate.now().minusDays(i).toString(), nu);
            jsonArray.put(jsonObject);
        }
        return jsonArray.toString();
    }

    /**
     * Returns a list of the top 100 campaigns and their total clicks in a specific date range.
     *
     * @param request the filter statistics
     * @return a list of the top 100 campaigns and their total clicks in a specific date range
     */
    public List<TopCampagne> getTopCampaignsDayRange(FilterStatistics request) {
        if (jwtUserDetailsService.isAffiliate()) {
            request.setAffiliateId(jwtUserDetailsService.getAffiliateId());
        } else if (jwtUserDetailsService.isAdvertiser()) {
            request.setAdvertiserId(jwtUserDetailsService.getAdvertiserId());
        }
        return repository.listaTopCampagneTotale(request.getDays(), request.getAffiliateId(), request.getAdvertiserId());
    }

    public List<TopCampagne> getValueTopCampaignsDayRange(FilterStatistics request) {
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

// PER WIDGET GRAFICO
//    public String getWidgetCampaignsDayCpc() {
//        Filter request = new Filter();
//        request.setDoyMenoDieci(LocalDate.now().getDayOfYear() - 11);
//        request.setYear((long) LocalDate.now().getYear());
//        if (Boolean.FALSE.equals(jwtUserDetailsService.isAdmin()))
//            request.setAffiliateId(jwtUserDetailsService.getAffiliateId());
//        Page<WidgetCampaignDayCpc> tutto = widgetCampaignDayCpcRepository.findAll(getSpecificationCampaignDayCpc(request), pageableDoy);
//
//        Set<Long> doys = tutto.stream().map(WidgetCampaignDayCpc::getDoy).collect(Collectors.toSet());
//        Set<String> camps = tutto.stream().map(WidgetCampaignDayCpc::getCampaign).collect(Collectors.toSet());
//        Set<Long> doysDaVerificare = new HashSet<>();
//        if (!doys.isEmpty())
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
//                if (Boolean.FALSE.equals(jwtUserDetailsService.isAdmin()))
//                    filter.setAffiliateId(jwtUserDetailsService.getAffiliateId());
//                List<WidgetCampaignDayCpc> giornato = widgetCampaignDayCpcRepository.findAll(getSpecificationCampaignDayCpc(filter), pageableDoy).stream().collect(Collectors.toList());
//                for (WidgetCampaignDayCpc w : giornato)
//                    dd = dd + w.getTotale();
//                ja.put(dd);
//            });
//
//            ele.put("data", ja);
//            arr.put(ele);
//        });
//
//        JSONArray xSeries = new JSONArray();
//        LocalDate today = LocalDate.now();
//        doysDaVerificare.stream().sorted().forEach(gg -> xSeries.put(today.withDayOfYear(Math.toIntExact(gg)).toString()));
//
//        mainObj.put("series", arr);
//        mainObj.put("xSeries", xSeries);
//
//        return mainObj.toString();
//    }

    /**
     * ============================================================================================================
     **/

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class FilterStatistics {
        private Long affiliateId;
        private Long advertiserId;
        private Integer days;
    }

}