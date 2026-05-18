package com.tcg.portal.application.service;

import com.tcg.portal.application.port.in.ImportDeckUseCase;
import com.tcg.portal.application.port.out.ImportFailureRepository;
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

    public AsyncImportService(ImportDeckUseCase importDeckUseCase,
                              ImportJobStore jobStore,
                              ImportFailureRepository importFailureRepository) {
        this.importDeckUseCase = importDeckUseCase;
        this.jobStore = jobStore;
        this.importFailureRepository = importFailureRepository;
    }

    @Async
    public void startImport(Long deckId, String cardList) {
        jobStore.markPending(deckId);
        importFailureRepository.clearByDeckId(deckId);
        log.info("Async import started for deck {}", deckId);
        try {
            ImportResult result = importDeckUseCase.importFromList(deckId, cardList);
            if (!result.failedNames().isEmpty()) {
                importFailureRepository.saveFailures(deckId, result.failedNames());
            }
            jobStore.markDone(deckId, result.importedCount(), result.failedCount(), result.failedNames());
            log.info("Async import done for deck {}: {} imported, {} failed",
                    deckId, result.importedCount(), result.failedCount());
        } catch (Exception e) {
            log.error("Async import failed for deck {}", deckId, e);
            jobStore.markDone(deckId, 0, 1, java.util.List.of("Import error: " + e.getMessage()));
        }
    }
}
