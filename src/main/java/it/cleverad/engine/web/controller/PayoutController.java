package it.cleverad.engine.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cleverad.engine.business.PayoutBusiness;
import it.cleverad.engine.service.JwtUserDetailsService;
import it.cleverad.engine.web.dto.PayoutDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Tag(name = "Payout", description = "Endpoints for all the Payout Operations")
@RestController
@RequestMapping(value = "/payout")
public class PayoutController {

    @Autowired
    private PayoutBusiness business;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    /**
     * ============================================================================================================
     **/

    @Operation(summary = "Create Payout", description = "Creates a new Payout")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PayoutDTO create(@ModelAttribute PayoutBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

    @Operation(summary = "Lists the Payouts", description = "Lists the Payouts, searched and paginated")
    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<PayoutDTO> search(PayoutBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @Operation(summary = "Update the Payout", description = "Update the specific Payout")
    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PayoutDTO update(@PathVariable Long id, @RequestBody PayoutBusiness.Filter request) {
        return business.update(id, request);
    }

    @Operation(summary = "Get the Payout", description = "Get the specific Payout")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PayoutDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @Operation(summary = "Delete Payout", description = "Delete the specific Payout")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }

    @Operation(summary = "Get the Payout", description = "Get the specific Payout")
    @GetMapping("/{id}/affiliate")
    @ResponseStatus(HttpStatus.OK)
    public Page<PayoutDTO> findByIdAffilaite(@PathVariable Long id) {
        return business.findByIdAffilaite(id);
    }

    @Operation(summary = "Get the Payout", description = "Get the specific Payout")
    @GetMapping("/affiliate")
    @ResponseStatus(HttpStatus.OK)
    public Page<PayoutDTO> findByAffilaite() {
        return business.findByIdAffilaite(jwtUserDetailsService.getAffiliateID());
    }

    /**
     * ============================================================================================================
     **/

}
