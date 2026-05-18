package com.tcg.portal.application.service;

import com.tcg.portal.application.port.in.ImportDeckUseCase;
import com.tcg.portal.application.port.in.ImportProgressCallback;
import com.tcg.portal.application.port.out.CardCachePort;
import com.tcg.portal.application.port.out.CardSearchPort;
import com.tcg.portal.application.port.out.DeckRepository;
import com.tcg.portal.domain.exception.DeckNotFoundException;
import com.tcg.portal.domain.model.Card;
import com.tcg.portal.domain.model.Deck;
import com.tcg.portal.domain.model.DeckEntry;
import com.tcg.portal.domain.model.FailedCard;
import com.tcg.portal.domain.model.ImportResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class ImportDeckService implements ImportDeckUseCase {

    private static final Logger log = LoggerFactory.getLogger(ImportDeckService.class);
    private static final Pattern QTY_PATTERN = Pattern.compile("^(\\d+)x?\\s+(.+)$");

    private final DeckRepository deckRepository;
    private final CardCachePort cardCachePort;
    private final CardSearchPort cardSearchPort;

    public ImportDeckService(DeckRepository deckRepository,
                             CardCachePort cardCachePort,
                             CardSearchPort cardSearchPort) {
        this.deckRepository = deckRepository;
        this.cardCachePort = cardCachePort;
        this.cardSearchPort = cardSearchPort;
    }

    @Override
    public ImportResult importFromList(Long deckId, String cardList, ImportProgressCallback onProgress) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new DeckNotFoundException(deckId));

        List<ParsedEntry> entries = parse(cardList);
        int total = entries.size();
        List<String> importedNames = new ArrayList<>();
        List<FailedCard> failedCards = new ArrayList<>();

        for (ParsedEntry entry : entries) {
            Optional<Card> resolved = resolveCard(entry.name());
            if (resolved.isPresent()) {
                Card card = resolved.get();
                addToDeck(deck, card, entry.quantity(), entry.sideboard());
                importedNames.add(card.name());
            } else {
                log.info("Could not resolve card '{}' during import for deck {}", entry.name(), deckId);
                String diag = cardSearchPort.lastDiagnostics();
                failedCards.add(new FailedCard(entry.name(), diag));
            }
            onProgress.onCardProcessed(importedNames.size() + failedCards.size(), total,
                    importedNames.size(), failedCards.size());
        }

        Deck saved = deckRepository.save(deck);
        return new ImportResult(saved, importedNames, failedCards);
    }

    // ── Parsing ─────────────────────────────────────────────────

    private List<ParsedEntry> parse(String raw) {
        if (raw == null || raw.isBlank()) return List.of();

        List<ParsedEntry> result = new ArrayList<>();
        boolean inSideboard = false;

        for (String line : raw.lines().toList()) {
            String t = line.trim();
            if (t.isEmpty() || t.startsWith("//") || t.startsWith("#")) continue;

            // Sideboard section marker
            if (t.equalsIgnoreCase("Sideboard:") || t.equalsIgnoreCase("Sideboard")) {
                inSideboard = true;
                continue;
            }

            boolean side = inSideboard;

            // Inline SB: prefix  e.g. "SB: 2 Searing Blood"
            if (t.toLowerCase().startsWith("sb:")) {
                side = true;
                t = t.substring(3).trim();
            }

            // Parse optional quantity
            int qty = 1;
            String name;
            Matcher m = QTY_PATTERN.matcher(t);
            if (m.matches()) {
                qty = Math.max(1, Integer.parseInt(m.group(1)));
                name = m.group(2).trim();
            } else {
                name = t;
            }

            if (!name.isEmpty()) {
                result.add(new ParsedEntry(name, qty, side));
            }
        }
        return result;
    }

    // ── Helpers ─────────────────────────────────────────────────

    private Optional<Card> resolveCard(String name) {
        return cardCachePort.findByName(name)
                .or(() -> cardSearchPort.findByName(name).map(cardCachePort::save));
    }

    private void addToDeck(Deck deck, Card card, int quantity, boolean sideboard) {
        deck.getEntries().stream()
                .filter(e -> e.getCard().scryfallId().equals(card.scryfallId())
                        && e.isSideboard() == sideboard)
                .findFirst()
                .ifPresentOrElse(
                        e -> e.setQuantity(e.getQuantity() + quantity),
                        () -> deck.getEntries().add(new DeckEntry(null, card, quantity, sideboard))
                );
    }

    private record ParsedEntry(String name, int quantity, boolean sideboard) {}
}
