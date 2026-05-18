package com.tcg.portal.application.port.out;

import java.util.List;

public interface ImportFailureRepository {
    List<String> findCardNamesByDeckId(Long deckId);
    void saveFailures(Long deckId, List<String> cardNames);
    void clearByDeckId(Long deckId);
}
