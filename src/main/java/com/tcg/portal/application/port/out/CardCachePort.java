package com.tcg.portal.application.port.out;

import com.tcg.portal.domain.model.Card;

import java.util.Optional;

public interface CardCachePort {
    Optional<Card> findById(String scryfallId);
    Optional<Card> findByName(String name);
    Card save(Card card);
}
