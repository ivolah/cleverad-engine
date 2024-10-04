package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.ReportBusiness;
import it.cleverad.engine.persistence.model.service.QueryTopElenco;
import it.cleverad.engine.web.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

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
    public it.cleverad.engine.web.dto.ReportDTO create(@ModelAttribute ReportBusiness.BaseCreateRequest request) {
        return reportBusiness.create(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<it.cleverad.engine.web.dto.ReportDTO> search(ReportBusiness.Filter request, Pageable pageable) {
        return reportBusiness.search(request, pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public it.cleverad.engine.web.dto.ReportDTO getByUuid(@PathVariable Long id) {
        return reportBusiness.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.reportBusiness.delete(id);
    }

    /**
     * ============================================================================================================
     **/

    @GetMapping(path = "/campagne/orderby/imp")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<QueryTopElenco> searchCampaginsOrderedStatWidget(@Valid ReportBusiness.TopFilter request, Pageable pageable) {
        request.setDictionaryIds(null);
        return reportBusiness.searchCampaginsOrderedStatWidget(request);
    }

    @GetMapping(path = "/daily")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<ReportDailyDTO> getDaily(@Valid ReportBusiness.TopFilter request, Pageable pageable) {
        return reportBusiness.searchReportDaily(request, pageable);
    }

    @GetMapping(path = "/campagne/top")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<ReportCampagneDTO> getCamapgneTop(@Valid ReportBusiness.TopFilter request, Pageable pageable) {
        return reportBusiness.searchReportCampaign(request, pageable);
    }

    @GetMapping(path = "/affiliates")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<ReportAffiliatesDTO> getAffiliati(@Valid ReportBusiness.TopFilter request, Pageable pageable) {
        return reportBusiness.searchReportAffiliate(request, pageable);
    }

    @GetMapping(path = "/affiliates/top")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<ReportAffiliatesChannelDTO> searchReportAffiliateChannel(@Valid ReportBusiness.TopFilter request, Pageable pageable) {
        return reportBusiness.searchReportAffiliateChannel(request, pageable);
    }

    @GetMapping(path = "/campagne/top/channel")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<ReportAffiliatesChannelCampaignDTO> searchReportAffiliateChannelCampaign(@Valid ReportBusiness.TopFilter request, Pageable pageable) {
        return reportBusiness.searchReportAffiliateChannelCampaign(request, pageable);
    }

    /*
     * ============================================================================================================
     **/

}