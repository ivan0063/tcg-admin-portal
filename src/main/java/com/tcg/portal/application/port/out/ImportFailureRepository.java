package com.tcg.portal.application.port.out;

import com.tcg.portal.domain.model.FailedCard;

import java.util.List;

public interface ImportFailureRepository {
    List<FailedCard> findFailuresByDeckId(Long deckId);
    void saveFailures(Long deckId, List<FailedCard> cards);
    void clearByDeckId(Long deckId);
}
