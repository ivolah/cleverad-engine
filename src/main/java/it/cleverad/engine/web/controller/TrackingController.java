package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.TrackingBusiness;
import it.cleverad.engine.web.dto.TrackingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*",maxAge = 3600)
@RestController
@RequestMapping(value = "/tracking")
public class TrackingController {

    @Autowired
    private TrackingBusiness business;

    /**
     * ============================================================================================================
     **/

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public TrackingDTO create(@ModelAttribute TrackingBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

    /**
     * ============================================================================================================
     **/

}
