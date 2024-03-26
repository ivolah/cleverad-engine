package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.*;
import it.cleverad.engine.persistence.model.service.TopCampagne;
import it.cleverad.engine.persistence.model.service.WidgetAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/stats")
public class StatisticsController {

    @Autowired
    private StatCPCBusiness statCPCBusiness;
    @Autowired
    private StatCPLBusiness statCPLBusiness;
    @Autowired
    private StatCPMBusiness statCPMBusiness;
    @Autowired
    private StatCPSBusiness statCPSBusiness;
    @Autowired
    private StatBusiness statBusiness;
    @Autowired
    private AgentBusiness agentBusiness;

    /**
     * ============================================================================================================
     **/

    @GetMapping(path = "/cpc/top")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<TopCampagne> getCPCTopCampaigns6DayRange() {
        StatCPCBusiness.FilterStatistics request = new StatCPCBusiness.FilterStatistics();
        request.setDays(6);
        return statCPCBusiness.getTopCampaignsDayRange(request);
    }

    @GetMapping(path = "/cpc/top/valore")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<TopCampagne> getCPCValueTopCampaigns6DayRange() {
        StatCPCBusiness.FilterStatistics request = new StatCPCBusiness.FilterStatistics();
        request.setDays(6);
        return statCPCBusiness.getValueTopCampaignsDayRange(request);
    }

    @GetMapping(path = "/cpc/campaign/day/total")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String getCPCStatTotale6Giorni(StatCPCBusiness.FilterStatistics request) {
        if (request.getDays() == null) request.setDays(6);
        return statCPCBusiness.getStatTotaleGiorno(request);
    }

    @GetMapping(path = "/dashboard/cpc/total/ten")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String getCPC10DayTotal() {
        return statCPCBusiness.get10DayTotal();
    }

    /**
     * ============================================================================================================
     **/

    @GetMapping(path = "/cpl/top")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<TopCampagne> getCPLTopCampaigns6DayRange() {
        StatCPCBusiness.FilterStatistics request = new StatCPCBusiness.FilterStatistics();
        request.setDays(6);
        return statCPLBusiness.getTopCampaignsDayRange(request);
    }

    @GetMapping(path = "/cpl/top/valore")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<TopCampagne> getCPLValueTopCampaigns6DayRange() {
        StatCPCBusiness.FilterStatistics request = new StatCPCBusiness.FilterStatistics();
        request.setDays(6);
        return statCPLBusiness.getValueTopCampaignsDayRange(request);
    }

    @GetMapping(path = "/cpl/campaign/day/total")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String getCPLStatTotale6Giorni(StatCPCBusiness.FilterStatistics request) {
        if (request.getDays() == null) request.setDays(6);
        return statCPLBusiness.getStatTotaleGiorno(request);
    }

    @GetMapping(path = "/dashboard/cpl/total/ten")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String getCPL10DayTotal() {
        return statCPLBusiness.get10DayTotal();
    }

    /**
     * ============================================================================================================
     **/

    @GetMapping(path = "/cpm/campaign/day/widget")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String getStatCpmDayWidget() {
        return statCPMBusiness.getWidgetCampaignsDayCpm();
    }

    @GetMapping(path = "/cpm/top")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<TopCampagne> getCPMTopCampaigns6DayRange() {
        StatCPCBusiness.FilterStatistics request = new StatCPCBusiness.FilterStatistics();
        request.setDays(6);
        return statCPMBusiness.getTopCampaignsDayRange(request);
    }

    @GetMapping(path = "/cpm/top/valore")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<TopCampagne> getCPMValueTopCampaigns6DayRange() {
        StatCPCBusiness.FilterStatistics request = new StatCPCBusiness.FilterStatistics();
        request.setDays(6);
        return statCPMBusiness.getValueTopCampaignsDayRange(request);
    }

    @GetMapping(path = "/cpm/campaign/day/total")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String getCPMStatTotale6Giorni(StatCPCBusiness.FilterStatistics request) {
        if (request.getDays() == null) request.setDays(6);
        return statCPMBusiness.getStatTotaleGiorno(request);
    }

    @GetMapping(path = "/dashboard/cpm/total/ten")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String getCPM10DayTotal() {
        return statCPMBusiness.get10DayTotal();
    }

    /**
     * ============================================================================================================
     **/

    @GetMapping(path = "/cps/top")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<TopCampagne> getCPSTopCampaigns6DayRange() {
        StatCPCBusiness.FilterStatistics request = new StatCPCBusiness.FilterStatistics();
        request.setDays(6);
        return statCPSBusiness.getTopCampaignsDayRange(request);
    }

    @GetMapping(path = "/cps/top/valore")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<TopCampagne> getCPSValueTopCampaigns6DayRange() {
        StatCPCBusiness.FilterStatistics request = new StatCPCBusiness.FilterStatistics();
        request.setDays(6);
        return statCPSBusiness.getValueTopCampaignsDayRange(request);
    }

    @GetMapping(path = "/cps/campaign/day/total")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String getCPSStatTotale6Giorni(StatCPCBusiness.FilterStatistics request) {
        if (request.getDays() == null) request.setDays(6);
        return statCPSBusiness.getStatTotaleGiorno(request);
    }

    @GetMapping(path = "/dashboard/cps/total/ten")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String getCPS10DayTotal() {
        return statCPSBusiness.get10DayTotal();
    }

    /**
     * ============================================================================================================
     **/

    @GetMapping(path = "/agent/os")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<WidgetAgent> searchOS(AgentBusiness.Filter request) {
        return agentBusiness.searchOS(request);
    }

    @GetMapping(path = "/agent/device")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<WidgetAgent> searchDevic(AgentBusiness.Filter request) {
        return agentBusiness.searchDevic(request);
    }

    @GetMapping(path = "/agent/data")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<WidgetAgent> searchAgent(AgentBusiness.Filter request) {
        return agentBusiness.searchAgent(request);
    }

    @GetMapping(path = "/agent/data/detailed")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<WidgetAgent> searchAgentDetailed(AgentBusiness.Filter request) {
        return agentBusiness.searchAgentDetailed(request);
    }

    /**
     * ============================================================================================================
     **/

    @GetMapping(path = "/dashboard/total/ten")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String getLast10Days() {
        return statBusiness.getLast10Days();
    }

}