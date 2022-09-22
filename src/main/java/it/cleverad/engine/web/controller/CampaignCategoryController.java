package it.cleverad.engine.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cleverad.engine.business.CampaignCategoryBusiness;
import it.cleverad.engine.web.dto.CampaignCategoryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Tag(name = "CampaignCategorys", description = "Endpoints for all the CampaignCategorys Operations")
@RestController
@RequestMapping(value = "/campaigncategory")
public class CampaignCategoryController {

    @Autowired
    private CampaignCategoryBusiness business;

    /**
     * ============================================================================================================
     **/

    @Operation(summary = "Create CampaignCategory", description = "Creates a new CampaignCategory")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CampaignCategoryDTO create(@ModelAttribute CampaignCategoryBusiness.BaseCreateRequest request) {
        return   business.create(request);
    }

    @Operation(summary = "Lists the CampaignCategorys", description = "Lists the CampaignCategorys, searched and paginated")
    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<CampaignCategoryDTO> search(CampaignCategoryBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @Operation(summary = "Update the CampaignCategory", description = "Update the specific CampaignCategory")
    @PatchMapping(path = "/{id}" )
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CampaignCategoryDTO update(@PathVariable Long id, @RequestBody CampaignCategoryBusiness.Filter request) {
        return  business.update(id, request);
    }

    @Operation(summary = "Get the CampaignCategory", description = "Get the specific CampaignCategory")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CampaignCategoryDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @Operation(summary = "Delete CampaignCategory", description = "Delete the specific CampaignCategory")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }

    /**
     * ============================================================================================================
     **/

}
