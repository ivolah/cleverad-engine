package it.cleverad.engine.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cleverad.engine.business.ChannelCategoryBusiness;
import it.cleverad.engine.web.dto.ChannelCategoryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Tag(name = "ChannelCategorys", description = "Endpoints for all the ChannelCategorys Operations")
@RestController
@RequestMapping(value = "/channelcategory")
public class ChannelCategoryController {

    @Autowired
    private ChannelCategoryBusiness business;

    /**
     * ============================================================================================================
     **/

    @Operation(summary = "Create ChannelCategory", description = "Creates a new ChannelCategory")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ChannelCategoryDTO create(@ModelAttribute ChannelCategoryBusiness.BaseCreateRequest request) {
        return   business.create(request);
    }

    @Operation(summary = "Lists the ChannelCategorys", description = "Lists the ChannelCategorys, searched and paginated")
    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<ChannelCategoryDTO> search(ChannelCategoryBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @Operation(summary = "Update the ChannelCategory", description = "Update the specific ChannelCategory")
    @PatchMapping(path = "/{id}" )
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ChannelCategoryDTO update(@PathVariable Long id, @RequestBody ChannelCategoryBusiness.Filter request) {
        return  business.update(id, request);
    }

    @Operation(summary = "Get the ChannelCategory", description = "Get the specific ChannelCategory")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ChannelCategoryDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @Operation(summary = "Delete ChannelCategory", description = "Delete the specific ChannelCategory")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }

    /**
     * ============================================================================================================
     **/

}
