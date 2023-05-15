package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.AgentBusiness;
import it.cleverad.engine.business.ViewBusiness;
import it.cleverad.engine.persistence.model.service.*;
import it.cleverad.engine.web.dto.AgentDTO;
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
    private AgentBusiness agentBusiness;

    /**
     * ============================================================================================================
     **/

    @GetMapping(path = "/cpc/top")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<WidgetCampaignDayCpc> getStatTopCpc(@PathVariable(value = "6") Integer giorni) {
        return business.getTopCampaignsDayCpc(giorni);
    }

    @GetMapping(path = "/cpc/campaign/day/total")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String getStatTotaleDayCpc(ViewBusiness.Filter request) {
        return business.getStatTotaleDayCpc(request);
    }

    @GetMapping(path = "/cpc/campaign/day")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<WidgetCampaignDayCpc> getStatCpcCampaignDay(ViewBusiness.Filter request, Pageable pageable) {
        return business.getStatCampaignDayCpc(request, pageable);
    }
    @GetMapping(path = "/cpc/campaign/{id}/day")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<WidgetCampaignDayCpc> getStatCpcCampaignDay(@PathVariable Long id, Pageable pageable) {
        ViewBusiness.Filter request = new ViewBusiness.Filter();
        request.setCampaignId(id);
        return business.getStatCampaignDayCpc(request, pageable);
    }

    @GetMapping(path = "/cpc/campaign/day/widget")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String getStatCpcDayWidget() {
        return business.getWidgetCampaignsDayCpc();
    }

    /**
     * ============================================================================================================
     **/

    @GetMapping(path = "/cpm/top")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<WidgetCampaignDayCpm> getStatTopCpm() {
        return business.getTopCampaignsDayCpm();
    }

    @GetMapping(path = "/cpm/campaign/day/total")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String getStatTotaleDayCpm(ViewBusiness.Filter request) {
        return business.getStatTotaleDayCpm(request);
    }

    @GetMapping(path = "/cpm/campaign/day")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<WidgetCampaignDayCpm> getStatDayCpm(ViewBusiness.Filter request, Pageable pageable) {
        return business.getStatCampaignDayCpm(request, pageable);
    }
    @GetMapping(path = "/cpm/campaign/{id}/day")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<WidgetCampaignDayCpm> getStatCampaignDayCpm(@PathVariable Long id, Pageable pageable) {
        ViewBusiness.Filter request = new ViewBusiness.Filter();
        request.setCampaignId(id);
        return business.getStatCampaignDayCpm(request, pageable);
    }

    @GetMapping(path = "/cpm/campaign/day/widget")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String getStatCpmDayWidget() {
        return business.getWidgetCampaignsDayCpm();
    }

    /**
     * ============================================================================================================
     **/

    @GetMapping(path = "/cpl/top")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<WidgetCampaignDayCpl> getStatTopCpl() {
        return business.getTopCampaignsDayCpl();
    }

    @GetMapping(path = "/cpl/campaign/day/total")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String getStatTotaleDayCpl(ViewBusiness.Filter request) {
        return business.getStatTotaleDayCpl(request);
    }

    @GetMapping(path = "/cpl/campaign/day")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<WidgetCampaignDayCpl> getStatCplDay(ViewBusiness.Filter request, Pageable pageable) {
        return business.getStatCampaignDayCpl(request, pageable);
    }
    @GetMapping(path = "/cpl/campaign/{id}/day")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<WidgetCampaignDayCpl> getStatCplCampaignDay(@PathVariable Long id, Pageable pageable) {
        ViewBusiness.Filter request = new ViewBusiness.Filter();
        request.setCampaignId(id);
        return business.getStatCampaignDayCpl(request, pageable);
    }

    @GetMapping(path = "/cpl/campaign/day/widget")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String getStatCplDayWidget() {
        return business.getWidgetCampaignsDayCpl();
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

}
