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
    public ProviderResult findByName(String name) {
        try {
            ScryfallCardDto dto = restClient.get()
                    .uri("/cards/named?exact={name}", name)
                    .retrieve()
                    .body(ScryfallCardDto.class);
            if (dto == null) return ProviderResult.notFound(providerName(), 404, null);
            return ProviderResult.found(providerName(), mapper.toDomain(dto));
        } catch (RestClientResponseException e) {
            log.debug("Scryfall exact '{}' HTTP {}: {}", name, e.getStatusCode().value(), e.getMessage());
            return ProviderResult.notFound(providerName(), e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (RestClientException e) {
            log.debug("Scryfall exact '{}' network error: {}", name, e.getMessage());
            return ProviderResult.notFound(providerName(), 0, e.getMessage());
        }
    }

    @Override
    public String providerName() {
        return "Scryfall/exact";
    }
}
