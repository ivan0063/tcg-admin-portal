package com.tcg.portal.application.service;

import com.tcg.portal.application.port.in.DeckAllocationUseCase;
import com.tcg.portal.application.port.in.ManageDeckUseCase;
import com.tcg.portal.application.port.out.CollectionRepository;
import com.tcg.portal.domain.model.Deck;
import com.tcg.portal.domain.model.DeckAllocationResult;
import com.tcg.portal.domain.model.DeckEntry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DeckAllocationService implements DeckAllocationUseCase {

    private final ManageDeckUseCase deckUseCase;
    private final CollectionRepository collectionRepository;

    public DeckAllocationService(ManageDeckUseCase deckUseCase,
                                 CollectionRepository collectionRepository) {
        this.deckUseCase = deckUseCase;
        this.collectionRepository = collectionRepository;
    }

    @Override
    public Map<Long, DeckAllocationResult> allocateAll() {
        Map<String, Integer> pool = new HashMap<>(collectionRepository.getGlobalInventoryByCard());
        List<Deck> decks = deckUseCase.getAllDecks().stream()
                .sorted(Comparator.comparing(Deck::getCreatedAt))
                .toList();

        Map<Long, DeckAllocationResult> results = new LinkedHashMap<>();
        for (Deck deck : decks) {
            results.put(deck.getId(), allocate(deck, pool));
        }
        return results;
    }

    @Override
    public DeckAllocationResult allocateForDeck(Long deckId) {
        return allocateAll().get(deckId);
    }

    private DeckAllocationResult allocate(Deck deck, Map<String, Integer> pool) {
        Map<String, Integer> required = aggregateRequired(deck);
        Map<String, Integer> allocated = new HashMap<>();
        int totalRequired = 0;
        int totalAllocated = 0;

        for (Map.Entry<String, Integer> entry : required.entrySet()) {
            String id = entry.getKey();
            int needed = entry.getValue();
            int available = pool.getOrDefault(id, 0);
            int given = Math.min(available, needed);
            pool.put(id, available - given);
            allocated.put(id, given);
            totalRequired += needed;
            totalAllocated += given;
        }

        return new DeckAllocationResult(deck.getId(), Map.copyOf(allocated), totalRequired, totalAllocated);
    }

    private Map<String, Integer> aggregateRequired(Deck deck) {
        Map<String, Integer> required = new HashMap<>();
        for (DeckEntry entry : deck.getEntries()) {
            required.merge(entry.getCard().scryfallId(), entry.getQuantity(), Integer::sum);
        }
        return required;
    }
}
