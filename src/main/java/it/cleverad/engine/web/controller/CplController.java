package it.cleverad.engine.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cleverad.engine.business.CplBusiness;
import it.cleverad.engine.web.dto.CplDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Tag(name = "Cpl", description = "Endpoints for all the Cpls Operations")
@RestController
@RequestMapping(value = "/cpl")
public class CplController {

    @Autowired
    private CplBusiness business;

    /**
     * ============================================================================================================
     **/

    @Operation(summary = "Create Cpl", description = "Creates a new Cpl")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CplDTO create(@ModelAttribute CplBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

    @Operation(summary = "Lists the Cpls", description = "Lists the Cpls, searched and paginated")
    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<CplDTO> search(CplBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @Operation(summary = "Update the Cpl", description = "Update the specific Cpl")
    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CplDTO update(@PathVariable Long id, @RequestBody CplBusiness.Filter request) {
        return business.update(id, request);
    }

    @Operation(summary = "Get the Cpl", description = "Get the specific Cpl")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CplDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @Operation(summary = "Delete Cpl", description = "Delete the specific Cpl")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }

    /**
     * ============================================================================================================
     **/

}
