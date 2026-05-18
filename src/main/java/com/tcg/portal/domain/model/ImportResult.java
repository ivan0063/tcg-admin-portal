package com.tcg.portal.domain.model;

import java.util.List;

public record ImportResult(
        Deck deck,
        List<String> importedNames,
        List<FailedCard> failedCards
) {
    public boolean hasFailures()   { return !failedCards.isEmpty(); }
    public int importedCount()     { return importedNames.size(); }
    public int failedCount()       { return failedCards.size(); }
    public List<String> failedNames() {
        return failedCards.stream().map(FailedCard::name).toList();
    }
}
