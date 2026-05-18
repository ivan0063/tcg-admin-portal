package com.tcg.portal.infrastructure.scryfall.provider;

import com.tcg.portal.infrastructure.cardchain.CardSearchProvider;
import com.tcg.portal.infrastructure.cardchain.ProviderResult;
import com.tcg.portal.infrastructure.scryfall.ScryfallCardMapper;
import com.tcg.portal.infrastructure.scryfall.dto.ScryfallCardDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Component
@Order(2)
public class ScryfallFuzzyProvider implements CardSearchProvider {

    private static final Logger log = LoggerFactory.getLogger(ScryfallFuzzyProvider.class);

    private final RestClient restClient;
    private final ScryfallCardMapper mapper;

    public ScryfallFuzzyProvider(RestClient scryfallRestClient, ScryfallCardMapper mapper) {
        this.restClient = scryfallRestClient;
        this.mapper = mapper;
    }

    @Override
    public ProviderResult findByName(String name) {
        try {
            ScryfallCardDto dto = restClient.get()
                    .uri("/cards/named?fuzzy={name}", name)
                    .retrieve()
                    .body(ScryfallCardDto.class);
            if (dto == null) return ProviderResult.notFound(providerName(), 404, null);
            return ProviderResult.found(providerName(), mapper.toDomain(dto));
        } catch (RestClientResponseException e) {
            log.debug("Scryfall fuzzy '{}' HTTP {}: {}", name, e.getStatusCode().value(), e.getMessage());
            return ProviderResult.notFound(providerName(), e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (RestClientException e) {
            log.debug("Scryfall fuzzy '{}' network error: {}", name, e.getMessage());
            return ProviderResult.notFound(providerName(), 0, e.getMessage());
        }
    }

    @Override
    public String providerName() {
        return "Scryfall/fuzzy";
    }
}
