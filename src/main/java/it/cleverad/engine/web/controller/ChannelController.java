package it.cleverad.engine.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cleverad.engine.business.ChannelBusiness;
import it.cleverad.engine.web.dto.ChannelDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Tag(name = "Channels", description = "Endpoints for all the Channels Operations")
@RestController
@RequestMapping(value = "/channel")
public class ChannelController {

    @Autowired
    private ChannelBusiness business;

    /**
     * ============================================================================================================
     **/

    @Operation(summary = "Create Channel", description = "Creates a new Channel")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ChannelDTO create(@ModelAttribute ChannelBusiness.BaseCreateRequest request) {
        return   business.create(request);
    }

    @Operation(summary = "Lists the Channels", description = "Lists the Channels, searched and paginated")
    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<ChannelDTO> search(ChannelBusiness.Filter request, Pageable pageable) {
        return business.search(request, pageable);
    }

    @Operation(summary = "Update the Channel", description = "Update the specific Channel")
    @PatchMapping(path = "/{id}" )
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ChannelDTO update(@PathVariable Long id, @RequestBody ChannelBusiness.Filter request) {
        return  business.update(id, request);
    }

    @Operation(summary = "Get the Channel", description = "Get the specific Channel")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ChannelDTO getByUuid(@PathVariable Long id) {
        return business.findById(id);
    }

    @Operation(summary = "Delete Channel", description = "Delete the specific Channel")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }

    /**
     * ============================================================================================================
     **/

}
