package it.cleverad.engine.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cleverad.engine.business.AffiliateChannelCommissionCampaignBusiness;
import it.cleverad.engine.web.dto.AffiliateChannelCommissionCampaignDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Tag(name = "AffiliateChannelCommissionCampaigns", description = "Endpoints for all the AffiliateChannelCommissionCampaign Operations")
@RestController
@RequestMapping(value = "/affiliatechannelcommissioncampaign")
public class AffiliateChannelCommissionCampaignController {

    @Autowired
    private AffiliateChannelCommissionCampaignBusiness business;

    /**
     * ============================================================================================================
     **/

    @Operation(summary = "Create AffiliateChannelCommissionCampaign", description = "Creates a new AffiliateChannelCommissionCampaign")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AffiliateChannelCommissionCampaignDTO create(@ModelAttribute AffiliateChannelCommissionCampaignBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

//    @Operation(summary = "Lists the AffiliateChannelCommissionCampaigns", description = "Lists the AffiliateChannelCommissionCampaigns, searched and paginated")
//    @GetMapping
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public Page<AffiliateChannelCommissionCampaignDTO> search(AffiliateChannelCommissionCampaignBusiness.Filter request, Pageable pageable) {
//        return business.search(request, pageable);
//    }

//    @Operation(summary = "Update the AffiliateChannelCommissionCampaign", description = "Update the specific AffiliateChannelCommissionCampaign")
//    @PatchMapping(path = "/{id}")
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public AffiliateChannelCommissionCampaignDTO update(@PathVariable Long id, @RequestBody AffiliateChannelCommissionCampaignBusiness.Filter request) {
//        return business.update(id, request);
//    }

    @Operation(summary = "Get the AffiliateChannelCommissionCampaign", description = "Get the specific AffiliateChannelCommissionCampaign")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AffiliateChannelCommissionCampaignDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @Operation(summary = "Delete AffiliateChannelCommissionCampaign", description = "Delete the specific AffiliateChannelCommissionCampaign")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }

    @Operation(summary = "Lists the AffiliateChannelCommissionCampaigns", description = "Lists the AffiliateChannelCommissionCampaigns, searched and paginated")
    @GetMapping("/{id}/campaign")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<AffiliateChannelCommissionCampaignDTO> getByIdCampaign(@PathVariable Long id, Pageable pageable) {
        return business.searchByCampaignId(id, pageable);
    }

    /**
     * ============================================================================================================
     **/

}
