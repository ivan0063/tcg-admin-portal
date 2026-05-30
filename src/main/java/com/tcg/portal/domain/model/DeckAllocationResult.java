package com.tcg.portal.domain.model;

import java.util.Map;

public record DeckAllocationResult(
        long deckId,
        Map<String, Integer> allocatedByCard,
        int totalRequired,
        int totalAllocated
) {
    public boolean isFullyBuildable() {
        return totalAllocated >= totalRequired;
    }

    public int missingCount() {
        return Math.max(0, totalRequired - totalAllocated);
    }

    public int getAllocated(String scryfallId) {
        return allocatedByCard.getOrDefault(scryfallId, 0);
    }
}
