package it.cleverad.engine.web.controller;

import it.cleverad.engine.service.telegram.app.ContactInfo;
import it.cleverad.engine.service.telegram.app.TelegramClientWrapper;
import it.cleverad.engine.service.telegram.bot.TelegramService;
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

    @Autowired
    TelegramClientWrapper telegramClientWrapper;

    /**
     * ============================================================================================================
     **/

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void create(@ModelAttribute TelegramService.BaseCreateRequest request) throws TelegramApiException {
        telegramService.invia(request);
    }

//    @RequestMapping(value = "/getInfo", method = RequestMethod.GET)
//    public ContactInfo getInfo(@RequestParam(value="phone") String phone) throws InterruptedException {
//        if (phone.substring(0,1).equals("+")) {
//            phone = phone.substring(1);
//        }
//
//        ContactInfo result = telegramClientWrapper.getInfoByPhone(phone);
//
//        if (!result.getPhotofilename().isEmpty()) {
//            result.setPhotofilename("/photos/" + result.getUserId());
//        }
//
//        return result;
//    }

    /**
     * ============================================================================================================
     **/

}