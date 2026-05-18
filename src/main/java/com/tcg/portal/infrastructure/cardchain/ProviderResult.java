package com.tcg.portal.infrastructure.cardchain;

import com.tcg.portal.domain.model.Card;

import java.util.Optional;

public record ProviderResult(
        String providerName,
        Optional<Card> card,
        int statusCode,
        String rawBody
) {
    public boolean found() { return card.isPresent(); }

    public static ProviderResult found(String providerName, Card card) {
        return new ProviderResult(providerName, Optional.of(card), 200, null);
    }

    public static ProviderResult notFound(String providerName, int statusCode, String rawBody) {
        return new ProviderResult(providerName, Optional.empty(), statusCode, rawBody);
    }

    public static ProviderResult skipped(String providerName, String reason) {
        return new ProviderResult(providerName, Optional.empty(), 0, reason);
    }
}
