package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.AffiliateBusiness;
import it.cleverad.engine.business.CampaignBusiness;
import it.cleverad.engine.web.dto.AffiliateDTO;
import it.cleverad.engine.web.dto.CampaignBrandBuddiesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/brandbuddies")
public class BrandBuddiesController {

    @Autowired
    private AffiliateBusiness business;
    @Autowired
    private CampaignBusiness campaignBusiness;

    /**
     * ============================================================================================================
     **/

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AffiliateDTO create(@ModelAttribute AffiliateBusiness.BaseCreateRequest request) {
        request.setBrandbuddies(true);
        request.setChannelTypeId(30L);
        request.setChannelOwnerId(43L);
        request.setBusinessTypeId(53L);
        request.setChannelDimension("1");
        request.setChannelUrl("");
        request.setChannelName("Canale BrandBuddies " + request.getFirstName().toUpperCase() + " " + request.getLastName().toUpperCase());
        return business.create(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<AffiliateDTO> search(AffiliateBusiness.Filter request, @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
        request.setBrandbuddies(true);
        return business.search(request, pageable);
    }

    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AffiliateDTO update(@PathVariable Long id, @RequestBody AffiliateBusiness.Filter request) {
        request.setBrandbuddies(true);
        return business.update(id, request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AffiliateDTO getById(@PathVariable Long id) {
        return business.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }

    @GetMapping("/campaigns")
    public Page<CampaignBrandBuddiesDTO> getCampaignsBB(CampaignBusiness.Filter request, @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
        return campaignBusiness.getCampaignsActiveBrandBuddies(request, pageable);
    }


    /**
     * ============================================================================================================
     **/

}