package it.cleverad.engine.web.controller;

import it.cleverad.engine.service.telegram.TelegramService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@CrossOrigin
@RestController
@RequestMapping(value = "/telegram")
@Slf4j
public class TelegramController {

    @Autowired
    TelegramService telegramService;

    /**
     * ============================================================================================================
     **/

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void create(@ModelAttribute TelegramService.BaseCreateRequest request) throws TelegramApiException {
        telegramService.invia(request);
    }

    /**
     * ============================================================================================================
     **/


}
