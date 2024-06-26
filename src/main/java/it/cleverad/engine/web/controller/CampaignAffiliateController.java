package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.CampaignAffiliateBusiness;
import it.cleverad.engine.web.dto.CampaignAffiliateDTO;
import it.cleverad.engine.web.dto.DictionaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/campaignaffiliate")
public class CampaignAffiliateController {

    @Autowired
    private CampaignAffiliateBusiness business;

    /**
     * ============================================================================================================
     **/

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CampaignAffiliateDTO create(@ModelAttribute CampaignAffiliateBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<CampaignAffiliateDTO> search(CampaignAffiliateBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CampaignAffiliateDTO update(@PathVariable Long id, @RequestBody CampaignAffiliateBusiness.Filter request) {
        return business.update(id, request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CampaignAffiliateDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @GetMapping("/{id}/campaign")
    @ResponseStatus(HttpStatus.OK)
    public Page<CampaignAffiliateDTO> getByIdCampaign(@PathVariable Long id, Pageable pageable) {
        return business.searchByCampaignID(id, pageable);
    }

    @GetMapping("/{id}/campaign/filtered")
    @ResponseStatus(HttpStatus.OK)
    public Page<CampaignAffiliateDTO> getByIdCampaignFiltered(@PathVariable Long id, Pageable pageable) {
        return business.searchByCampaignIDAll(id, pageable);
    }

    @GetMapping("/{id}/campaign/brandbuddies")
    @ResponseStatus(HttpStatus.OK)
    public Page<CampaignAffiliateDTO> getByIdCampaignBrandBuddies(@PathVariable Long id, Pageable pageable) {
        return business.searchByCampaignIDBrandBuddies(id, pageable);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }

    @GetMapping("/types")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<DictionaryDTO> status() {
        return this.business.getTypes();
    }

    /**
     * ============================================================================================================
     **/

}