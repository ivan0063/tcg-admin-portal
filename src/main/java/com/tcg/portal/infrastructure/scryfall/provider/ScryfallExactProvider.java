package com.tcg.portal.infrastructure.scryfall.provider;

import com.tcg.portal.domain.model.Card;
import com.tcg.portal.infrastructure.cardchain.CardSearchProvider;
import com.tcg.portal.infrastructure.scryfall.ScryfallCardMapper;
import com.tcg.portal.infrastructure.scryfall.dto.ScryfallCardDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

@Component
@Order(1)
public class ScryfallExactProvider implements CardSearchProvider {

    private static final Logger log = LoggerFactory.getLogger(ScryfallExactProvider.class);

    private final RestClient restClient;
    private final ScryfallCardMapper mapper;

    public ScryfallExactProvider(RestClient scryfallRestClient, ScryfallCardMapper mapper) {
        this.restClient = scryfallRestClient;
        this.mapper = mapper;
    }

    @Override
    public Optional<Card> findByName(String name) {
        try {
            ScryfallCardDto dto = restClient.get()
                    .uri("/cards/named?exact={name}", name)
                    .retrieve()
                    .body(ScryfallCardDto.class);
            return Optional.ofNullable(dto).map(mapper::toDomain);
        } catch (RestClientException e) {
            log.debug("Scryfall exact '{}' not found: {}", name, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public String providerName() {
        return "Scryfall/exact";
    }
}
