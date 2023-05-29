package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.ReportBusiness;
import it.cleverad.engine.persistence.model.service.ReportDaily;
import it.cleverad.engine.persistence.model.service.ReportTopAffiliates;
import it.cleverad.engine.persistence.model.service.ReportTopCampaings;
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
    public Page<ReportTopCampaings> getCamapgneTop(@Valid ReportBusiness.TopFilter request, Pageable pageable) {
        return reportBusiness.searchTopCampaigns(request, pageable);
    }

    @GetMapping(path = "/campagne/top/channel")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<ReportTopCampaings> getCamapgneTopChannel(@Valid ReportBusiness.TopFilter request, Pageable pageable) {
        return reportBusiness.searchTopCampaignsChannel(request, pageable);
    }

    @GetMapping(path = "/affiliates/top")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<ReportTopAffiliates> getAffiliatiTop(@Valid ReportBusiness.TopFilter request, Pageable pageable) {
        return reportBusiness.searchTopAffilaites(request, pageable);
    }

    @GetMapping(path = "/daily")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<ReportDaily> getDaily(@Valid ReportBusiness.TopFilter request, Pageable pageable) {
        return reportBusiness.searchDaily(request, pageable);
    }

//    @GetMapping(path = "/cpc/click/grouped/day")
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public Page<StatClickCpc> getGroupedCpcClicks(@Valid ReportBusiness.TopFilter request, Pageable pageable) {
//        return reportBusiness.getGroupedCpcClicks(request, pageable);
//    }

    /**
     * ============================================================================================================
     **/

}
