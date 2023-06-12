package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.RepresentativeBusiness;
import it.cleverad.engine.service.JwtUserDetailsService;
import it.cleverad.engine.web.dto.RepresentativeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/representative")
public class RepresentativeController {

    @Autowired
    private RepresentativeBusiness business;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    /**
     * ============================================================================================================
     **/

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = "/affiliate")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public RepresentativeDTO createaff(@ModelAttribute RepresentativeBusiness.BaseCreateRequest request) {
        return business.create(request, "AFFILIATE");
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = "/advertiser")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public RepresentativeDTO createadv(@ModelAttribute RepresentativeBusiness.BaseCreateRequest request) {
        return business.create(request, "ADVERTISER");
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<RepresentativeDTO> search(RepresentativeBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @PatchMapping(path = "/{id}/affiliate")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public RepresentativeDTO updateAFF(@PathVariable Long id, @RequestBody RepresentativeBusiness.Filter request) {
        return business.update(id, request, "AFFILIATE");
    }

    @PatchMapping(path = "/{id}/advertiser")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public RepresentativeDTO updateADV(@PathVariable Long id, @RequestBody RepresentativeBusiness.Filter request) {
        return business.update(id, request, "ADVERTISER");
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RepresentativeDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }

    @DeleteMapping("/{id}/advertiser")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteDaAdvertiser(@PathVariable Long id) {
        this.business.delete(id);
    }

    @GetMapping("/{id}/affiliate")
    @ResponseStatus(HttpStatus.OK)
    public Page<RepresentativeDTO> findByIdAffilaite(@PathVariable Long id) {
        return business.findByIdAffilaite(id);
    }

    @GetMapping("/affiliate")
    @ResponseStatus(HttpStatus.OK)
    public Page<RepresentativeDTO> findByAffilaite() {
        return business.findByIdAffilaite(jwtUserDetailsService.getAffiliateID());
    }


    @GetMapping("/{id}/advertiser")
    @ResponseStatus(HttpStatus.OK)
    public Page<RepresentativeDTO> findByIdAdvertiser(@PathVariable Long id) {
        return business.findByIdAdvertiser(id);
    }


    /**
     * ============================================================================================================
     **/

}
