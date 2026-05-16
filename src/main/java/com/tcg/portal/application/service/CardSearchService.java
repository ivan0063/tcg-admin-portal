package com.tcg.portal.application.service;

import com.tcg.portal.application.port.in.CardSearchUseCase;
import com.tcg.portal.application.port.out.CardSearchPort;
import com.tcg.portal.domain.exception.CardNotFoundException;
import com.tcg.portal.domain.model.Card;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardSearchService implements CardSearchUseCase {

    private final CardSearchPort cardSearchPort;

    public CardSearchService(CardSearchPort cardSearchPort) {
        this.cardSearchPort = cardSearchPort;
    }

    @Override
    public List<Card> searchCards(String query) {
        if (query == null || query.isBlank()) return List.of();
        return cardSearchPort.searchCards(query.trim());
    }

    @Override
    public Card getCardById(String scryfallId) {
        return cardSearchPort.findById(scryfallId)
                .orElseThrow(() -> new CardNotFoundException(scryfallId));
    }
}
