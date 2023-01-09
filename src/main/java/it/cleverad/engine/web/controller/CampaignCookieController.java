package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.CampaignCookieBusiness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping(value = "/campaigncookie")
public class CampaignCookieController {

    @Autowired
    private CampaignCookieBusiness business;

    /**
     * ============================================================================================================
     **/

//    @Operation(summary = "Create CampaignCookie", description = "Creates a new CampaignCookie")
//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public CampaignCookieDTO create(@ModelAttribute CampaignCookieBusiness.BaseCreateRequest request) {
//        return   business.create(request);
//    }
//
//    @Operation(summary = "Lists the CampaignCookies", description = "Lists the CampaignCookies, searched and paginated")
//    @GetMapping
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public Page<CampaignCookieDTO> search(CampaignCookieBusiness.Filter request, Pageable pageable) {
//        return business.search(request, pageable);
//    }
//
//    @Operation(summary = "Update the CampaignCookie", description = "Update the specific CampaignCookie")
//    @PatchMapping(path = "/{id}" )
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public CampaignCookieDTO update(@PathVariable Long id, @RequestBody CampaignCookieBusiness.Filter request) {
//        return  business.update(id, request);
//    }
//
//    @Operation(summary = "Get the CampaignCookie", description = "Get the specific CampaignCookie")
//    @GetMapping("/{id}")
//    @ResponseStatus(HttpStatus.OK)
//    public CampaignCookieDTO getByUuid(@PathVariable Long id) {
//        return business.findById(id);
//    }
//
//    @Operation(summary = "Delete CampaignCookie", description = "Delete the specific CampaignCookie")
//    @DeleteMapping("/{id}")
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public void delete(@PathVariable Long id) {
//        this.business.delete(id);
//    }

    /**
     * ============================================================================================================
     **/

}
