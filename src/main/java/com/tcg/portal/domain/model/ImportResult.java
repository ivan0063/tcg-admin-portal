package com.tcg.portal.domain.model;

import java.util.List;

public record ImportResult(
        Deck deck,
        List<Card> importedCards,
        List<FailedCard> failedCards
) {
    public boolean hasFailures()      { return !failedCards.isEmpty(); }
    public int importedCount()        { return importedCards.size(); }
    public int failedCount()          { return failedCards.size(); }
    public List<String> importedNames() {
        return importedCards.stream().map(Card::name).toList();
    }
    public List<String> failedNames() {
        return failedCards.stream().map(FailedCard::name).toList();
    }
}
