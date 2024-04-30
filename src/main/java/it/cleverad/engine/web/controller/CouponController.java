package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.CouponBusiness;
import it.cleverad.engine.web.dto.CouponDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/coupon")
public class CouponController {

    @Autowired
    private CouponBusiness business;

    /**
     * ============================================================================================================
     **/

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CouponDTO create(@ModelAttribute CouponBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<CouponDTO> search(CouponBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CouponDTO update(@PathVariable Long id, @RequestBody CouponBusiness.Filter request) {
        return business.update(id, request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CouponDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }

    /**
     * ============================================================================================================
     **/

}