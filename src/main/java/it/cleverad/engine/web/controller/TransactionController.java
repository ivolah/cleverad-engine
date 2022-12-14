package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.TransactionAllBusiness;
import it.cleverad.engine.business.TransactionBusiness;
import it.cleverad.engine.web.dto.TransactionAllDTO;
import it.cleverad.engine.web.dto.TransactionCPCDTO;
import it.cleverad.engine.web.dto.TransactionCPLDTO;
import it.cleverad.engine.web.dto.TransactionCPMDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/transaction")
public class TransactionController {

    @Autowired
    private TransactionBusiness business;

    @Autowired
    private TransactionAllBusiness allBusiness;

    /**
     * ============================================================================================================
     **/

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

    /**
     * ============================================================================================================
     **/

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<TransactionAllDTO> searchAll(TransactionAllBusiness.Filter request, Pageable pageable) {
        return allBusiness.search(request,  pageable);
    }



}
