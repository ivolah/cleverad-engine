package it.cleverad.engine.business;

import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.persistence.model.service.TopCampagne;
import it.cleverad.engine.persistence.repository.service.TransactionCPCRepository;
import it.cleverad.engine.persistence.repository.service.TransactionCPLRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Component
@Transactional
public class StatBusiness {

    @Autowired
    private TransactionCPCRepository cpcRepository;
    @Autowired
    private TransactionCPLRepository cplRepository;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    /**
     * ===========================================================================================================
     * =================================================================<==========================================
     **/

    public String getLast10Days() {

        StatCPCBusiness.FilterStatistics request = new StatCPCBusiness.FilterStatistics();
        if (jwtUserDetailsService.isAffiliate()) {
            request.setAffiliateId(jwtUserDetailsService.getAffiliateId());
        } else if (jwtUserDetailsService.isAdvertiser()) {
            request.setAdvertiserId(jwtUserDetailsService.getAdvertiserId());
        }

        // Elementi padre
        JSONObject root = new JSONObject();
        JSONArray series = new JSONArray();

        // CLICK :: gestione totale click
        JSONArray seriesCpc = new JSONArray();
        for (int i = 9; i >= 0; i--) {
            Long tot = 0L;
            TopCampagne topCampagne = cpcRepository.totaleGiorno(i, request.getAffiliateId(), request.getAdvertiserId()).stream().findFirst().orElse(null);
            if (topCampagne != null) {
                tot = topCampagne.gettotale();
            }
            seriesCpc.put(tot);
        }
        JSONObject cpc = new JSONObject();
        cpc.put("name", "Click");
        cpc.put("data", seriesCpc);
        series.put(cpc);

        // LEAD :: gestione totale lead
        JSONArray seriesCpl = new JSONArray();
        for (int i = 9; i >= 0; i--) {
            Long tot = 0L;
            TopCampagne topCampagne = cplRepository.totaleGiorno(i, request.getAffiliateId(), request.getAdvertiserId()).stream().findFirst().orElse(null);
            if (topCampagne != null) {
                tot = topCampagne.gettotale();
            }
            seriesCpl.put(tot);
        }
        JSONObject cpl = new JSONObject();
        cpl.put("name", "Lead");
        cpl.put("data", seriesCpl);
        series.put(cpl);

        // VALORE GIORNO :: gestione totale Valore
        JSONArray seriesVal = new JSONArray();
        for (int i = 9; i >= 0; i--) {
            Double totCpc = 0D;
            TopCampagne topCampagneCPC = cpcRepository.totaleGiorno(i, request.getAffiliateId(), request.getAdvertiserId()).stream().findFirst().orElse(null);
            if (topCampagneCPC != null)
                totCpc = topCampagneCPC.getvalore();

            Double totCpl = 0D;
            TopCampagne topCampagneCPL = cplRepository.totaleGiorno(i, request.getAffiliateId(), request.getAdvertiserId()).stream().findFirst().orElse(null);
            if (topCampagneCPL != null)
                totCpl = topCampagneCPL.getvalore();

            seriesVal.put(totCpc + totCpl);
        }
        JSONObject val = new JSONObject();
        val.put("name", "Totale (€)");
        val.put("data", seriesVal);
        series.put(val);

        root.put("series", series);

        JSONArray x = new JSONArray();
        for (int i = 9; i >= 0; i--) {
            x.put(LocalDate.now().minusDays(i).toString());
        }

        root.put("x", x);
        return root.toString();
    }

}