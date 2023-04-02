package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.TargetBusiness;
import it.cleverad.engine.business.TrackingBusiness;
import it.cleverad.engine.web.dto.TargetDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/target")
public class TargetController {

    @Autowired
    private TrackingBusiness trackingBusiness;
    @Autowired
    private TargetBusiness targetBusiness;

    /**
     * ============================================================================================================
     **/

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public TargetDTO getTarget(@ModelAttribute TrackingBusiness.BaseCreateRequest request) {
        return trackingBusiness.getTarget(request);
    }

    /**
     * ============================================================================================================
     **/

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = "/create")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public TargetDTO create(@ModelAttribute TargetBusiness.BaseCreateRequest request) {
        return targetBusiness.create(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<TargetDTO> search(TargetBusiness.Filter request, Pageable pageable) {
        return targetBusiness.search(request, pageable);
    }

    @GetMapping(path = "/{id}/media")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<TargetDTO> search(@PathVariable Long id, Pageable pageable) {
        return targetBusiness.getByMediaId(id, pageable);
    }

    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public TargetDTO update(@PathVariable Long id, @RequestBody TargetBusiness.Filter request) {
        return targetBusiness.update(id, request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TargetDTO getByUuid(@PathVariable Long id) {
        return targetBusiness.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.targetBusiness.delete(id);
    }




    /**
     * ============================================================================================================
     **/

}
