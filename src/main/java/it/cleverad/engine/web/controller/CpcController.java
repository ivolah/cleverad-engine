package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.CpcBusiness;
import it.cleverad.engine.persistence.model.tracking.Cpc;
import it.cleverad.engine.web.dto.tracking.CpcDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/cpc")
public class CpcController {

    @Autowired
    private CpcBusiness business;

    /**
     * ============================================================================================================
     **/

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CpcDTO create(@ModelAttribute CpcBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<CpcDTO> search(CpcBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @GetMapping(path = "/refferal")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<CpcDTO> searchWithRefferal(CpcBusiness.Filter request, Pageable pageable) {
        return business.searchWithReferral(request, pageable);
    }

    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CpcDTO update(@PathVariable Long id, @RequestBody CpcBusiness.Filter request) {
        return business.update(id, request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CpcDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }


    @GetMapping(path = "/rndtrs/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<Cpc> findByRndTrs(@PathVariable Long id) {
        return business.findByRndTrs(id);
    }
    
    /**
     * ============================================================================================================
     **/

}