package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.BBLinkBusiness;
import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.web.dto.BBLinkDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/bblink")
public class BBLinkController {

    @Autowired
    private BBLinkBusiness business;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    /**
     * ============================================================================================================
     **/

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public BBLinkDTO create(@ModelAttribute BBLinkBusiness.BaseCreateRequest request) {
        return business.create(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<BBLinkDTO> search(BBLinkBusiness.Filter request, Pageable pageable) {
        if (!jwtUserDetailsService.getRole().equals("Admin")) {
            request.setBrandbuddiesId(jwtUserDetailsService.getAffiliateID());
        }
        return business.search(request, pageable);
    }

//    @PatchMapping(path = "/{id}")
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public BBLinkDTO update(@PathVariable Long id, @RequestBody BBLinkBusiness.Filter request) {
//        return business.update(id, request);
//    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BBLinkDTO findById(@PathVariable Long id) {
        return business.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) {
        this.business.delete(id);
    }

    /**
     * ============================================================================================================
     **/

}