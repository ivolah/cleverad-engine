package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.CampaignBudgetBusiness;
import it.cleverad.engine.business.FileCampaignBudgetBusiness;
import it.cleverad.engine.web.dto.CampaignBudgetDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping(value = "/campaignbudget")
public class CampaignBudgetController {

    @Autowired
    private CampaignBudgetBusiness business;

    @Autowired
    private FileCampaignBudgetBusiness fileCampaignBudgetBusiness;

    /**
     * ============================================================================================================
     **/

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CampaignBudgetDTO create(@ModelAttribute CampaignBudgetBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<CampaignBudgetDTO> search(CampaignBudgetBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CampaignBudgetDTO update(@PathVariable Long id, @RequestBody CampaignBudgetBusiness.Update request) {
        return business.update(id, request);
    }

    @PatchMapping(path = "/recalculate/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void recalculate(@PathVariable Long id) {
        business.recalculate(id);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CampaignBudgetDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @GetMapping("/{id}/campaign")
    @ResponseStatus(HttpStatus.OK)
    public Page<CampaignBudgetDTO> getByIdCampaign(@PathVariable Long id, Pageable pageable) {
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

    @PostMapping("/upload/invoice")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Long uploadInvoice(@RequestParam("file") MultipartFile file, FileCampaignBudgetBusiness.BaseCreateRequest request) {
        return fileCampaignBudgetBusiness.storeFile(file, request, "INVOICE");
    }

    @PostMapping("/upload/order")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Long uploadOrder(@RequestParam("file") MultipartFile file, FileCampaignBudgetBusiness.BaseCreateRequest request) {
        return fileCampaignBudgetBusiness.storeFile(file, request, "ORDER");
    }

    @GetMapping("/{id}/invoice")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Resource> getInvoice(@PathVariable Long id) {
        return fileCampaignBudgetBusiness.downloadFile(id, "INVOICE");
    }

    @GetMapping("/{id}/order")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Resource> getOrder(@PathVariable Long id) {
        return fileCampaignBudgetBusiness.downloadFile(id, "ORDER");
    }

    @DeleteMapping("/{id}/invoice")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteIvoice(@PathVariable Long id) {
        this.fileCampaignBudgetBusiness.delete(id, "INVOICE");
    }

    @DeleteMapping("/{id}/order")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteOrder(@PathVariable Long id) {
        this.fileCampaignBudgetBusiness.delete(id, "ORDER");
    }

}