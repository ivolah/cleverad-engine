package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.AffiliateChannelCommissionCampaignBusiness;
import it.cleverad.engine.web.dto.AffiliateChannelCommissionCampaignDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/affiliatechannelcommissioncampaign")
public class AffiliateChannelCommissionCampaignController {

    @Autowired
    private AffiliateChannelCommissionCampaignBusiness business;

    /**
     * ============================================================================================================
     **/

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AffiliateChannelCommissionCampaignDTO create(@ModelAttribute AffiliateChannelCommissionCampaignBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<AffiliateChannelCommissionCampaignDTO> search(AffiliateChannelCommissionCampaignBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AffiliateChannelCommissionCampaignDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }

    @GetMapping("/{id}/campaign")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<AffiliateChannelCommissionCampaignDTO> getByIdCampaign(@PathVariable Long id, Pageable pageable) {
        return business.searchByCampaignId(id, pageable);
    }

    @GetMapping("/{id}/campaign/affiliate")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<AffiliateChannelCommissionCampaignDTO> getByIdCampaignAffiliate(@PathVariable Long id, Pageable pageable) {
        return business.searchByCampaignIdAffiliateNotZero(id, pageable);
        //  return business.searchByCampaignIdAffiliateWithZero(id, pageable);
    }

    @GetMapping("/{id}/campaign/affiliate/not")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<AffiliateChannelCommissionCampaignDTO> searchByCampaignIdAffiliateNotZero(@PathVariable Long id, Pageable pageable) {
        return business.searchByCampaignIdAffiliateNotZero(id, pageable);
    }


    @GetMapping("/{id}/block")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AffiliateChannelCommissionCampaignDTO block(@PathVariable Long id) {
        return business.block(id);
    }

    @GetMapping("/{id}/unblock")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AffiliateChannelCommissionCampaignDTO unblock(@PathVariable Long id) {
        return business.unblock(id);
    }

    /**
     * ============================================================================================================
     **/

}