package com.tcg.portal.application.port.in;

import com.tcg.portal.domain.model.DeckAllocationResult;

import java.util.Map;

public interface DeckAllocationUseCase {
    Map<Long, DeckAllocationResult> allocateAll();
    DeckAllocationResult allocateForDeck(Long deckId);
}
