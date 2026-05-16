package com.tcg.portal.application.port.in;

import com.tcg.portal.domain.model.ImportResult;

public interface ImportDeckUseCase {
    /**
     * Parses a plain-text card list and imports found cards into the given deck.
     * Cards that cannot be resolved via Scryfall are collected in the result
     * without stopping the import process.
     *
     * Supported list format:
     *   4 Lightning Bolt
     *   2x Goblin Guide
     *   SB: 3 Searing Blood
     *   Sideboard:
     *   2 Relic of Progenitus
     *   // comment lines are ignored
     */
    ImportResult importFromList(Long deckId, String cardList);
}
