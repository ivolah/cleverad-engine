package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.CampaignCostBusiness;
import it.cleverad.engine.web.dto.CampaignCostDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/campaigncost")
public class CampaignCostController {

    @Autowired
    private CampaignCostBusiness business;

    /**
     * ============================================================================================================
     **/

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CampaignCostDTO create(@ModelAttribute CampaignCostBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<CampaignCostDTO> search(CampaignCostBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CampaignCostDTO update(@PathVariable Long id, @RequestBody CampaignCostBusiness.BaseCreateRequest request) {
        return business.update(id, request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CampaignCostDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @GetMapping("/{id}/campaign")
    @ResponseStatus(HttpStatus.OK)
    public Page<CampaignCostDTO> getByIdCampaign(@PathVariable Long id, Pageable pageable) {
        return business.searchByCampaignID(id, pageable);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }


    /**
     * ============================================================================================================
     **/

}