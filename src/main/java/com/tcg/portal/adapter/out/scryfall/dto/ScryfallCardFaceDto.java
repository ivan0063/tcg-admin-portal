package com.tcg.portal.adapter.out.scryfall.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record ScryfallCardFaceDto(
        String name,
        @JsonProperty("image_uris") Map<String, String> imageUris
) {}
