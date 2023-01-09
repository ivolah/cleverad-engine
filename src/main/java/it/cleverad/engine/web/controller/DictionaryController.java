package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.DictionaryBusiness;
import it.cleverad.engine.web.dto.DictionaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/dictionary")
public class DictionaryController {

    @Autowired
    private DictionaryBusiness business;

    /**
     * ============================================================================================================
     **/

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public DictionaryDTO create(@ModelAttribute DictionaryBusiness.BaseCreateRequest request) {
        return  business.create(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<DictionaryDTO> search(DictionaryBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @PatchMapping(path = "/{id}" )
    @ResponseStatus(HttpStatus.ACCEPTED)
    public DictionaryDTO update(@PathVariable Long id, @RequestBody DictionaryBusiness.Filter request) {
        return business.update(id, request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DictionaryDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }

    @GetMapping("/role")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<DictionaryDTO> role(DictionaryBusiness.Filter request, Pageable pageable) {
        return business.getTypeRole(request, pageable);
    }

    @GetMapping("/status")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<DictionaryDTO> status(DictionaryBusiness.Filter request, Pageable pageable) {
        return business.getTypeStatus(request, pageable);
    }


    /**
     * ============================================================================================================
     **/

}
