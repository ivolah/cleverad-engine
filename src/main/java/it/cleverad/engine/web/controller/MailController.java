package it.cleverad.engine.web.controller;

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
        return business.conferma(request, "CANALE");
    }
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = "/conferma/affiliato")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MailDTO inviaAffiliatoApprovato(@ModelAttribute MailService.BaseCreateRequest request) {
        return business.conferma(request, "Affiliato");
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = "/campagna")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MailDTO inviaInvitoCampagna(@ModelAttribute MailService.BaseCreateRequest request) {
        return business.invitoCampagna(request);
    }


    /**
     * ============================================================================================================
     **/

}
