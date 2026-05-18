package com.tcg.portal.application.port.out;

import com.tcg.portal.domain.model.Card;

import java.util.List;
import java.util.Optional;

public interface CardSearchPort {
    List<Card> searchCards(String query);
    Optional<Card> findById(String scryfallId);
    /** Exact name lookup, falls back to fuzzy if no exact match. */
    Optional<Card> findByName(String name);
    /** Raw JSON diagnostics from the last failed findByName call on this thread. Null if last call succeeded or was never made. */
    default String lastDiagnostics() { return null; }
}
