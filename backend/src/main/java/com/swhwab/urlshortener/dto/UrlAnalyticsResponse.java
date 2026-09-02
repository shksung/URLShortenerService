package com.swhwab.urlshortener.dto;

public record UrlAnalyticsResponse(
        String shortCode,
        String originalUrl,
        long clickCount,
        long createdAtEpochMillis
) {
}
