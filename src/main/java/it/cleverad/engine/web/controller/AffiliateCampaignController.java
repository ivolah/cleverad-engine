package it.cleverad.engine.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cleverad.engine.business.AffiliateCampaignBusiness;
import it.cleverad.engine.web.dto.AffiliateCampaignDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Tag(name = "Affilaites Campaign", description = "Endpoints for all the Affilaites Campaign Operations")
@RestController
@RequestMapping(value = "/affiliatecampaign")
public class AffiliateCampaignController {

    @Autowired
    private AffiliateCampaignBusiness business;

    /**
     * ============================================================================================================
     **/

    @Operation(summary = "Create Affiliate", description = "Creates a new Affiliate")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AffiliateCampaignDTO create(@ModelAttribute AffiliateCampaignBusiness.BaseCreateRequest request) {
       return business.create(request);
    }

    @Operation(summary = "Get the Affiliate", description = "Get the specific Affiliate")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AffiliateCampaignDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @Operation(summary = "Delete Affiliate", description = "Delete the specific Affiliate")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }

    /**
     * ============================================================================================================
     **/

}
