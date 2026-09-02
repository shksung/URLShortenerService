package com.swhwab.urlshortener.controller;

import com.swhwab.urlshortener.dto.CreateShortUrlRequest;
import com.swhwab.urlshortener.dto.UrlAnalyticsResponse;
import com.swhwab.urlshortener.dto.UrlRedirectResponse;
import com.swhwab.urlshortener.service.UrlShortenerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UrlShortenerController {

    private final UrlShortenerService urlShortenerService;

    public UrlShortenerController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<String> shortenUrl(@Valid @RequestBody CreateShortUrlRequest request) {
        String shortCode = urlShortenerService.createShortUrl(request.url());
        return ResponseEntity.status(HttpStatus.CREATED).body(shortCode);
    }

    @GetMapping("/analytics/{shortCode}")
    public ResponseEntity<UrlAnalyticsResponse> analytics(@PathVariable String shortCode) {
        return ResponseEntity.ok(urlShortenerService.getAnalytics(shortCode));
    }

}
