package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.*;
import it.cleverad.engine.web.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/transaction")
@Slf4j
public class TransactionController {

    @Autowired
    private TransactionBusiness business;
    @Autowired
    private TransazioniCPCBusiness transazioniCPCBusiness;
    @Autowired
    private TransazioniCPLBusiness transazioniCPLBusiness;

    @Autowired
    private TransactionAllBusiness allBusiness;
    @Autowired
    private TransactionStatusBusiness statusBusiness;

    /**
     * ============================================================================================================
     **/

    @GetMapping("/types")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<DictionaryDTO> status() {
        return this.business.getTypes();
    }

    @PostMapping(path = "/cpc", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public TransactionCPCDTO createcpc(@ModelAttribute TransactionBusiness.BaseCreateRequest request) {
        return business.createCpc(request);
    }

    @PostMapping(path = "/cpl", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public TransactionCPLDTO createcpl(@ModelAttribute TransactionBusiness.BaseCreateRequest request) {
        return business.createCpl(request);
    }

    @PostMapping(path = "/cpm", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public TransactionCPMDTO createcpm(@ModelAttribute TransactionBusiness.BaseCreateRequest request) {
        return business.createCpm(request);
    }

//    @Operation(summary = "Lists the Transactions", description = "Lists the Transactions, searched and paginated")
//    @GetMapping
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public Page<TransactionDTO> search(TransactionBusiness.Filter request, Pageable pageable) {
//        return business.search(request, pageable);
//    }

//    @Operation(summary = "Update the Transaction", description = "Update the specific Transaction")
//    @PatchMapping(path = "/{id}")
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public TransactionDTO update(@PathVariable Long id, @RequestBody TransactionBusiness.Filter request) {
//        return business.update(id, request);
//    }

    // GET BY ID

    @GetMapping("/{id}/cpc")
    @ResponseStatus(HttpStatus.OK)
    public TransactionCPCDTO findByIdCPC(@PathVariable Long id) {
        return business.findByIdCPC(id);
    }

    @GetMapping("/{id}/cpl")
    @ResponseStatus(HttpStatus.OK)
    public TransactionCPLDTO findByIdCPL(@PathVariable Long id) {
        return business.findByIdCPL(id);
    }

    @GetMapping("/{id}/cpm")
    @ResponseStatus(HttpStatus.OK)
    public TransactionCPMDTO findByIdCPM(@PathVariable Long id) {
        return business.findByIdCPM(id);
    }

    //DELETE

    @DeleteMapping("/{id}/cpc")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteCpc(@PathVariable Long id) {
        this.business.delete(id, "CPC");
    }

    @DeleteMapping("/{id}/cpl")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteCpl(@PathVariable Long id) {
        this.business.delete(id, "CPL");
    }

    @DeleteMapping("/{id}/cpm")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteCpm(@PathVariable Long id) {
        this.business.delete(id, "cpm");
    }

    //SEARCH BY CAPAOIGN ID

    @GetMapping("/{id}/campaign/cpc")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<TransactionCPCDTO> getbyCampaignCPC(@PathVariable Long id, Pageable pageable) {
        TransactionBusiness.Filter request = new TransactionBusiness.Filter();
        request.setCampaignId(id);
        return business.searchCpc(request, pageable);
    }

    @GetMapping("/{id}/campaign/cpl")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<TransactionCPLDTO> getbyCampaignCPL(@PathVariable Long id, Pageable pageable) {
        TransactionBusiness.Filter request = new TransactionBusiness.Filter();
        request.setCampaignId(id);
        return business.searchCpl(request, pageable);
    }

    @GetMapping("/{id}/campaign/cpm")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<TransactionCPMDTO> getbyCampaignCPM(@PathVariable Long id, Pageable pageable) {
        TransactionBusiness.Filter request = new TransactionBusiness.Filter();
        request.setCampaignId(id);
        return business.searchCpm(request, pageable);
    }

    //search by id affiliate x ADMIN
    @GetMapping("/{id}/affiliate/cpc")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<TransactionCPCDTO> getbyAffiliateCpc(@PathVariable Long id, Pageable pageable) {
        TransactionBusiness.Filter request = new TransactionBusiness.Filter();
        request.setAffiliateId(id);
        return business.searchByAffiliateCpc(request, id, pageable);
    }

    @GetMapping("/{id}/affiliate/cpl")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<TransactionCPLDTO> getbyAffiliateCpl(@PathVariable Long id, Pageable pageable) {
        TransactionBusiness.Filter request = new TransactionBusiness.Filter();
        request.setAffiliateId(id);
        return business.searchByAffiliateCpl(request, id, pageable);
    }

    @GetMapping("/{id}/affiliate/cpm")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<TransactionCPMDTO> getbyAffiliateCpm(@PathVariable Long id, Pageable pageable) {
        TransactionBusiness.Filter request = new TransactionBusiness.Filter();
        request.setAffiliateId(id);
        return business.searchByAffiliateCpm(request, id, pageable);
    }

    // SEARCH BY AFFILIATE
    @GetMapping("/affiliate/cpc")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<TransactionCPCDTO> searchByAffiliateCpc(TransactionBusiness.Filter request, Pageable pageable) {
        return business.searchByAffiliateCpc(request, null, pageable);
    }

    @GetMapping("/affiliate/cpm")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<TransactionCPMDTO> searchByAffiliateCpm(TransactionBusiness.Filter request, Pageable pageable) {
        return business.searchByAffiliateCpm(request, null, pageable);
    }

    @GetMapping("/affiliate/cpl")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<TransactionCPLDTO> searchByAffiliateCpl(TransactionBusiness.Filter request, Pageable pageable) {
        return business.searchByAffiliateCpl(request, null, pageable);
    }

    @GetMapping("/affiliate/cps")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<TransactionCPSDTO> searchByAffiliateCps(TransactionBusiness.Filter request, Pageable pageable) {
        return business.searchByAffiliateCps(request, null, pageable);
    }

    @PatchMapping("/update/status")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateStatus(@RequestBody TransactionBusiness.FilterUpdate request) {
        business.updateStatus(request.getId(), request.getDictionaryId(), request.getTipo(), request.getApproved(), request.getStatusId());
    }

    /**
     * ============================================================================================================
     **/

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<TransactionStatusDTO> searchAll(TransactionAllBusiness.Filter request, Pageable pageable) {
        return allBusiness.searchPrefiltrato(request, pageable);
    }

    @GetMapping("/all/affiliate")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<TransactionStatusDTO> searchAllPrefiltrato(TransactionAllBusiness.Filter request, Pageable pageable) {
        return allBusiness.searchPrefiltrato(request, pageable);
    }


    /**
     * ============================================================================================================
     **/

    @GetMapping("/status")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<TransactionStatusDTO> searchAll(TransactionStatusBusiness.Filter request, Pageable pageable) {
        return statusBusiness.searchPrefiltrato(request, pageable);
    }

    @GetMapping("/status/affiliate")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<TransactionStatusDTO> searchAllPrefiltrato(TransactionStatusBusiness.Filter request, Pageable pageable) {
        return statusBusiness.searchPrefiltrato(request, pageable);
    }


    /**
     * ============================================================================================================
     **/

    @PostMapping("/manage/cpl")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void manageCPL(@ModelAttribute TransazioniCPLBusiness.FilterUpdate request) {
        transazioniCPLBusiness.rigenera(Integer.parseInt(request.getYear()), Integer.parseInt(request.getMonth()), Integer.parseInt(request.getDay()));
    }

    @PostMapping("/manage/cpc")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void manageCPL(@ModelAttribute TransazioniCPCBusiness.FilterUpdate request) {
        transazioniCPCBusiness.rigenera(Integer.parseInt(request.getYear()), Integer.parseInt(request.getMonth()), Integer.parseInt(request.getDay()));
    }

}
