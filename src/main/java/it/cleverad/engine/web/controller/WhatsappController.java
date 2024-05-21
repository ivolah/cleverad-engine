package it.cleverad.engine.web.controller;


import it.cleverad.engine.service.webapps.WhatsappService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/whatsapp")
@Slf4j
public class WhatsappController {

    @Autowired
    WhatsappService service;

    /**
     * ============================================================================================================
     **/

    @GetMapping(value = "/check")
    public Boolean getInfo(@NotNull @RequestParam(value = "phone") String phone) {
        if (phone.charAt(0) == '+') {
            phone = phone.substring(1);
        }
        return service.checkNumber(phone);
    }

    /**
     * ============================================================================================================
     **/

}