package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.AffiliateBusiness;
import it.cleverad.engine.business.DictionaryBusiness;
import it.cleverad.engine.web.dto.AffiliateDTO;
import it.cleverad.engine.web.dto.DictionaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/affiliate")
public class AffiliateController {

    @Autowired
    private AffiliateBusiness business;
    @Autowired
    private DictionaryBusiness dictionaryBusiness;

    /**
     * ============================================================================================================
     **/

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AffiliateDTO create(@ModelAttribute AffiliateBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<AffiliateDTO> search(AffiliateBusiness.Filter request, @PageableDefault(value = 500) Pageable pageable) {
        return business.search(request, pageable);
    }

    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AffiliateDTO update(@PathVariable Long id, @RequestBody AffiliateBusiness.Filter request) {
        return business.update(id, request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AffiliateDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }

    @GetMapping("/types/company")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<DictionaryDTO> getTypeCompany() {
        return business.getTypeCompany();
    }

    @GetMapping("/types/channel")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<DictionaryDTO> getChannelTypeAffiliate() {
        return business.getChannelTypeAffiliate();
    }

    @GetMapping("/approve")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<AffiliateDTO> daApprovare(Pageable pageable) {
        AffiliateBusiness.Filter request = new AffiliateBusiness.Filter();
        request.setStatus(true);
        return business.search(request, pageable);
    }

    @GetMapping("/types/status")
    public Page<DictionaryDTO> getTypes() {
        return dictionaryBusiness.getAffiliateStatusTypes();
    }

    /**
     * ============================================================================================================
     **/

}
