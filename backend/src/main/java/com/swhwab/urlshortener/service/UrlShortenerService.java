package com.swhwab.urlshortener.service;

import com.swhwab.urlshortener.dto.UrlAnalyticsResponse;
import com.swhwab.urlshortener.dto.UrlRedirectResponse;
import com.swhwab.urlshortener.entity.ShortUrl;
import com.swhwab.urlshortener.repository.ShortUrlRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class UrlShortenerService {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_CODE_LENGTH = 7;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final ShortUrlRepository repository;
    private final Set<String> disallowedHosts;

    public UrlShortenerService(
            ShortUrlRepository repository,
            @Value("#{'${urlshortener.blocked-hosts:localhost,127.0.0.1,0.0.0.0}'.split(',')}") String[] blockedHosts) {
        this.repository = repository;
        this.disallowedHosts = Arrays.stream(blockedHosts)
                .map(String::trim)
                .filter(host -> !host.isEmpty())
                .map(host -> host.toLowerCase(Locale.ROOT))
                .collect(Collectors.toCollection(TreeSet::new));
    }

    @Transactional
    public String createShortUrl(String originalUrl) {
        String normalizedUrl = normalizeUrl(originalUrl);
        String shortCode = generateUniqueShortCode();

        ShortUrl shortUrl = ShortUrl.builder()
                .shortCode(shortCode)
                .originalUrl(normalizedUrl)
                .clickCount(0)
                .createdAtEpochMillis(System.currentTimeMillis())
                .build();

        repository.save(shortUrl);
        return shortCode;
    }

    @Transactional(readOnly = true)
    public UrlRedirectResponse resolveShortUrl(String shortCode) {
        ShortUrl shortUrl = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new IllegalArgumentException("Short code not found: " + shortCode));

        shortUrl.setClickCount(shortUrl.getClickCount() + 1);
        repository.save(shortUrl);

        return new UrlRedirectResponse(shortUrl.getOriginalUrl());
    }

    @Transactional(readOnly = true)
    public UrlAnalyticsResponse getAnalytics(String shortCode) {
        ShortUrl shortUrl = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new IllegalArgumentException("Short code not found: " + shortCode));

        return new UrlAnalyticsResponse(
                shortUrl.getShortCode(),
                shortUrl.getOriginalUrl(),
                shortUrl.getClickCount(),
                shortUrl.getCreatedAtEpochMillis()
        );
    }

    private String normalizeUrl(String originalUrl) {
        String trimmed = originalUrl.trim();
        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            throw new IllegalArgumentException("URL must start with http:// or https://");
        }

        try {
            String host = URI.create(trimmed).getHost();
            if (host != null && isDisallowedHost(host)) {
                throw new IllegalArgumentException("URL cannot point back to the same application and create a redirect loop");
            }
        } catch (IllegalArgumentException exception) {
            if ("URL cannot point back to the same application and create a redirect loop".equals(exception.getMessage())) {
                throw exception;
            }
            throw new IllegalArgumentException("URL is not a valid HTTP/HTTPS address", exception);
        }

        return trimmed;
    }

    private boolean isDisallowedHost(String host) {
        String normalizedHost = host.trim().toLowerCase(Locale.ROOT).replaceAll("\\.$", "");
        if (disallowedHosts.contains(normalizedHost)) {
            return true;
        }

        try {
            return InetAddress.getByName(normalizedHost).isLoopbackAddress();
        } catch (UnknownHostException exception) {
            return false;
        }
    }

    private String generateUniqueShortCode() {
        String shortCode;
        do {
            shortCode = generateRandomCode();
        } while (repository.existsByShortCode(shortCode));
        return shortCode;
    }

    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(SHORT_CODE_LENGTH);
        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString().toLowerCase(Locale.ROOT);
    }
}
