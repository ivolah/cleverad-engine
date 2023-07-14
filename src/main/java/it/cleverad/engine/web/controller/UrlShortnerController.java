package it.cleverad.engine.web.controller;

import it.cleverad.engine.service.shorturl.UrlLongRequest;
import it.cleverad.engine.service.shorturl.UrlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping(path = "/short")
public class UrlShortnerController {

    @Autowired
    private UrlService urlService;

    @GetMapping(path = "/old/{id}")
    public String getUrlOl(@PathVariable String shortUrl) {
        return urlService.getOriginalUrl(shortUrl);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String create(@ModelAttribute UrlLongRequest req) {
        return urlService.convertToShortUrl(req);
    }

//    @GetMapping(value = "{shortUrl}")
//    @Cacheable(value = "urls", key = "#shortUrl", sync = true)
//    public ResponseEntity<Void> getAndRedirect(@PathVariable String shortUrl) {
//        var url = urlService.getOriginalUrl(shortUrl);
//        return ResponseEntity.status(HttpStatus.FOUND)
//                .location(URI.create(url))
//                .build();
//    }

    @GetMapping(path = "{shortUrl}")
    public String getUrl(@PathVariable String shortUrl) {
        return urlService.getOriginalUrl(shortUrl);
    }

}
