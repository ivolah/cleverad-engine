package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.*;
import it.cleverad.engine.persistence.model.service.QueryTransaction;
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
    private TransactionAllBusiness allBusiness;
    @Autowired
    private TransactionStatusBusiness statusBusiness;
    @Autowired
    private TransactionCPLBusiness transactionCPLBusiness;
    @Autowired
    private TransactionCPCBusiness transactionCPCBusiness;
    @Autowired
    private TransactionCPMBusiness transactionCPMBusiness;
    @Autowired
    private TransactionCPSBusiness transactionCPSBusiness;

    /**
     * ============================================================================================================
     **/

    @GetMapping("/types")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<DictionaryDTO> status() {
        return this.transactionCPLBusiness.getTypes();
    }

    @PostMapping(path = "/cpc", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public TransactionCPCDTO createcpc(@ModelAttribute TransactionCPCBusiness.BaseCreateRequest request) {
        return transactionCPCBusiness.createCpc(request);
    }

    @PostMapping(path = "/cpl", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public TransactionCPLDTO createcpl(@ModelAttribute TransactionCPLBusiness.BaseCreateRequest request) {
        return transactionCPLBusiness.createCpl(request);
    }

    @PostMapping(path = "/cpm", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public TransactionCPMDTO createcpm(@ModelAttribute TransactionCPMBusiness.BaseCreateRequest request) {
        return transactionCPMBusiness.createCpm(request);
    }

    // GET BY ID

    @GetMapping("/{id}/cpc")
    @ResponseStatus(HttpStatus.OK)
    public TransactionCPCDTO findByIdCPC(@PathVariable Long id) {
        return transactionCPCBusiness.findByIdCPC(id);
    }

    @GetMapping("/{id}/cpl")
    @ResponseStatus(HttpStatus.OK)
    public TransactionCPLDTO findByIdCPL(@PathVariable Long id) {
        return transactionCPLBusiness.findByIdCPL(id);
    }

    @GetMapping("/{id}/cpm")
    @ResponseStatus(HttpStatus.OK)
    public TransactionCPMDTO findByIdCPM(@PathVariable Long id) {
        return transactionCPMBusiness.findByIdCPM(id);
    }

    //DELETE

    @DeleteMapping("/{id}/cpc")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteCpc(@PathVariable Long id) {
        this.transactionCPCBusiness.delete(id);
    }

    @DeleteMapping("/{id}/cpl")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteCpl(@PathVariable Long id) {
        this.transactionCPLBusiness.delete(id);
    }

    @DeleteMapping("/{id}/cpm")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteCpm(@PathVariable Long id) {
        this.transactionCPMBusiness.delete(id);
    }

    //SEARCH BY CAPAOIGN ID

    @GetMapping("/{id}/campaign/cpc")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<TransactionCPCDTO> getbyCampaignCPC(@PathVariable Long id, Pageable pageable) {
        TransactionCPCBusiness.Filter request = new TransactionCPCBusiness.Filter();
        request.setCampaignId(id);
        return transactionCPCBusiness.searchCpc(request, pageable);
    }

    @GetMapping("/cpc/list")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<TransactionCPCDTO> getbyCampaignCPCS(TransactionCPCBusiness.Filter request, Pageable pageable) {
        return transactionCPCBusiness.searchCpc(request, pageable);
    }

    @GetMapping("/{id}/campaign/cpl")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<TransactionCPLDTO> getbyCampaignCPL(@PathVariable Long id, Pageable pageable) {
        return transactionCPLBusiness.searchByCampaign(id, pageable);
    }

    @GetMapping("/{id}/campaign/cpm")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<TransactionCPMDTO> getbyCampaignCPM(@PathVariable Long id, Pageable pageable) {
        TransactionCPMBusiness.Filter request = new TransactionCPMBusiness.Filter();
        request.setCampaignId(id);
        return transactionCPMBusiness.searchCpm(request, pageable);
    }

    //search by id affiliate x ADMIN
    @GetMapping("/{id}/affiliate/cpc")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<TransactionCPCDTO> getbyAffiliateCpc(@PathVariable Long id, Pageable pageable) {
        TransactionCPCBusiness.Filter request = new TransactionCPCBusiness.Filter();
        request.setAffiliateId(id);
        return transactionCPCBusiness.searchByAffiliateCpc(request, id, pageable);
    }

    /**
     * @GetMapping("/{id}/affiliate/cpl")
     * @ResponseStatus(HttpStatus.ACCEPTED) public Page<TransactionCPLDTO> getbyAffiliateCpl(@PathVariable Long id, Pageable pageable) {
     * TransactionCPLBusiness.Filter request = new TransactionCPLBusiness.Filter();
     * request.setAffiliateId(id);
     * return transactionCPLBusiness.searchByAffiliateCpl(request, id, pageable);
     * }
     **/

    @GetMapping("/{id}/affiliate/cpm")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<TransactionCPMDTO> getbyAffiliateCpm(@PathVariable Long id, Pageable pageable) {
        TransactionCPMBusiness.Filter request = new TransactionCPMBusiness.Filter();
        request.setAffiliateId(id);
        return transactionCPMBusiness.searchByAffiliateCpm(request, id, pageable);
    }

    // SEARCH BY AFFILIATE
    @GetMapping("/affiliate/cpc")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<TransactionCPCDTO> searchByAffiliateCpc(TransactionCPCBusiness.Filter request, Pageable pageable) {
        return transactionCPCBusiness.searchByAffiliateCpc(request, null, pageable);
    }

    @GetMapping("/affiliate/cpm")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<TransactionCPMDTO> searchByAffiliateCpm(TransactionCPMBusiness.Filter request, Pageable pageable) {
        return transactionCPMBusiness.searchByAffiliateCpm(request, null, pageable);
    }

    @GetMapping("/affiliate/cpl")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<TransactionCPLDTO> searchByAffiliateCpl(TransactionCPLBusiness.Filter request, Pageable pageable) {
        return transactionCPLBusiness.searchByAffiliateCpl(request, null, pageable);
    }

    @GetMapping("/affiliate/cps")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<TransactionCPSDTO> searchByAffiliateCps(TransactionCPSBusiness.Filter request, Pageable pageable) {
        return transactionCPSBusiness.searchByAffiliateCps(request, null, pageable);
    }

    @PatchMapping("/update/status")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateStatus(@RequestBody TransactionCPCBusiness.FilterUpdate request) {
        switch (request.getTipo()) {
            case "CPC":
                transactionCPCBusiness.updateStatus(request.getId(), request.getDictionaryId(), request.getApproved(), request.getStatusId());
                break;
            case "CPL":
                transactionCPLBusiness.updateStatus(request.getId(), request.getDictionaryId(), request.getApproved(), request.getStatusId());
                break;
            case "CPM":
                transactionCPMBusiness.updateStatus(request.getId(), request.getDictionaryId(), request.getApproved(), request.getStatusId());
                break;
            case "CPS":
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + request.getTipo());
        }
    }

    /**
     * ============================================================================================================
     **/

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<QueryTransaction> searchAll(TransactionStatusBusiness.QueryFilter request, Pageable pageable) {
        return statusBusiness.searchPrefiltratoN(request, pageable);
    }

    @GetMapping("/all/affiliate")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<QueryTransaction> searchAllPrefiltrato(TransactionStatusBusiness.QueryFilter request, Pageable pageable) {
        return statusBusiness.searchPrefiltratoN(request, pageable);
    }

    /**
     * ============================================================================================================
     **/

    @GetMapping("/status")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<QueryTransaction> searchStatus(TransactionStatusBusiness.QueryFilter request, Pageable pageable) {
        return statusBusiness.searchPrefiltratoN(request, pageable);
    }

    @GetMapping("/status/affiliate")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<QueryTransaction> searchStatusPrefiltrato(TransactionStatusBusiness.QueryFilter request, Pageable pageable) {
        return statusBusiness.searchPrefiltratoN(request, pageable);
    }

    /**
     * ============================================================================================================
     **/

}