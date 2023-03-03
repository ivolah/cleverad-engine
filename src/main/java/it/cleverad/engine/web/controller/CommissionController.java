package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.CommissionBusiness;
import it.cleverad.engine.web.dto.CommissionDTO;
import it.cleverad.engine.web.dto.DictionaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/commission")
public class CommissionController {

    @Autowired
    private CommissionBusiness business;

    /**
     * ============================================================================================================
     **/

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CommissionDTO create(@ModelAttribute CommissionBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<CommissionDTO> search(CommissionBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CommissionDTO update(@PathVariable Long id, @RequestBody CommissionBusiness.Filter request) {
        return business.update(id, request);
    }

    @PatchMapping(path = "/{id}/disable")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CommissionDTO disable(@PathVariable Long id) {
        return business.disable(id);
    }

    @PatchMapping(path = "/{id}/enable")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CommissionDTO enable(@PathVariable Long id) {
        return business.enable(id);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CommissionDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @DeleteMapping("/{commissionId}/campaign/{campaignId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long campaignId, @PathVariable Long commissionId ) {
        this.business.delete(campaignId, commissionId);
    }

    @GetMapping("/types")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<DictionaryDTO> getTypes() {
        return business.getTypes();
    }

    @GetMapping("/{id}/campaign")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<CommissionDTO> getByIdCampaign(@PathVariable Long id) {
        return business.getByIdCampaign(id);
    }

    @GetMapping("/{id}/campaign/active")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<CommissionDTO> getByIdCampaignAttive(@PathVariable Long id) {
        return business.getByIdCampaignAttive(id);
    }

    /**
     * ============================================================================================================
     **/

}
