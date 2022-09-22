package it.cleverad.engine.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cleverad.engine.business.CampaignRevenueFactorBusiness;
import it.cleverad.engine.web.dto.CampaignRevenueFactorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Tag(name = "CampaignRevenueFactor", description = "Endpoints for all the CampaignRevenueFactor Operations")
@RestController
@RequestMapping(value = "/campaignrevenuefactor")
public class CampaignRevenueFactorController {

    @Autowired
    private CampaignRevenueFactorBusiness business;

    /**
     * ============================================================================================================
     **/

    @Operation(summary = "Create CampaignRevenueFactor", description = "Creates a new CampaignRevenueFactor")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CampaignRevenueFactorDTO create(@ModelAttribute CampaignRevenueFactorBusiness.BaseCreateRequest request) {
        return   business.create(request);
    }

    @Operation(summary = "Lists the CampaignRevenueFactor", description = "Lists the CampaignRevenueFactor, searched and paginated")
    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<CampaignRevenueFactorDTO> search(CampaignRevenueFactorBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @Operation(summary = "Update the CampaignRevenueFactor", description = "Update the specific CampaignRevenueFactor")
    @PatchMapping(path = "/{id}" )
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CampaignRevenueFactorDTO update(@PathVariable Long id, @RequestBody CampaignRevenueFactorBusiness.Filter request) {
        return  business.update(id, request);
    }

    @Operation(summary = "Get the CampaignRevenueFactor", description = "Get the specific CampaignRevenueFactor")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CampaignRevenueFactorDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @Operation(summary = "Delete CampaignRevenueFactor", description = "Delete the specific CampaignRevenueFactor")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }

    /**
     * ============================================================================================================
     **/

}
