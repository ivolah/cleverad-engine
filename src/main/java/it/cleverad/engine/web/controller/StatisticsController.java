package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.ViewBusiness;
import it.cleverad.engine.business.ReportBusiness;
import it.cleverad.engine.persistence.model.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/stats")
public class StatisticsController {

    @Autowired
    private ViewBusiness business;

    @Autowired
    private ReportBusiness topCampagneBusiness;

    /**
     * ============================================================================================================
     **/

    @GetMapping(path = "/cpc/click")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<StatCpcClickCampaignMedia> getStatCpcClickCampaign(ViewBusiness.Filter request, Pageable pageable) {
        return business.searchStatCpcClickCampaign(request, pageable);
    }

    @GetMapping(path = "/cpc/click/day")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<StatCpcClickCampaignMediaDay> getStatCpcClickCampaignDay(ViewBusiness.Filter request, Pageable pageable) {
        return business.searchStatCpcClickCampaignMediaDay(request, pageable);
    }

    @GetMapping(path = "/cpc/value")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<StatCpcValueCampaign> getTargetStatCpcValueCampaign(ViewBusiness.Filter request, Pageable pageable) {
        return business.searchStatCpcValueCampaign(request, pageable);
    }

    @GetMapping(path = "/cpc/value/day")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<StatCpcValueCampaignWeek> getTargetStatCpcValueCampaignWeek(ViewBusiness.Filter request, Pageable pageable) {
        return business.searchStatCpcValueCampaignWeek(request, pageable);
    }

    @GetMapping(path = "/cpc/transaction")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<StatCpcTransactionCampaignMedia> getStatCpcTransactionCampaign(ViewBusiness.Filter request, Pageable pageable) {
        return business.searchStatCpcTransactionCampaign(request, pageable);
    }


    @GetMapping(path = "/cpl/value")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<StatCplValueCampaign> getStatCplValueCampaign(ViewBusiness.Filter request, Pageable pageable) {
        return business.searchStatCplValueCampaign(request, pageable);
    }

    @GetMapping(path = "/cpl/value/week")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<StatCplValueCampaignWeek> getStatCplValueCampaignWeek(ViewBusiness.Filter request, Pageable pageable) {
        return business.searchStatCplValueCampaignWeek(request, pageable);
    }


    /**
     * ============================================================================================================
     **/

}
