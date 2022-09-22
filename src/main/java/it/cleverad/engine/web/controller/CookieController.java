package it.cleverad.engine.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cleverad.engine.business.CookieBusiness;
import it.cleverad.engine.web.dto.CookieDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Tag(name = "Cookies", description = "Endpoints for all the Cookies Operations")
@RestController
@RequestMapping(value = "/cookie")
public class CookieController {

    @Autowired
    private CookieBusiness business;

    /**
     * ============================================================================================================
     **/

    @Operation(summary = "Create Cookie", description = "Creates a new Cookie")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CookieDTO create(@ModelAttribute CookieBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

    @Operation(summary = "Lists the Cookies", description = "Lists the Cookies, searched and paginated")
    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<CookieDTO> search(CookieBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @Operation(summary = "Update the Cookie", description = "Update the specific Cookie")
    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CookieDTO update(@PathVariable Long id, @RequestBody CookieBusiness.Filter request) {
        return business.update(id, request);
    }

    @Operation(summary = "Get the Cookie", description = "Get the specific Cookie")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CookieDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @Operation(summary = "Delete Cookie", description = "Delete the specific Cookie")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }

    /**
     * ============================================================================================================
     **/

}
