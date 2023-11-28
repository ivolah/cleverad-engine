package it.cleverad.engine.business;

import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.persistence.model.service.WidgetCampaignDayCpc;
import it.cleverad.engine.persistence.repository.service.WidgetCampaignDayCpcRepository;
import it.cleverad.engine.persistence.repository.service.WidgetCampaignDayCplRepository;
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
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Transactional
public class StatBusiness {

    @Autowired
    private WidgetCampaignDayCpcRepository widgetCampaignDayCpcRepository;
    @Autowired
    private StatCPCBusiness statCPCBusiness;
    @Autowired
    private WidgetCampaignDayCplRepository widgetCampaignDayCplRepository;
    @Autowired
    private StatCPLBusiness statCPLBusiness;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    private final Pageable pageableDoy = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("year").descending().and(Sort.by("doy").descending()));

    /**
     * ===========================================================================================================
     * =================================================================<==========================================
     **/

    public String getLast10Days() {

        LocalDate today = LocalDate.now();
        Map<Integer, Integer> dayOfYearMap = new HashMap<>();
        for (int i = 0; i <= 9; i++) {
            LocalDate previousDay = today.minusDays(i);
            dayOfYearMap.put(previousDay.getDayOfYear(), previousDay.getYear());
        }


        JSONObject root = new JSONObject();
        JSONArray series = new JSONArray();

        // gestione totale click
        JSONArray seriesCpc = new JSONArray();
        dayOfYearMap.forEach((doy, year) -> {
            StatCPCBusiness.Filter filterCPC = new StatCPCBusiness.Filter();
            filterCPC.setDoy(Long.valueOf(doy));
            filterCPC.setYear(Long.valueOf(year));
            if (Boolean.FALSE.equals(jwtUserDetailsService.isAdmin()))
                filterCPC.setAffiliateId(jwtUserDetailsService.getAffiliateID());
            Double tot = 0D;
            for (WidgetCampaignDayCpc w : widgetCampaignDayCpcRepository.findAll(statCPCBusiness.getSpecificationCampaignDayCpc(filterCPC), pageableDoy))
                tot = tot + w.getTotale();
            seriesCpc.put(tot);
        });
        JSONObject cpc = new JSONObject();
        cpc.put("name", "Click");
        cpc.put("data", seriesCpc);
        series.put(cpc);

        // gestione totale lead

        JSONArray seriesCpl = new JSONArray();
        dayOfYearMap.forEach((doy, year) -> {
            StatCPLBusiness.Filter filterCPL = new StatCPLBusiness.Filter();
            filterCPL.setDoy(Long.valueOf(doy));
            filterCPL.setYear(Long.valueOf(year));
            if (Boolean.FALSE.equals(jwtUserDetailsService.isAdmin()))
                filterCPL.setAffiliateId(jwtUserDetailsService.getAffiliateID());
            Long totCpl = widgetCampaignDayCplRepository.findAll(statCPLBusiness.getSpecificationCampaignDayCpl(filterCPL), pageableDoy).getTotalElements();
            seriesCpl.put(totCpl);
        });
        JSONObject cpl = new JSONObject();
        cpl.put("name", "Lead");
        cpl.put("data", seriesCpl);
        series.put(cpl);

        // gestione totale Valore

        JSONArray seriesVal = new JSONArray();
        dayOfYearMap.forEach((doy, year) -> {
            Double tot = 0D;

            StatCPCBusiness.Filter filterCPC = new StatCPCBusiness.Filter();
            filterCPC.setDoy(Long.valueOf(doy));
            filterCPC.setYear(Long.valueOf(year));
            if (Boolean.FALSE.equals(jwtUserDetailsService.isAdmin()))
                filterCPC.setAffiliateId(jwtUserDetailsService.getAffiliateID());
            for (WidgetCampaignDayCpc w : widgetCampaignDayCpcRepository.findAll(statCPCBusiness.getSpecificationCampaignDayCpc(filterCPC), pageableDoy))
                tot = tot + w.getValore();

            StatCPLBusiness.Filter filterCPL = new StatCPLBusiness.Filter();

            filterCPL.setDoy(Long.valueOf(doy));
            filterCPL.setYear(Long.valueOf(year));
            if (Boolean.FALSE.equals(jwtUserDetailsService.isAdmin()))
                filterCPL.setAffiliateId(jwtUserDetailsService.getAffiliateID());
            for (WidgetCampaignDayCpc w : widgetCampaignDayCpcRepository.findAll(statCPCBusiness.getSpecificationCampaignDayCpc(filterCPC), pageableDoy))
                tot = tot + w.getValore();

            seriesVal.put(tot);
        });
        JSONObject val = new JSONObject();
        val.put("name", "Totale (€)");
        val.put("data", seriesVal);
        series.put(val);

        root.put("series", series);

        JSONArray x = new JSONArray();
        dayOfYearMap.forEach((doy, year) -> x.put(today.withDayOfYear(Math.toIntExact(doy)).toString()));
        root.put("x", x);

        return root.toString();
    }

}