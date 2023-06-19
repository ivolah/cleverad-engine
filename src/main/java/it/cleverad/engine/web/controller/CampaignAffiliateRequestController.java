package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.CampaignAffiliateRequestBusiness;
import it.cleverad.engine.web.dto.CampaignAffiliateRequestDTO;
import it.cleverad.engine.web.dto.DictionaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/campaignaffiliaterequest")
public class CampaignAffiliateRequestController {

    @Autowired
    private CampaignAffiliateRequestBusiness business;

    /**
     * ============================================================================================================
     **/

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CampaignAffiliateRequestDTO create(@ModelAttribute CampaignAffiliateRequestBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<CampaignAffiliateRequestDTO> search(CampaignAffiliateRequestBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CampaignAffiliateRequestDTO update(@PathVariable Long id, @RequestBody CampaignAffiliateRequestBusiness.Filter request) {
        return business.update(id, request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CampaignAffiliateRequestDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @GetMapping("/{id}/campaign")
    @ResponseStatus(HttpStatus.OK)
    public Page<CampaignAffiliateRequestDTO> getByIdCampaign(@PathVariable Long id, Pageable pageable) {
        return business.searchByCampaignID(id, pageable);
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
