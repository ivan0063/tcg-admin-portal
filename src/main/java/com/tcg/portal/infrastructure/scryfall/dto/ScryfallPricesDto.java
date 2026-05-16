package com.tcg.portal.infrastructure.scryfall.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ScryfallPricesDto(
        String usd,
        @JsonProperty("usd_foil") String usdFoil
) {}
