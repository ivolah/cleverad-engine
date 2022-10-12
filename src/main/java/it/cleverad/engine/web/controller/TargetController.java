package it.cleverad.engine.web.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.cleverad.engine.business.TrackingBusiness;
import it.cleverad.engine.web.dto.TargetDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*",maxAge = 3600)
@Tag(name = "Target", description = "Endpoints for all the Target Operations")
@RestController
@RequestMapping(value = "/target")
public class TargetController {

    @Autowired
    private TrackingBusiness business;

    /**
     * ============================================================================================================
     **/

    @PostMapping( consumes = MediaType.MULTIPART_FORM_DATA_VALUE )
    @ResponseStatus(HttpStatus.ACCEPTED)
    public TargetDTO getTarget(@ModelAttribute TrackingBusiness.BaseCreateRequest request) {
        return business.getTarget(request);
    }

    /**
     * ============================================================================================================
     **/

}
