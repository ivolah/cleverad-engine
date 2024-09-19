package it.cleverad.engine.business;

import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.persistence.model.service.TopCampagne;
import it.cleverad.engine.persistence.repository.service.TransactionCPSRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
public class StatCPSBusiness {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private TransactionCPSRepository repository;

    /**
     * ===========================================================================================================
     * >>>  CPS  CPS  CPS  CPS  CPS  CPS  CPS  CPS  CPS  CPS  CPS  CPS  CPS  CPS  CPS  CPS  CPS  CPS  CPS  CPS  CPS  CPS  CPS  CPS  CPS <<<
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
        log.info(" CPS {}", mainObj.toString());
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