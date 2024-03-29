package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.ChannelBusiness;
import it.cleverad.engine.web.dto.ChannelDTO;
import it.cleverad.engine.web.dto.DictionaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/channel")
public class ChannelController {

    @Autowired
    private ChannelBusiness business;

    /**
     * ============================================================================================================
     **/

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ChannelDTO create(@ModelAttribute ChannelBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<ChannelDTO> search(ChannelBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ChannelDTO update(@PathVariable Long id, @RequestBody ChannelBusiness.Filter request) {
        return business.update(id, request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ChannelDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }

    @GetMapping("/{id}/user")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<ChannelDTO> getbyIdUser(@PathVariable Long id, Pageable pageable) {
        return business.getbyIdUser(id, pageable);
    }

    @GetMapping("/{campaignId}/campaign")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<ChannelDTO> getbyIdCampaignPrefiltrato(@PathVariable Long campaignId, Pageable pageable) {
        return business.getbyIdCampaignPrefiltrato(campaignId, pageable);
    }


    @GetMapping("/affiliate")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<ChannelDTO> getbyIdUserAll(Pageable pageable) {
        return business.getbyIdAffiliateAll(pageable);
    }

    @GetMapping("/{id}/affiliate")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<ChannelDTO> getbyIdAffiliate(@PathVariable Long id, Pageable pageable) {
        ChannelBusiness.Filter request = new ChannelBusiness.Filter();
        request.setAffiliateId(id);
        return business.search(request, pageable);
    }

    @GetMapping("/{id}/affiliate/active")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<ChannelDTO> getbyIdAffiliateAllActive(@PathVariable Long id, Pageable pageable) {
        ChannelBusiness.Filter request = new ChannelBusiness.Filter();
        request.setAffiliateId(id);
        request.setStatus(true);
        return business.search(request, pageable);
    }

    @GetMapping("/{id}/affiliatechannelcommissioncampaign")
    @ResponseStatus(HttpStatus.ACCEPTED)

    public Page<ChannelDTO> getbyIdAffiliateChannelCommissionTemplate(@PathVariable Long id, Pageable pageable) {
        return business.getbyIdAffiliateChannelCommissionTemplate(id, pageable);
    }

    @GetMapping("/types")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<DictionaryDTO> getTypes() {
        return business.getTypes();
    }

    @GetMapping("/approve")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<ChannelDTO> daApprovare(Pageable pageable) {
        ChannelBusiness.Filter request = new ChannelBusiness.Filter();
        request.setStatus(false);
        return business.search(request, pageable);
    }

    @PatchMapping(path = "/{id}/disable")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ChannelDTO disable(@PathVariable Long id) {
        return business.disable(id);
    }

    @PatchMapping(path = "/{id}/enable")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ChannelDTO enable(@PathVariable Long id) {
        return business.enable(id);
    }

    /**
     * ============================================================================================================
     **/
}
