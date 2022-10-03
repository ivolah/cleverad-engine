package it.cleverad.engine.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cleverad.engine.business.RevenueFactorBusiness;
import it.cleverad.engine.web.dto.DictionaryDTO;
import it.cleverad.engine.web.dto.RevenueFactorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Tag(name = "RevenueFactor", description = "Endpoints for all the RevenueFactor Operations")
@RestController
@RequestMapping(value = "/revenuefactor")
public class RevenueFactorController {

    @Autowired
    private RevenueFactorBusiness business;

    /**
     * ============================================================================================================
     **/

    @Operation(summary = "Create RevenueFactor", description = "Creates a new RevenueFactor")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public RevenueFactorDTO create(@ModelAttribute RevenueFactorBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

    @Operation(summary = "Lists the RevenueFactor", description = "Lists the RevenueFactor, searched and paginated")
    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<RevenueFactorDTO> search(RevenueFactorBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @Operation(summary = "Update the RevenueFactor", description = "Update the specific RevenueFactor")
    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public RevenueFactorDTO update(@PathVariable Long id, @RequestBody RevenueFactorBusiness.Filter request) {
        return business.update(id, request);
    }

    @Operation(summary = "Get the RevenueFactor", description = "Get the specific RevenueFactor")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RevenueFactorDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @Operation(summary = "Delete RevenueFactor", description = "Delete the specific RevenueFactor")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }

    @GetMapping("/{id}/campaign")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<RevenueFactorDTO> getbyIdCampaign(@PathVariable Long id, Pageable pageable) {
        return business.getbyIdCampaign(id, pageable);
    }

    @GetMapping("/types")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<DictionaryDTO> getTypes() {
        return business.getTypes();
    }

    /**
     * ============================================================================================================
     **/

}
