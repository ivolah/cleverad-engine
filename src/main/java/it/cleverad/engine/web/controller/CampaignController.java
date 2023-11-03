package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.CampaignBusiness;
import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.web.dto.CampaignBrandBuddiesDTO;
import it.cleverad.engine.web.dto.CampaignDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/campaign")
public class CampaignController {

    @Autowired
    private CampaignBusiness business;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    /**
     * ============================================================================================================
     **/

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CampaignDTO create(@ModelAttribute CampaignBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<CampaignDTO> search(CampaignBusiness.Filter request, @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
        return business.search(request, pageable);
    }

    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CampaignDTO update(@PathVariable Long id, @RequestBody CampaignBusiness.Filter request) {
        return business.update(id, request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CampaignDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }

    @GetMapping("/{affiliateId}/affiliate")
    public Page<CampaignDTO> getCampaigns(@PathVariable Long affiliateId) {
        return business.getCampaigns(affiliateId);
    }

    @GetMapping("/affiliate")
    public Page<CampaignDTO> getCampaigns(@PageableDefault(value = Integer.MAX_VALUE)Pageable pageable) {
        return business.getCampaignsActive(jwtUserDetailsService.getAffiliateID(), pageable);
    }

    @GetMapping("/affiliate/not")
    public Page<CampaignDTO> getCampaignsNot( @PageableDefault(value = Integer.MAX_VALUE)Pageable pageable) {
        return business.getCampaignsNot(jwtUserDetailsService.getAffiliateID(), pageable);
    }

    @GetMapping("/brandbuddies")
    public Page<CampaignBrandBuddiesDTO> getCampaignsBB(CampaignBusiness.Filter request, @PageableDefault(value = Integer.MAX_VALUE)Pageable pageable) {
        return business.getCampaignsActiveBrandBuddies(request,pageable);
    }

//    @GetMapping("/affiliate")
//    public Page<CampaignDTO> getCampaignAffilaite(Pageable pageable) {
//        return business.getCampaignsGuest(pageable);
//    }

    /**
     * ============================================================================================================
     **/

}