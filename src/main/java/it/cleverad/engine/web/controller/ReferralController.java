package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.ReferralBusiness;
import it.cleverad.engine.config.model.Refferal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/referral")
public class ReferralController {

    @Autowired
    private ReferralBusiness business;

    /**
     * ============================================================================================================
     **/

    @PatchMapping(path = "/decode")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Refferal decc(@RequestBody ReferralBusiness.Filter request) {
        return business.decode(request);
    }

    @PatchMapping(path = "/generate")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Refferal generate(@RequestBody ReferralBusiness.FilterGenerate request) {
        return business.generate(request);
    }
    /**
     * ============================================================================================================
     **/

}