package com.tcg.portal.application.service;

import com.tcg.portal.application.port.in.ManageDeckUseCase;
import com.tcg.portal.domain.model.Deck;
import com.tcg.portal.domain.model.DeckEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class AsyncPdfExportService {

    private static final Logger log = LoggerFactory.getLogger(AsyncPdfExportService.class);
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ManageDeckUseCase deckUseCase;
    private final DeckPdfExportService pdfExportService;
    private final PdfExportJobStore jobStore;

    public AsyncPdfExportService(ManageDeckUseCase deckUseCase,
                                 DeckPdfExportService pdfExportService,
                                 PdfExportJobStore jobStore) {
        this.deckUseCase = deckUseCase;
        this.pdfExportService = pdfExportService;
        this.jobStore = jobStore;
    }

    @Async
    public void generatePdf(Long deckId) {
        jobStore.markPending(deckId);
        log.info("PDF export started for deck {}", deckId);
        try {
            Deck deck = deckUseCase.getDeck(deckId);
            Map<String, byte[]> imageCache = fetchImages(deck);
            byte[] pdf = pdfExportService.generatePdf(deck, imageCache);
            jobStore.markDone(deckId, pdf);
            log.info("PDF export done for deck {} ({} bytes)", deckId, pdf.length);
        } catch (Exception e) {
            log.error("PDF export failed for deck {}", deckId, e);
            jobStore.markError(deckId, e.getMessage());
        }
    }

    private Map<String, byte[]> fetchImages(Deck deck) {
        List<DeckEntry> allEntries = deck.getEntries();
        Map<String, String> urlByScryfallId = allEntries.stream()
                .filter(e -> e.getCard().smallImageUri() != null)
                .collect(Collectors.toMap(
                        e -> e.getCard().scryfallId(),
                        e -> e.getCard().smallImageUri(),
                        (a, b) -> a));

        Map<String, byte[]> cache = new ConcurrentHashMap<>();
        urlByScryfallId.entrySet().parallelStream().forEach(entry -> {
            try {
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(entry.getValue()))
                        .timeout(Duration.ofSeconds(15))
                        .header("User-Agent", "TCGAdminPortal/1.0")
                        .GET()
                        .build();
                HttpResponse<InputStream> resp = HTTP.send(req, HttpResponse.BodyHandlers.ofInputStream());
                if (resp.statusCode() == 200) {
                    cache.put(entry.getKey(), resp.body().readAllBytes());
                }
            } catch (Exception e) {
                log.warn("Could not fetch image for {}: {}", entry.getKey(), e.getMessage());
            }
        });
        return cache;
    }
}
