package com.tcg.portal.adapter.out.scryfall;

import com.tcg.portal.adapter.out.scryfall.dto.ScryfallCardDto;
import com.tcg.portal.adapter.out.scryfall.dto.ScryfallCardFaceDto;
import com.tcg.portal.adapter.out.scryfall.dto.ScryfallSearchResponse;
import com.tcg.portal.application.port.out.CardSearchPort;
import com.tcg.portal.domain.model.Card;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ScryfallAdapter implements CardSearchPort {

    private static final Logger log = LoggerFactory.getLogger(ScryfallAdapter.class);

    private final RestClient restClient;

    public ScryfallAdapter(RestClient scryfallRestClient) {
        this.restClient = scryfallRestClient;
    }

    @Override
    public List<Card> searchCards(String query) {
        try {
            ScryfallSearchResponse response = restClient.get()
                    .uri("/cards/search?q={q}&order=name&unique=cards", query)
                    .retrieve()
                    .body(ScryfallSearchResponse.class);
            if (response == null || response.data() == null) return List.of();
            return response.data().stream().map(this::toDomain).toList();
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
            return Optional.ofNullable(dto).map(this::toDomain);
        } catch (RestClientException e) {
            log.warn("Scryfall lookup failed for id '{}': {}", scryfallId, e.getMessage());
            return Optional.empty();
        }
    }

    private Card toDomain(ScryfallCardDto dto) {
        String imageUri = resolveImageUri(dto, "normal");
        String smallImageUri = resolveImageUri(dto, "small");

        BigDecimal usdPrice = parsePrice(dto.prices() != null ? dto.prices().usd() : null);
        BigDecimal usdFoilPrice = parsePrice(dto.prices() != null ? dto.prices().usdFoil() : null);

        return new Card(
                dto.id(),
                dto.name(),
                dto.manaCost(),
                dto.cmc() != null ? dto.cmc() : 0.0,
                dto.typeLine(),
                dto.oracleText(),
                dto.colors() != null ? dto.colors() : List.of(),
                dto.colorIdentity() != null ? dto.colorIdentity() : List.of(),
                dto.rarity(),
                dto.set(),
                dto.setName(),
                imageUri,
                smallImageUri,
                usdPrice,
                usdFoilPrice
        );
    }

    private String resolveImageUri(ScryfallCardDto dto, String size) {
        if (dto.imageUris() != null) {
            return dto.imageUris().get(size);
        }
        if (dto.cardFaces() != null && !dto.cardFaces().isEmpty()) {
            ScryfallCardFaceDto face = dto.cardFaces().get(0);
            Map<String, String> faceUris = face.imageUris();
            if (faceUris != null) return faceUris.get(size);
        }
        return null;
    }

    private BigDecimal parsePrice(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try {
            return new BigDecimal(raw);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
