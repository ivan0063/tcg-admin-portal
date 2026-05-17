package com.tcg.portal.infrastructure.scryfall.provider;

import com.tcg.portal.domain.model.Card;
import com.tcg.portal.infrastructure.cardchain.CardSearchProvider;
import com.tcg.portal.infrastructure.scryfall.ScryfallCardMapper;
import com.tcg.portal.infrastructure.scryfall.dto.ScryfallSearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

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
    public Optional<Card> findByName(String name) {
        try {
            // Exact-name fulltext search — matches cards whose printed name equals the query
            String query = "!\"" + name + "\"";
            ScryfallSearchResponse response = restClient.get()
                    .uri("/cards/search?q={q}&unique=cards&order=released", query)
                    .retrieve()
                    .body(ScryfallSearchResponse.class);
            if (response == null || response.data() == null || response.data().isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(mapper.toDomain(response.data().get(0)));
        } catch (RestClientException e) {
            log.debug("Scryfall search '{}' not found: {}", name, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public String providerName() {
        return "Scryfall/search";
    }
}
