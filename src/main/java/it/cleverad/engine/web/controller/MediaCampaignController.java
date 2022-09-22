package it.cleverad.engine.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cleverad.engine.business.MediaCampaignBusiness;
import it.cleverad.engine.web.dto.MediaCampaignDTO;
import it.cleverad.engine.web.exception.PostgresCleveradException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Tag(name = "Media Campaign", description = "Endpoints for all the Media Campaign Operations")
@RestController
@RequestMapping(value = "/mediacampaign")
public class MediaCampaignController {

    @Autowired
    private MediaCampaignBusiness business;

    /**
     * ============================================================================================================
     **/

    @Operation(summary = "Create ", description = "Creates a new ")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MediaCampaignDTO create(@ModelAttribute MediaCampaignBusiness.BaseCreateRequest request) throws PostgresCleveradException {
        return business.create(request);
    }

    @Operation(summary = "Get", description = "Get the specific ")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MediaCampaignDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @Operation(summary = "Delete", description = "Delete the specific ")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }

    /**
     * ============================================================================================================
     **/

}
