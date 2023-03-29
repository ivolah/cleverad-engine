package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.ReportBusiness;
import it.cleverad.engine.persistence.model.service.TopCampaings;
import it.cleverad.engine.web.dto.ReportDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/report")
public class ReportController {

    @Autowired
    private ReportBusiness reportBusiness;

    /**
     * ============================================================================================================
     **/

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ReportDTO create(@ModelAttribute ReportBusiness.BaseCreateRequest request) {
        return reportBusiness.create(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<ReportDTO> search(ReportBusiness.Filter request, Pageable pageable) {
        return reportBusiness.search(request, pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ReportDTO getByUuid(@PathVariable Long id) {
        return reportBusiness.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.reportBusiness.delete(id);
    }

    @GetMapping(path = "/campagne/top")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<TopCampaings> getCamapgneTop(@Valid ReportBusiness.TopFilter request, Pageable pageable) {
        return reportBusiness.searchTopCampaigns(request, pageable);
    }

    /**
     * ============================================================================================================
     **/

}
