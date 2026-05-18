package com.tcg.portal.infrastructure.cardchain;

import com.tcg.portal.domain.model.Card;

import java.util.Optional;

public record ChainResult(Optional<Card> card, String diagnosticsJson) {
    public boolean found() { return card.isPresent(); }
}
