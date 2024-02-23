package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.*;
import it.cleverad.engine.persistence.model.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/stats")
public class StatisticsController {

    @Autowired
    private ViewBusiness business;
    @Autowired
    private StatCPCBusiness statCPCBusiness;
    @Autowired
    private StatCPLBusiness statCPLBusiness;
    @Autowired
    private StatCPMBusiness statCPMBusiness;

    @Autowired
    private StatBusiness statBusiness;

    @Autowired
    private AgentBusiness agentBusiness;

    /**
     * ============================================================================================================
     **/

    @GetMapping(path = "/cpc/top")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<WidgetCampaignDayCpc> getStatTopCpc(@PathVariable(value = "6") Integer giorni) {
        return statCPCBusiness.getTopCampaignsDayCpc(giorni);
    }

    @GetMapping(path = "/cpc/campaign/day/total")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String getStatTotaleDayCpc() {
        return statCPCBusiness.getStatTotaleDayCpc();
    }

    @GetMapping(path = "/cpc/campaign/day")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<WidgetCampaignDayCpc> getStatCpcCampaignDay(StatCPCBusiness.Filter request, Pageable pageable) {
        return statCPCBusiness.getStatCampaignDayCpc(request, pageable);
    }

    @GetMapping(path = "/cpc/campaign/{id}/day")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<WidgetCampaignDayCpc> getStatCpcCampaignDay(@PathVariable Long id, Pageable pageable) {
        StatCPCBusiness.Filter request = new StatCPCBusiness.Filter();
        request.setCampaignId(id);
        return statCPCBusiness.getStatCampaignDayCpc(request, pageable);
    }

    @GetMapping(path = "/cpc/campaign/day/widget")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String getStatCpcDayWidget() {
        return statCPCBusiness.getWidgetCampaignsDayCpc();
    }

    @GetMapping(path = "/dashboard/cpc/total/ten")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String get10DayTotal() {
        return statCPCBusiness.get10DayTotal();
    }

    /**
     * ============================================================================================================
     **/

    @GetMapping(path = "/cpm/campaign/day/widget")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String getStatCpmDayWidget() {
        return statCPMBusiness.getWidgetCampaignsDayCpm();
    }

    @GetMapping(path = "/cpm/campaign/day/total")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String getStatTotaleDayCpm(StatCPMBusiness.Filter request) {
        return statCPMBusiness.getStatTotaleDayCpm();
    }

    @GetMapping(path = "/cpm/top")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<WidgetCampaignDayCpm> getStatTopCpm() {
        return statCPMBusiness.getTopCampaignsDayCpm();
    }


    @GetMapping(path = "/cpm/campaign/day")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<WidgetCampaignDayCpm> getStatDayCpm(StatCPMBusiness.Filter request, Pageable pageable) {
        return statCPMBusiness.getStatCampaignDayCpm(request, pageable);
    }

    @GetMapping(path = "/cpm/campaign/{id}/day")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<WidgetCampaignDayCpm> getStatCampaignDayCpm(@PathVariable Long id, Pageable pageable) {
        StatCPMBusiness.Filter request = new StatCPMBusiness.Filter();
        request.setCampaignId(id);
        return statCPMBusiness.getStatCampaignDayCpm(request, pageable);
    }


    /**
     * ============================================================================================================
     **/

    @GetMapping(path = "/cpl/top")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<WidgetCampaignDayCpl> getStatTopCpl() {
        return statCPLBusiness.getTopCampaignsDayCpl();
    }

    @GetMapping(path = "/cpl/campaign/day/total")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String getStatTotaleDayCpl(StatCPLBusiness.Filter request) {
        return statCPLBusiness.getStatTotaleDayCpl(request);
    }

    @GetMapping(path = "/cpl/campaign/day")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<WidgetCampaignDayCpl> getStatCplDay(StatCPLBusiness.Filter request, Pageable pageable) {
        return statCPLBusiness.getStatCampaignDayCpl(request, pageable);
    }

    @GetMapping(path = "/cpl/campaign/{id}/day")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<WidgetCampaignDayCpl> getStatCplCampaignDay(@PathVariable Long id, Pageable pageable) {
        StatCPLBusiness.Filter request = new StatCPLBusiness.Filter();
        request.setCampaignId(id);
        return statCPLBusiness.getStatCampaignDayCpl(request, pageable);
    }

    @GetMapping(path = "/cpl/campaign/day/widget")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String getStatCplDayWidget() {
        return statCPLBusiness.getWidgetCampaignsDayCpl();
    }

    /**
     * ============================================================================================================
     **/

    @GetMapping(path = "/cps/top")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<WidgetCampaignDayCps> getStatTopCps() {
        return business.getTopCampaignsDayCps();
    }

    @GetMapping(path = "/cps/campaign/day")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<WidgetCampaignDayCps> getStatCpsCampaignDay(ViewBusiness.Filter request, Pageable pageable) {
        return business.getStatCampaignDayCps(request, pageable);
    }

    @GetMapping(path = "/cps/campaign/{id}/day")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<WidgetCampaignDayCps> getStatCpsCampaignDay(@PathVariable Long id, Pageable pageable) {
        ViewBusiness.Filter request = new ViewBusiness.Filter();
        request.setCampaignId(id);
        return business.getStatCampaignDayCps(request, pageable);
    }

    @GetMapping(path = "/cps/campaign/day/widget")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String getStatCpsDayWidget() {
        return business.getWidgetCampaignsDayCps();
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