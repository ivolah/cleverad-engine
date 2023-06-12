package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.WalletTransactionBusiness;
import it.cleverad.engine.web.dto.WalletTransactionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/wallettransaction")
public class WalletTransactionController {

    @Autowired
    private WalletTransactionBusiness business;


    /**
     * ============================================================================================================
     **/

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public WalletTransactionDTO create(@ModelAttribute WalletTransactionBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<WalletTransactionDTO> search(WalletTransactionBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public WalletTransactionDTO update(@PathVariable Long id, @RequestBody WalletTransactionBusiness.Filter request) {
        return business.update(id, request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public WalletTransactionDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }

    @GetMapping("/{id}/affiliate")
    @ResponseStatus(HttpStatus.OK)
    public Page<WalletTransactionDTO> findByIdAffilaite(@PathVariable Long id) {
        return business.findByIdAffilaite(id);
    }

    @GetMapping("/affiliate")
    @ResponseStatus(HttpStatus.OK)
    public Page<WalletTransactionDTO> findByAffilaite() {
        return business.findByIdAffilaite(null);
    }

    /**
     * ============================================================================================================
     **/

}
