package com.swhwab.urlshortener.controller;

import com.swhwab.urlshortener.dto.UrlRedirectResponse;
import com.swhwab.urlshortener.service.UrlShortenerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class RedirectController {

    private final UrlShortenerService urlShortenerService;

    public RedirectController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        UrlRedirectResponse redirect = urlShortenerService.resolveShortUrl(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(redirect.originalUrl()))
                .build();
    }
}
