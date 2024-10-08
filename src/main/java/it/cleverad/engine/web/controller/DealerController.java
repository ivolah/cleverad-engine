package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.DealerBusiness;
import it.cleverad.engine.web.dto.DealerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/dealer")
public class DealerController {

    @Autowired
    private DealerBusiness business;

    /**
     * ============================================================================================================
     **/

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public DealerDTO create(@ModelAttribute DealerBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<DealerDTO> search(DealerBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public DealerDTO update(@PathVariable Long id, @RequestBody DealerBusiness.Filter request) {
        return business.update(id, request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DealerDTO getByUuid(@PathVariable Long id) {
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