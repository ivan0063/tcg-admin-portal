package com.tcg.portal.infrastructure.scryfall;

import com.tcg.portal.application.port.out.CardSearchPort;
import com.tcg.portal.domain.model.Card;
import com.tcg.portal.infrastructure.cardchain.CardSearchChain;
import com.tcg.portal.infrastructure.scryfall.dto.ScryfallCardDto;
import com.tcg.portal.infrastructure.scryfall.dto.ScryfallSearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Optional;

@Component
public class ScryfallAdapter implements CardSearchPort {

    private static final Logger log = LoggerFactory.getLogger(ScryfallAdapter.class);

    private final RestClient restClient;
    private final ScryfallCardMapper mapper;
    private final CardSearchChain chain;

    public ScryfallAdapter(RestClient scryfallRestClient,
                           ScryfallCardMapper mapper,
                           CardSearchChain chain) {
        this.restClient = scryfallRestClient;
        this.mapper = mapper;
        this.chain = chain;
    }

    @Override
    public List<Card> searchCards(String query) {
        try {
            ScryfallSearchResponse response = restClient.get()
                    .uri("/cards/search?q={q}&order=name&unique=cards", query)
                    .retrieve()
                    .body(ScryfallSearchResponse.class);
            if (response == null || response.data() == null) return List.of();
            return response.data().stream().map(mapper::toDomain).toList();
        } catch (RestClientException e) {
            log.warn("Scryfall search failed for '{}': {}", query, e.getMessage());
            return List.of();
        }
    }

    @Override
    public Optional<Card> findById(String scryfallId) {
        try {
            ScryfallCardDto dto = restClient.get()
                    .uri("/cards/{id}", scryfallId)
                    .retrieve()
                    .body(ScryfallCardDto.class);
            return Optional.ofNullable(dto).map(mapper::toDomain);
        } catch (RestClientException e) {
            log.warn("Scryfall lookup failed for id '{}': {}", scryfallId, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<Card> findByName(String name) {
        return chain.findByName(name);
    }
}
