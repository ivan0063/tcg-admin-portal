package com.tcg.portal.application.service;

import com.tcg.portal.application.port.in.ManageDeckUseCase;
import com.tcg.portal.application.port.out.CardCachePort;
import com.tcg.portal.application.port.out.CardSearchPort;
import com.tcg.portal.application.port.out.DeckRepository;
import com.tcg.portal.application.port.out.ImportFailureRepository;
import com.tcg.portal.domain.exception.CardNotFoundException;
import com.tcg.portal.domain.exception.DeckNotFoundException;
import com.tcg.portal.domain.model.Card;
import com.tcg.portal.domain.model.Deck;
import com.tcg.portal.domain.model.DeckEntry;
import com.tcg.portal.domain.model.DeckFormat;
import com.tcg.portal.domain.model.FailedCard;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class DeckService implements ManageDeckUseCase {

    private final DeckRepository deckRepository;
    private final CardCachePort cardCachePort;
    private final CardSearchPort cardSearchPort;
    private final ImportFailureRepository importFailureRepository;

    public DeckService(DeckRepository deckRepository,
                       CardCachePort cardCachePort,
                       CardSearchPort cardSearchPort,
                       ImportFailureRepository importFailureRepository) {
        this.deckRepository = deckRepository;
        this.cardCachePort = cardCachePort;
        this.cardSearchPort = cardSearchPort;
        this.importFailureRepository = importFailureRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Deck> getAllDecks() {
        return deckRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Deck getDeck(Long id) {
        return deckRepository.findById(id)
                .orElseThrow(() -> new DeckNotFoundException(id));
    }

    @Override
    public Deck createDeck(String name, String description, DeckFormat format) {
        Deck deck = new Deck(null, name, description, format, LocalDateTime.now(), new ArrayList<>());
        return deckRepository.save(deck);
    }

    @Override
    public Deck updateDeck(Long id, String name, String description, DeckFormat format) {
        Deck deck = getDeck(id);
        deck.setName(name);
        deck.setDescription(description);
        deck.setFormat(format);
        return deckRepository.save(deck);
    }

    @Override
    public Deck addCard(Long deckId, String scryfallId, int quantity, boolean sideboard) {
        Deck deck = getDeck(deckId);
        Card card = resolveCard(scryfallId);

        deck.getEntries().stream()
                .filter(e -> e.getCard().scryfallId().equals(scryfallId) && e.isSideboard() == sideboard)
                .findFirst()
                .ifPresentOrElse(
                        existing -> existing.setQuantity(existing.getQuantity() + quantity),
                        () -> deck.getEntries().add(new DeckEntry(null, card, quantity, sideboard))
                );

        return deckRepository.save(deck);
    }

    @Override
    public Deck removeCard(Long deckId, Long entryId) {
        Deck deck = getDeck(deckId);
        deck.getEntries().removeIf(e -> e.getId().equals(entryId));
        return deckRepository.save(deck);
    }

    @Override
    public Deck updateEntry(Long deckId, Long entryId, int quantity, boolean sideboard) {
        Deck deck = getDeck(deckId);
        deck.getEntries().stream()
                .filter(e -> e.getId().equals(entryId))
                .findFirst()
                .ifPresent(e -> {
                    if (quantity <= 0) deck.getEntries().remove(e);
                    else {
                        e.setQuantity(quantity);
                        e.setSideboard(sideboard);
                    }
                });
        return deckRepository.save(deck);
    }

    @Override
    public void deleteDeck(Long id) {
        importFailureRepository.clearByDeckId(id);
        deckRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FailedCard> getImportFailures(Long deckId) {
        return importFailureRepository.findFailuresByDeckId(deckId);
    }

    @Override
    public void clearImportFailures(Long deckId) {
        importFailureRepository.clearByDeckId(deckId);
    }

    private Card resolveCard(String scryfallId) {
        return cardCachePort.findById(scryfallId)
                .orElseGet(() -> {
                    Card fetched = cardSearchPort.findById(scryfallId)
                            .orElseThrow(() -> new CardNotFoundException(scryfallId));
                    return cardCachePort.save(fetched);
                });
    }
}
