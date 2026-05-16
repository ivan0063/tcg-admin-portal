package com.tcg.portal.application.port.in;

import com.tcg.portal.domain.model.Card;

import java.util.List;

public interface CardSearchUseCase {
    List<Card> searchCards(String query);
    Card getCardById(String scryfallId);
}
