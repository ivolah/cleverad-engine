package it.cleverad.engine.web.controller;

import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.service.MailService;
import it.cleverad.engine.web.dto.MailDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/mail")
public class MailController {

    @Autowired
    private MailService business;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    /**
     * ============================================================================================================
     **/

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = "/custom")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MailDTO inviaCustom(@ModelAttribute MailService.BaseCreateRequest request) {
        return business.inviaCustom(request);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = "/registrazione")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MailDTO inviaMailRegistrazione(@ModelAttribute MailService.BaseCreateRequest request) {
        return business.inviaMailRegistrazione(request);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = "/conferma/canale")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MailDTO inviaCanaleApprovato(@ModelAttribute MailService.BaseCreateRequest request) {
        return business.confermaCanale(request);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = "/rifiuto/canale")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MailDTO inviaCanaleRifiutato(@ModelAttribute MailService.BaseCreateRequest request) {
        return business.rifiutoCanale(request);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = "/conferma/affiliato")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MailDTO inviaAffiliatoApprovato(@ModelAttribute MailService.BaseCreateRequest request) {
        return business.confermaAffiliato(request);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = "/rifiuto/affiliato")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MailDTO inviaAffiliatoRifiutato(@ModelAttribute MailService.BaseCreateRequest request) {
        return business.rifiutoAffiliato(request);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = "/campagna")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MailDTO inviaInvitoCampagna(@ModelAttribute MailService.BaseCreateRequest request) {
        return business.invitoCampagna(request);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = "/campagna/affiliate")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MailDTO inviaInvitoCampagnaAffiliate(@ModelAttribute MailService.BaseCreateRequest request) {
        request.setAffiliateId(jwtUserDetailsService.getAffiliateId());
        return business.invitoCampagna(request);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = "/template")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MailDTO inviaTemplate(@ModelAttribute MailService.BaseCreateRequest request) {
        return business.invioTemplate(request);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = "/richiesta/invito")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void inviaRichiesta(@ModelAttribute MailService.BaseCreateRequest request) {
        business.invioRichiesta(request);
    }


    /**
     * ============================================================================================================
     **/

}