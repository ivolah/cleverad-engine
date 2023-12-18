package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.RigeneraCPCBusiness;
import it.cleverad.engine.business.RigeneraCPLBusiness;
import it.cleverad.engine.business.RigeneraWalletBusiness;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/rigenera")
@Slf4j
public class RigeneraController {

    @Autowired
    private RigeneraCPCBusiness rigeneraCPCBusiness;
    @Autowired
    private RigeneraCPLBusiness rigeneraCPLBusiness;
    @Autowired
    private RigeneraWalletBusiness rigeneraWalletBusiness;

    /**
     * ============================================================================================================
     **/

    @PostMapping("/cpl")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void manageCPL(@ModelAttribute RigeneraCPLBusiness.FilterUpdate request) {
        rigeneraCPLBusiness.rigenera(Integer.parseInt(request.getYear()), Integer.parseInt(request.getMonth()), Integer.parseInt(request.getDay()), request.getAffiliateId());
    }

    @PostMapping("/cpc")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void manageCPC(@ModelAttribute RigeneraCPCBusiness.FilterUpdate request) {
        rigeneraCPCBusiness.rigenera(request.getYear(), request.getMonth(), request.getDay(), request.getAffiliateId(), request.getCampaignId());
    }

    /**
     * ============================================================================================================
     **/

    @PostMapping("/wallet")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void wallet(@ModelAttribute RigeneraWalletBusiness.FilterUpdate request) {
        rigeneraWalletBusiness.rigenera(request.getAffiliateId());
    }

}