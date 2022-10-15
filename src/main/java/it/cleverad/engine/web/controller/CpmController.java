package it.cleverad.engine.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cleverad.engine.business.CpmBusiness;
import it.cleverad.engine.web.dto.CpmDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*",maxAge = 3600)
@Tag(name = "CPM", description = "Endpoints for all the CPM Operations")
@RestController
@RequestMapping(value = "/cpm")
public class CpmController {

    @Autowired
    private CpmBusiness business;

    /**
     * ============================================================================================================
     **/

    @Operation(summary = "Create Impression", description = "Creates a new CPM")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CpmDTO create(@ModelAttribute CpmBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

    /**
     * ============================================================================================================
     **/

}
