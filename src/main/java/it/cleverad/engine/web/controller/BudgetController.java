package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.BudgetBusiness;
import it.cleverad.engine.web.dto.BudgetDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/budget")
public class BudgetController {

    @Autowired
    private BudgetBusiness business;

    /**
     * ============================================================================================================
     **/

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public BudgetDTO create(@ModelAttribute BudgetBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<BudgetDTO> search(BudgetBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public BudgetDTO update(@PathVariable Long id, @RequestBody BudgetBusiness.Filter request) {
        return business.update(id, request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BudgetDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }


    @GetMapping("/{id}/campaign")
    @ResponseStatus(HttpStatus.OK)
    public Page<BudgetDTO> getByIdCampaign(@PathVariable Long id) {
        return business.getByIdCampaign(id);
    }
    /**
     * ============================================================================================================
     **/

}
