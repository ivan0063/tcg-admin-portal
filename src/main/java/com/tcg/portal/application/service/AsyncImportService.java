package com.tcg.portal.application.service;

import com.tcg.portal.application.port.in.ImportDeckUseCase;
import com.tcg.portal.application.port.in.ManageCollectionUseCase;
import com.tcg.portal.application.port.out.ImportFailureRepository;
import com.tcg.portal.domain.model.CardCondition;
import com.tcg.portal.domain.model.ImportResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncImportService {

    private static final Logger log = LoggerFactory.getLogger(AsyncImportService.class);

    private final ImportDeckUseCase importDeckUseCase;
    private final ImportJobStore jobStore;
    private final ImportFailureRepository importFailureRepository;
    private final ManageCollectionUseCase collectionUseCase;

    public AsyncImportService(ImportDeckUseCase importDeckUseCase,
                              ImportJobStore jobStore,
                              ImportFailureRepository importFailureRepository,
                              ManageCollectionUseCase collectionUseCase) {
        this.importDeckUseCase = importDeckUseCase;
        this.jobStore = jobStore;
        this.importFailureRepository = importFailureRepository;
        this.collectionUseCase = collectionUseCase;
    }

    @Async
    public void startImport(Long deckId, String cardList, Long targetCollectionId) {
        int total = estimateCardCount(cardList);
        jobStore.markPending(deckId, total);
        importFailureRepository.clearByDeckId(deckId);
        log.info("Async import started for deck {} ({} cards estimated, collection={})",
                deckId, total, targetCollectionId);
        try {
            ImportResult result = importDeckUseCase.importFromList(deckId, cardList,
                    (processed, t, imported, failed) ->
                            jobStore.markProgress(deckId, processed, t, imported, failed));

            if (!result.failedCards().isEmpty()) {
                importFailureRepository.saveFailures(deckId, result.failedCards());
            }

            if (targetCollectionId != null) {
                addDistinctCardsToCollection(result, targetCollectionId);
            }

            jobStore.markDone(deckId, result.importedCount(), result.failedCount(), result.failedNames());
            log.info("Async import done for deck {}: {} imported, {} failed",
                    deckId, result.importedCount(), result.failedCount());
        } catch (Exception e) {
            log.error("Async import failed for deck {}", deckId, e);
            jobStore.markDone(deckId, 0, 1, java.util.List.of("Import error: " + e.getMessage()));
        }
    }

    private void addDistinctCardsToCollection(ImportResult result, Long collectionId) {
        result.importedCards().stream()
                .collect(java.util.stream.Collectors.toMap(
                        com.tcg.portal.domain.model.Card::scryfallId,
                        c -> c,
                        (a, b) -> a))
                .values()
                .forEach(card -> {
                    try {
                        collectionUseCase.addCard(collectionId, card.scryfallId(), 1,
                                CardCondition.NEAR_MINT, false);
                    } catch (Exception e) {
                        log.warn("Could not add '{}' to collection {}: {}",
                                card.name(), collectionId, e.getMessage());
                    }
                });
    }

    private static int estimateCardCount(String cardList) {
        if (cardList == null || cardList.isBlank()) return 0;
        return (int) cardList.lines()
                .map(String::trim)
                .filter(l -> !l.isEmpty()
                        && !l.startsWith("//")
                        && !l.startsWith("#")
                        && !l.equalsIgnoreCase("Sideboard:")
                        && !l.equalsIgnoreCase("Sideboard")
                        && !l.equalsIgnoreCase("Deck"))
                .count();
    }
}
