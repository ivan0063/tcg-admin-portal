package com.tcg.portal.infrastructure.scryfall.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ScryfallSetDto(
        String code,
        String name,
        @JsonProperty("set_type") String setType,
        @JsonProperty("released_at") String releasedAt,
        @JsonProperty("card_count") int cardCount,
        @JsonProperty("icon_svg_uri") String iconSvgUri
) {}
