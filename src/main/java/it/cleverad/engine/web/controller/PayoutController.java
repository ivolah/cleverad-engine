package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.PayoutBusiness;
import it.cleverad.engine.business.TransactionStatusBusiness;
import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.persistence.model.service.Payout;
import it.cleverad.engine.web.dto.DictionaryDTO;
import it.cleverad.engine.web.dto.PayoutDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/payout")
public class PayoutController {

    @Autowired
    private PayoutBusiness business;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    /**
     * ============================================================================================================
     **/

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<Payout> create(@ModelAttribute PayoutBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

    @PostMapping(path = "/all")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<Payout> createAll(@RequestBody TransactionStatusBusiness.Filter request) {
        return business.createAll(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<PayoutDTO> search(PayoutBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PayoutDTO update(@PathVariable Long id, @RequestBody PayoutBusiness.Filter request) {
        return business.update(id, request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PayoutDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

//    @DeleteMapping("/{id}")
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public void deleteCpc(@PathVariable Long id) {
//        this.business.delete(id);
//    }

//    @DeleteMapping("/{payoutId}/transaction/{transactionId}/cpc")
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public PayoutDTO removeCpc(@PathVariable Long payoutId, @PathVariable Long transactionId) {
//        return this.business.removeCpc(payoutId, transactionId);
//    }

//    @DeleteMapping("/{payoutId}/transaction/{transactionId}/cpl")
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public PayoutDTO deleteCpl(@PathVariable Long payoutId, @PathVariable Long transactionId) {
//        return this.business.removeCpl(payoutId, transactionId);
//    }

    @GetMapping("/{id}/affiliate")
    @ResponseStatus(HttpStatus.OK)
    public Page<PayoutDTO> findByIdAffilaite(@PathVariable Long id, Pageable pageable) {
        return business.findByIdAffilaite(id, pageable);
    }

    @GetMapping("/affiliate")
    @ResponseStatus(HttpStatus.OK)
    public Page<PayoutDTO> findByAffilaite(Pageable pageable) {
        if (jwtUserDetailsService.isAdmin()) {
            return business.findByIdAffilaite(null, pageable);
        } else {
            return business.findByIdAffilaite(jwtUserDetailsService.getAffiliateId(), pageable);
        }
    }

    @GetMapping("/types")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<DictionaryDTO> getTypes() {
        return business.getTypes();
    }


    @PatchMapping(path = "/{id}/confermo")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PayoutDTO updateConfermato(@PathVariable Long id) {
        return business.updateStatus(id, 19L);
    }

    @PatchMapping(path = "/{id}/pagamento")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PayoutDTO updatePagamento(@PathVariable Long id) {
        return business.updateStatus(id, 21L);
    }

    @PatchMapping(path = "/{id}/concludo")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PayoutDTO updateConcluso(@PathVariable Long id) {
        return business.updateStatus(id, 22L);
    }

    @PatchMapping(path = "/{id}/rigetto")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PayoutDTO updateRigettato(@PathVariable Long id) {
        return business.updateStatus(id, 23L);
    }

    @PatchMapping(path = "/{id}/status/{statusId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PayoutDTO updateStatusPayout(@PathVariable Long id, @PathVariable Long statusId) {
        return business.updateStatus(id, statusId);
    }

    /**
     * ============================================================================================================
     **/

}