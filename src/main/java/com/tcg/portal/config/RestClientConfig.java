package com.tcg.portal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${scryfall.base-url}")
    private String scryfallBaseUrl;

    @Bean
    public RestClient scryfallRestClient() {
        return RestClient.builder()
                .baseUrl(scryfallBaseUrl)
                .defaultHeader("User-Agent", "TCGAdminPortal/1.0")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
