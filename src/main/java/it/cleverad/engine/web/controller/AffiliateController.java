package it.cleverad.engine.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cleverad.engine.business.AffiliateBusiness;
import it.cleverad.engine.web.dto.AffiliateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Tag(name = "Affilaites", description = "Endpoints for all the Affilaites Operations")
@RestController
@RequestMapping(value = "/affiliate")
public class AffiliateController {

    @Autowired
    private AffiliateBusiness business;

    /**
     * ============================================================================================================
     **/

    @Operation(summary = "Create Affiliate", description = "Creates a new Affiliate")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AffiliateDTO create(@ModelAttribute AffiliateBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

    @Operation(summary = "Lists the Affiliates", description = "Lists the Affiliates, searched and paginated")
    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<AffiliateDTO> search(AffiliateBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @Operation(summary = "Update the Affiliate", description = "Update the specific Affiliate")
    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AffiliateDTO update(@PathVariable Long id, @RequestBody AffiliateBusiness.Filter request) {
        return business.update(id, request);
    }

    @Operation(summary = "Get the Affiliate", description = "Get the specific Affiliate")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AffiliateDTO getByUuid(@PathVariable Long id)  {
        return business.findById(id);
    }

    @Operation(summary = "Delete Affiliate", description = "Delete the specific Affiliate")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }

    /**
     * ============================================================================================================
     **/

}
