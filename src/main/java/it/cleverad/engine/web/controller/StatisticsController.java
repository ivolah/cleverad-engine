package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.StatsBusiness;
import it.cleverad.engine.persistence.model.service.*;
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
    private StatsBusiness business;

    /**
     * ============================================================================================================
     **/

    @GetMapping(path = "/cpc/click")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<StatCpcClickCampaignMedia> getStatCpcClickCampaign(StatsBusiness.Filter request, Pageable pageable) {
        return business.searchStatCpcClickCampaign(request, pageable);
    }

    @GetMapping(path = "/cpc/click/day")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<StatCpcClickCampaignMediaDay> getStatCpcClickCampaignDay(StatsBusiness.Filter request, Pageable pageable) {
        return business.searchStatCpcClickCampaignMediaDay(request, pageable);
    }

    @GetMapping(path = "/cpc/value")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<StatCpcValueCampaign> getTargetStatCpcValueCampaign(StatsBusiness.Filter request, Pageable pageable) {
        return business.searchStatCpcValueCampaign(request, pageable);
    }

    @GetMapping(path = "/cpc/value/day")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<StatCpcValueCampaignWeek> getTargetStatCpcValueCampaignWeek(StatsBusiness.Filter request, Pageable pageable) {
        return business.searchStatCpcValueCampaignWeek(request, pageable);
    }

    @GetMapping(path = "/cpc/transaction")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<StatCpcTransactionCampaignMedia> getStatCpcTransactionCampaign(StatsBusiness.Filter request, Pageable pageable) {
        return business.searchStatCpcTransactionCampaign(request, pageable);
    }


    @GetMapping(path = "/cpl/value")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<StatCplValueCampaign> getStatCplValueCampaign(StatsBusiness.Filter request, Pageable pageable) {
        return business.searchStatCplValueCampaign(request, pageable);
    }

    @GetMapping(path = "/cpl/value/week")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<StatCplValueCampaignWeek> getStatCplValueCampaignWeek(StatsBusiness.Filter request, Pageable pageable) {
        return business.searchStatCplValueCampaignWeek(request, pageable);
    }

    /**
     * ============================================================================================================
     **/

}
