package com.swhwab.urlshortener;

import com.swhwab.urlshortener.service.UrlShortenerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UrlShortenerServiceTest {

    @Autowired
    private UrlShortenerService urlShortenerService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateShortCodeForValidUrl() {
        String shortCode = urlShortenerService.createShortUrl("https://example.com/very/long/path");

        assertThat(shortCode).isNotBlank();
        assertThat(shortCode).hasSizeLessThanOrEqualTo(10);
    }

    @Test
    void shouldRedirectFromRootShortCodeEndpoint() throws Exception {
        String shortCode = urlShortenerService.createShortUrl("https://example.com/very/long/path");

        mockMvc.perform(get("/" + shortCode))
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, "https://example.com/very/long/path"));
    }

    @Test
    void shouldRejectUrlsThatPointBackToThisApplication() {
        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                        urlShortenerService.createShortUrl("https://localhost:9091/adfasdfadsfdasfadfasdfad"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("same application");
    }

    @Test
    void shouldRejectLoopbackHostsAndLocalDevelopmentUrls() {
        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                        urlShortenerService.createShortUrl("http://localhost:3000/dashboard"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("same application");

        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                        urlShortenerService.createShortUrl("http://[::1]/health"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("same application");
    }
}
