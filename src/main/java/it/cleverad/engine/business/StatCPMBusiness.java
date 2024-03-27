package it.cleverad.engine.business;

import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.persistence.model.service.TopCampagne;
import it.cleverad.engine.persistence.model.service.WidgetCampaignDayCpm;
import it.cleverad.engine.persistence.repository.service.TransactionCPMRepository;
import it.cleverad.engine.persistence.repository.service.WidgetCampaignDayCpmRepository;
import it.cleverad.engine.persistence.repository.service.WidgetTopCPMRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
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
    @Autowired
    private TransactionCPMRepository repository;

    /**
     * ===========================================================================================================
     * >>> CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM <<<
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
            Long nu = repository.totaleGiorno(i, request.getAffiliateId(), request.getAdvertiserId()).stream().mapToLong(totale -> totale.gettotale()).sum();
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
        log.info("CPM {}", mainObj.toString());
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
            Long nu = repository.totaleGiorno(i, request.getAffiliateId(), request.getAdvertiserId()).stream().mapToLong(value -> value.gettotale()).sum();
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

    public String getWidgetCampaignsDayCpm() {

        // Prendo i top per ogni singolo giorno
        StatCPCBusiness.FilterStatistics request = new StatCPCBusiness.FilterStatistics();
        if (jwtUserDetailsService.isAffiliate()) {
            request.setAffiliateId(jwtUserDetailsService.getAffiliateId());
        } else if (jwtUserDetailsService.isAdvertiser()) {
            request.setAdvertiserId(jwtUserDetailsService.getAdvertiserId());
        }
        List<Long> listaIdCamapgne = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            List<Long> tc = repository.totaleGiorno(i, request.getAffiliateId(), request.getAdvertiserId()).stream().limit(5).map(topCampagne -> topCampagne.getid()).collect(Collectors.toList());
            listaIdCamapgne.addAll(tc);
        }

        // metto in lista in modo che non ci siano doppioni
        log.trace("Campagne pre :: {}", listaIdCamapgne.size());
        listaIdCamapgne = listaIdCamapgne.stream().distinct().collect(Collectors.toList());
        log.trace("Campagne post:: {}", listaIdCamapgne.size());
        listaIdCamapgne.stream().forEach(aLong -> log.info("Campagne {}", aLong));

        // setto gionri da verifica
        List<LocalDate> giorniDaVerificare = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            giorniDaVerificare.add(LocalDate.now().minusDays(i));
        }

        JSONObject mainObj = new JSONObject();
        JSONArray tutti = new JSONArray();
        listaIdCamapgne.stream().distinct().forEach(campagnaId -> {

            JSONObject ele = new JSONObject();
            String campagna = campaignBusiness.findById(campagnaId).getName();
            ele.put("name", campagna);
            JSONArray arrray = new JSONArray();

            // cerco giorno e calcolo totale
            giorniDaVerificare.stream().sorted().forEach(gg -> {

                Double dd = 0D;
                FilterStatistics filter = new FilterStatistics();
                if (jwtUserDetailsService.isAffiliate()) {
                    filter.setAffiliateId(jwtUserDetailsService.getAffiliateId());
                } else if (jwtUserDetailsService.isAdvertiser()) {
                    filter.setAdvertiserId(jwtUserDetailsService.getAdvertiserId());
                }
                Long totGiornoCampagna =   repository.totaleGiornoSpecifico(gg, filter.getAffiliateId(), filter.getAdvertiserId(), campagnaId).stream().mapToLong(totale -> totale.gettotale()).sum();
                if (totGiornoCampagna == null)
                    totGiornoCampagna = 0L;

                log.trace("campagna :: {}  ({}) :: {} :: {}", campagna, campagnaId, gg, dd);
                arrray.put(totGiornoCampagna);
            });

            ele.put("data", arrray);
            tutti.put(ele);
        });

        JSONArray xSeries = new JSONArray();
        giorniDaVerificare.stream().sorted().forEach(gg -> {
            xSeries.put(gg.toString());
        });

        mainObj.put("series", tutti);
        mainObj.put("xSeries", xSeries);
        return mainObj.toString();
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