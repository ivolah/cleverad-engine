package it.cleverad.engine.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cleverad.engine.business.CommissionBusiness;
import it.cleverad.engine.web.dto.CommissionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Tag(name = "Commissions", description = "Endpoints for all the Commission Operations")
@RestController
@RequestMapping(value = "/commission")
public class CommissionController {

    @Autowired
    private CommissionBusiness business;

    /**
     * ============================================================================================================
     **/

    @Operation(summary = "Create Commission", description = "Creates a new Commission")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CommissionDTO create(@ModelAttribute CommissionBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

    @Operation(summary = "Lists the Commissions", description = "Lists the Commissions, searched and paginated")
    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<CommissionDTO> search(CommissionBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @Operation(summary = "Update the Commission", description = "Update the specific Commission")
    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CommissionDTO update(@PathVariable Long id, @RequestBody CommissionBusiness.Filter request) {
        return business.update(id, request);
    }

    @Operation(summary = "Get the Commission", description = "Get the specific Commission")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CommissionDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @Operation(summary = "Delete Commission", description = "Delete the specific Commission")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }

    /**
     * ============================================================================================================
     **/

}
