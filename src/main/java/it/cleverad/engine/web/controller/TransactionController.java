package it.cleverad.engine.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cleverad.engine.business.TransactionBusiness;
import it.cleverad.engine.web.dto.TransactionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Tag(name = "Transaction", description = "Endpoints for all the Transactions Operations")
@RestController
@RequestMapping(value = "/transaction")
public class TransactionController {

    @Autowired
    private TransactionBusiness business;

    /**
     * ============================================================================================================
     **/

    @Operation(summary = "Create Transaction", description = "Creates a new Transaction")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public TransactionDTO create(@ModelAttribute TransactionBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

    @Operation(summary = "Lists the Transactions", description = "Lists the Transactions, searched and paginated")
    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<TransactionDTO> search(TransactionBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @Operation(summary = "Update the Transaction", description = "Update the specific Transaction")
    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public TransactionDTO update(@PathVariable Long id, @RequestBody TransactionBusiness.Filter request) {
        return business.update(id, request);
    }

    @Operation(summary = "Get the Transaction", description = "Get the specific Transaction")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TransactionDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @Operation(summary = "Delete Transaction", description = "Delete the specific Transaction")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }

    @GetMapping("/{id}/affiliate")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<TransactionDTO> getbyAffiliate(@PathVariable Long id, Pageable pageable) {
        TransactionBusiness.Filter request = new TransactionBusiness.Filter();
        request.setAffiliateId(id);
        return business.search(request, pageable);
    }

    @GetMapping("/{id}/campaign")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<TransactionDTO> getbyCampaign(@PathVariable Long id, Pageable pageable) {
        TransactionBusiness.Filter request = new TransactionBusiness.Filter();
        request.setCampaignId(id);
        return business.search(request, pageable);
    }


    /**
     * ============================================================================================================
     **/

}
