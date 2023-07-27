package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.WalletBusiness;
import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.web.dto.WalletDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/wallet")
public class WalletController {

    @Autowired
    private WalletBusiness business;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    /**
     * ============================================================================================================
     **/

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public WalletDTO create(@ModelAttribute WalletBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<WalletDTO> search(WalletBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public WalletDTO update(@PathVariable Long id, @RequestBody WalletBusiness.Filter request) {
        return business.update(id, request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public WalletDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }

    @GetMapping("/{id}/affiliate")
    @ResponseStatus(HttpStatus.OK)
    public Page<WalletDTO> findByIdAffilaite(@PathVariable Long id) {
        return business.findByIdAffilaite(id);
    }

    @GetMapping("/affiliate")
    @ResponseStatus(HttpStatus.OK)
    public Page<WalletDTO> findByAffilaite() {
        return business.findByIdAffilaite(jwtUserDetailsService.getAffiliateID());
    }

    /**
     * ============================================================================================================
     **/

}
