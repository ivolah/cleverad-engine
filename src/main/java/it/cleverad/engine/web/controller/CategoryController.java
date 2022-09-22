package it.cleverad.engine.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cleverad.engine.business.CategoryBusiness;
import it.cleverad.engine.web.dto.CategoryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Tag(name = "Categorys", description = "Endpoints for all the Categorys Operations")
@RestController
@RequestMapping(value = "/category")
public class CategoryController {

    @Autowired
    private CategoryBusiness business;

    /**
     * ============================================================================================================
     **/

    @Operation(summary = "Create Category", description = "Creates a new Category")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CategoryDTO create(@ModelAttribute CategoryBusiness.BaseCreateRequest request) {
        return   business.create(request);
    }

    @Operation(summary = "Lists the Categorys", description = "Lists the Categorys, searched and paginated")
    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<CategoryDTO> search(CategoryBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @Operation(summary = "Update the Category", description = "Update the specific Category")
    @PatchMapping(path = "/{id}" )
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CategoryDTO update(@PathVariable Long id, @RequestBody CategoryBusiness.Filter request) {
        return  business.update(id, request);
    }

    @Operation(summary = "Get the Category", description = "Get the specific Category")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @Operation(summary = "Delete Category", description = "Delete the specific Category")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }

    /**
     * ============================================================================================================
     **/

}
