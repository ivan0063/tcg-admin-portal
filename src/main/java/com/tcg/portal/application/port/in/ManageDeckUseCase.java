package com.tcg.portal.application.port.in;

import com.tcg.portal.domain.model.Deck;
import com.tcg.portal.domain.model.DeckFormat;

import java.util.List;

public interface ManageDeckUseCase {
    List<Deck> getAllDecks();
    Deck getDeck(Long id);
    Deck createDeck(String name, String description, DeckFormat format);
    Deck addCard(Long deckId, String scryfallId, int quantity, boolean sideboard);
    Deck removeCard(Long deckId, Long entryId);
    Deck updateEntry(Long deckId, Long entryId, int quantity, boolean sideboard);
    void deleteDeck(Long id);
    List<String> getImportFailures(Long deckId);
    void clearImportFailures(Long deckId);
}
