package it.cleverad.engine.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cleverad.engine.business.AdvertiserBusiness;
import it.cleverad.engine.web.dto.AdvertiserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Tag(name = "Advertisers", description = "Endpoints for all the Advertisers Operations")
@RestController
@RequestMapping(value = "/company")
public class AdvertiserController {

    @Autowired
    private AdvertiserBusiness business;

    /**
     * ============================================================================================================
     **/

    @Operation(summary = "Create Advertiser", description = "Creates a new Advertiser")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AdvertiserDTO create(@ModelAttribute AdvertiserBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

    @Operation(summary = "Lists the Affiliates", description = "Lists the Affiliates, searched and paginated")
    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<AdvertiserDTO> search(AdvertiserBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @Operation(summary = "Update the Advertiser", description = "Update the specific Advertiser")
    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AdvertiserDTO update(@PathVariable Long id, @RequestBody AdvertiserBusiness.Filter request) {
        return business.update(id, request);
    }

    @Operation(summary = "Get the Advertiser", description = "Get the specific Advertiser")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AdvertiserDTO getByUuid(@PathVariable Long id)  {
        return business.findById(id);
    }

    @Operation(summary = "Delete Advertiser", description = "Delete the specific Advertiser")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }

    /**
     * ============================================================================================================
     **/

}
