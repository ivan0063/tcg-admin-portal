package com.tcg.portal.infrastructure.scryfall.provider;

import com.tcg.portal.infrastructure.cardchain.CardSearchProvider;
import com.tcg.portal.infrastructure.cardchain.ProviderResult;
import com.tcg.portal.infrastructure.scryfall.ScryfallCardMapper;
import com.tcg.portal.infrastructure.scryfall.dto.ScryfallSearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Component
@Order(3)
public class ScryfallSearchProvider implements CardSearchProvider {

    private static final Logger log = LoggerFactory.getLogger(ScryfallSearchProvider.class);

    private final RestClient restClient;
    private final ScryfallCardMapper mapper;

    public ScryfallSearchProvider(RestClient scryfallRestClient, ScryfallCardMapper mapper) {
        this.restClient = scryfallRestClient;
        this.mapper = mapper;
    }

    @Override
    public ProviderResult findByName(String name) {
        try {
            String query = "!\"" + name + "\"";
            ScryfallSearchResponse response = restClient.get()
                    .uri("/cards/search?q={q}&unique=cards&order=released", query)
                    .retrieve()
                    .body(ScryfallSearchResponse.class);
            if (response == null || response.data() == null || response.data().isEmpty()) {
                return ProviderResult.notFound(providerName(), 200, "empty response");
            }
            return ProviderResult.found(providerName(), mapper.toDomain(response.data().get(0)));
        } catch (RestClientResponseException e) {
            log.debug("Scryfall search '{}' HTTP {}: {}", name, e.getStatusCode().value(), e.getMessage());
            return ProviderResult.notFound(providerName(), e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (RestClientException e) {
            log.debug("Scryfall search '{}' network error: {}", name, e.getMessage());
            return ProviderResult.notFound(providerName(), 0, e.getMessage());
        }
    }

    @Override
    public String providerName() {
        return "Scryfall/search";
    }
}
