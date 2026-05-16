package com.tcg.portal.adapter.out.scryfall.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ScryfallSearchResponse(
        @JsonProperty("total_cards") int totalCards,
        @JsonProperty("has_more") boolean hasMore,
        List<ScryfallCardDto> data
) {}
