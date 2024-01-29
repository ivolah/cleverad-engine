package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.CampaignCostBusiness;
import it.cleverad.engine.business.FileCampaignBudgetBusiness;
import it.cleverad.engine.business.FileCostBusiness;
import it.cleverad.engine.web.dto.CampaignCostDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin
@RestController
@RequestMapping(value = "/campaigncost")
public class CampaignCostController {

    @Autowired
    private CampaignCostBusiness business;

    @Autowired
    private FileCostBusiness fileCostBusiness;

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

    @PostMapping("/upload/document")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Long uploadInvoice(@RequestParam("file") MultipartFile file, FileCostBusiness.BaseCreateRequest request) {
        return fileCostBusiness.storeFile(file, request);
    }

    @GetMapping("/{id}/document")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Resource> getInvoice(@PathVariable Long id) {
        return fileCostBusiness.downloadFile(id);
    }

    @DeleteMapping("/{id}/document")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteOrder(@PathVariable Long id) {
        this.fileCostBusiness.delete(id);
    }

}