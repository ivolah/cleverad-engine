package it.cleverad.engine.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cleverad.engine.business.TrackingBusiness;
import it.cleverad.engine.web.dto.TrackingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*",maxAge = 3600)
@Tag(name = "Tracking", description = "Endpoints for all the Tracking Operations")
@RestController
@RequestMapping(value = "/tracking")
public class TrackingController {

    @Autowired
    private TrackingBusiness business;

    /**
     * ============================================================================================================
     **/

    @Operation(summary = "Create Tracking", description = "Creates a new Tracking")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public TrackingDTO create(@ModelAttribute TrackingBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

    /**
     * ============================================================================================================
     **/

}
