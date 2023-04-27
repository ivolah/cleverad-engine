package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.ViewBusiness;
import it.cleverad.engine.persistence.model.service.StatCampaignDayCpc;
import it.cleverad.engine.persistence.model.service.StatCampaignDayCpl;
import it.cleverad.engine.persistence.model.service.StatCampaignDayCpm;
import it.cleverad.engine.persistence.model.service.StatCampaignDayCps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/stats")
public class StatisticsController {

    @Autowired
    private ViewBusiness business;

    /**
     * ============================================================================================================
     **/

    @GetMapping(path = "/cpc/top")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<StatCampaignDayCpc> getStatTopCpc() {
        return business.getTopCampaignsDayCpc();
    }

    @GetMapping(path = "/cpc/campaign/day")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<StatCampaignDayCpc> getStatCpcDay(ViewBusiness.Filter request, Pageable pageable) {
        return business.getStatCampaignDayCpc(request, pageable);
    }
    @GetMapping(path = "/cpc/campaign/{id}/day")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<StatCampaignDayCpc> getStatCpcCampaignDay(@PathVariable Long id, Pageable pageable) {
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
    public Page<StatCampaignDayCpm> getStatTopCpm() {
        return business.getTopCampaignsDayCpm();
    }
    @GetMapping(path = "/cpm/campaign/day")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<StatCampaignDayCpm> getStatDayCpm(ViewBusiness.Filter request, Pageable pageable) {
        return business.getStatCampaignDayCpm(request, pageable);
    }
    @GetMapping(path = "/cpm/campaign/{id}/day")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<StatCampaignDayCpm> getStatCampaignDayCpm(@PathVariable Long id, Pageable pageable) {
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
    public Page<StatCampaignDayCpl> getStatTopCpl() {
        return business.getTopCampaignsDayCpl();
    }
    @GetMapping(path = "/cpl/campaign/day")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<StatCampaignDayCpl> getStatCplDay(ViewBusiness.Filter request, Pageable pageable) {
        return business.getStatCampaignDayCpl(request, pageable);
    }
    @GetMapping(path = "/cpl/campaign/{id}/day")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<StatCampaignDayCpl> getStatCplCampaignDay(@PathVariable Long id, Pageable pageable) {
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
    public Page<StatCampaignDayCps> getStatTopCps() {
        return business.getTopCampaignsDayCps();
    }
    @GetMapping(path = "/cps/campaign/day")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<StatCampaignDayCps> getStatCpsCampaignDay(ViewBusiness.Filter request, Pageable pageable) {
        return business.getStatCampaignDayCps(request, pageable);
    }
    @GetMapping(path = "/cps/campaign/{id}/day")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<StatCampaignDayCps> getStatCpsCampaignDay(@PathVariable Long id, Pageable pageable) {
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
}
