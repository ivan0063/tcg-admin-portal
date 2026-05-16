package com.tcg.portal.application.port.out;

import com.tcg.portal.domain.model.Card;

import java.util.List;
import java.util.Optional;

public interface CardSearchPort {
    List<Card> searchCards(String query);
    Optional<Card> findById(String scryfallId);
}
