package com.tcg.portal.adapter.out.scryfall.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public record ScryfallCardDto(
        String id,
        String name,
        @JsonProperty("mana_cost") String manaCost,
        Double cmc,
        @JsonProperty("type_line") String typeLine,
        @JsonProperty("oracle_text") String oracleText,
        List<String> colors,
        @JsonProperty("color_identity") List<String> colorIdentity,
        String rarity,
        String set,
        @JsonProperty("set_name") String setName,
        @JsonProperty("image_uris") Map<String, String> imageUris,
        @JsonProperty("card_faces") List<ScryfallCardFaceDto> cardFaces,
        ScryfallPricesDto prices
) {}
