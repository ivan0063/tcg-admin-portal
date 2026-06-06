package com.tcg.portal.infrastructure.scryfall;

import com.tcg.portal.domain.model.Card;
import com.tcg.portal.infrastructure.scryfall.dto.ScryfallCardDto;
import com.tcg.portal.infrastructure.scryfall.dto.ScryfallCardFaceDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
public class ScryfallCardMapper {

    public Card toDomain(ScryfallCardDto dto) {
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
                resolveImageUri(dto, "normal"),
                resolveImageUri(dto, "small"),
                parsePrice(dto.prices() != null ? dto.prices().usd() : null),
                parsePrice(dto.prices() != null ? dto.prices().usdFoil() : null),
                dto.collectorNumber()
        );
    }

    public String resolveImageUri(ScryfallCardDto dto, String size) {
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

    public BigDecimal parsePrice(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try {
            return new BigDecimal(raw);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
