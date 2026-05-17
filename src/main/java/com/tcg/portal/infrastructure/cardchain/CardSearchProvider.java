package com.tcg.portal.infrastructure.cardchain;

import com.tcg.portal.domain.model.Card;

import java.util.Optional;

public interface CardSearchProvider {
    Optional<Card> findByName(String name);
    String providerName();
}
