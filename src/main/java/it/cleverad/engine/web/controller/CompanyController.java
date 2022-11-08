package it.cleverad.engine.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cleverad.engine.business.CompanyBusiness;
import it.cleverad.engine.web.dto.CompanyDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Tag(name = "Companys", description = "Endpoints for all the Companys Operations")
@RestController
@RequestMapping(value = "/company")
public class CompanyController {

    @Autowired
    private CompanyBusiness business;

    /**
     * ============================================================================================================
     **/

    @Operation(summary = "Create Company", description = "Creates a new Company")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CompanyDTO create(@ModelAttribute CompanyBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

    @Operation(summary = "Lists the Affiliates", description = "Lists the Affiliates, searched and paginated")
    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<CompanyDTO> search(CompanyBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @Operation(summary = "Update the Company", description = "Update the specific Company")
    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CompanyDTO update(@PathVariable Long id, @RequestBody CompanyBusiness.Filter request) {
        return business.update(id, request);
    }

    @Operation(summary = "Get the Company", description = "Get the specific Company")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CompanyDTO getByUuid(@PathVariable Long id)  {
        return business.findById(id);
    }

    @Operation(summary = "Delete Company", description = "Delete the specific Company")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }

    /**
     * ============================================================================================================
     **/

}
