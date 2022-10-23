package it.cleverad.engine.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cleverad.engine.business.CampaignBusiness;
import it.cleverad.engine.web.dto.CampaignDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Tag(name = "Campaigns", description = "Endpoints for all the Campaigs Operations")
@RestController
@RequestMapping(value = "/campaign")
public class CampaignController {

    @Autowired
    private CampaignBusiness business;

    /**
     * ============================================================================================================
     **/

    @Operation(summary = "Create Campaign", description = "Creates a new Campaign")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CampaignDTO create(@ModelAttribute CampaignBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

    @Operation(summary = "Lists the Campaigns", description = "Lists the Campaigns, searched and paginated")
    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<CampaignDTO> search(CampaignBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @Operation(summary = "Update the Campaign", description = "Update the specific Campaign")
    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CampaignDTO update(@PathVariable Long id, @RequestBody CampaignBusiness.Filter request) {
        return business.update(id, request);
    }

    @Operation(summary = "Get the Campaign", description = "Get the specific Campaign")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CampaignDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @Operation(summary = "Delete Campaign", description = "Delete the specific Campaign")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }

    @Operation(summary = "Get the Affiliate Campaigns", description = "Get the specific AffiliateCampaigns")
    @GetMapping("/{affiliateId}/affiliate")
    public Page<CampaignDTO> getCampaigns(@PathVariable Long affiliateId) {
        return business.getCampaigns(affiliateId);
    }

    /**
     * ============================================================================================================
     **/

}
