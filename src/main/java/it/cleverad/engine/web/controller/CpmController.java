package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.CpmBusiness;
import it.cleverad.engine.web.dto.CpmDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/cpm")
public class CpmController {

    @Autowired
    private CpmBusiness business;

    /**
     * ============================================================================================================
     **/

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CpmDTO create(@ModelAttribute CpmBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<CpmDTO> search(CpmBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @GetMapping(path = "/refferal")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<CpmDTO> searchWithRefferal(CpmBusiness.Filter request, Pageable pageable) {
        return business.searchWithReferral(request, pageable);
    }

    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CpmDTO update(@PathVariable Long id, @RequestBody CpmBusiness.Filter request) {
        return business.update(id, request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CpmDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    /**
     * ============================================================================================================
     **/

}
