package com.tcg.portal.application.port.out;

import com.tcg.portal.domain.model.Deck;

import java.util.List;
import java.util.Optional;

public interface DeckRepository {
    List<Deck> findAll();
    Optional<Deck> findById(Long id);
    Deck save(Deck deck);
    void deleteById(Long id);
}
