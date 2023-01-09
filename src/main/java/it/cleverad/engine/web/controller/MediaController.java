package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.MediaBusiness;
import it.cleverad.engine.web.dto.MediaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/media")
public class MediaController {

    @Autowired
    private MediaBusiness business;

    /**
     * ============================================================================================================
     **/

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MediaDTO create(@ModelAttribute MediaBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<MediaDTO> search(MediaBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MediaDTO update(@PathVariable Long id, @RequestBody MediaBusiness.Filter request) {
        return business.update(id, request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MediaDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }

    @GetMapping("/{id}/campaign")
    @ResponseStatus(HttpStatus.OK)
    public Page<MediaDTO> getByCampaignId(@PathVariable Long id, Pageable pageable) {
        return business.getByCampaignId(id, pageable);
    }

    @GetMapping("/{id}/campaign/{idCampaign}")
    @ResponseStatus(HttpStatus.OK)
    public MediaDTO getByCampaignId(@PathVariable Long id, @PathVariable Long idCampaign) {
        return business.getByIdAndCampaignID(id, idCampaign);
    }

    @GetMapping("/{id}/campaign/{idCampaign}/channel/{idChannel}")
    @ResponseStatus(HttpStatus.OK)
    public MediaDTO getByCampaignId(@PathVariable Long id, @PathVariable Long idCampaign, @PathVariable Long idChannel) {
        return business.getByIdAndCampaignIDChannelID(id, idCampaign, idChannel);
    }

    /**
     * ============================================================================================================
     **/

}
