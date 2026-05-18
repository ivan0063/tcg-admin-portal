package com.tcg.portal.infrastructure.persistence.adapter;

import com.tcg.portal.application.port.out.ImportFailureRepository;
import com.tcg.portal.domain.model.FailedCard;
import com.tcg.portal.infrastructure.persistence.entity.DeckImportFailureEntity;
import com.tcg.portal.infrastructure.persistence.jpa.ImportFailureJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Transactional
public class ImportFailureJpaAdapter implements ImportFailureRepository {

    private final ImportFailureJpaRepository repo;

    public ImportFailureJpaAdapter(ImportFailureJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FailedCard> findFailuresByDeckId(Long deckId) {
        return repo.findByDeckIdOrderByFailedAtAsc(deckId).stream()
                .map(e -> new FailedCard(e.getCardName(), e.getDiagnostics()))
                .toList();
    }

    @Override
    public void saveFailures(Long deckId, List<FailedCard> cards) {
        LocalDateTime now = LocalDateTime.now();
        List<DeckImportFailureEntity> entities = cards.stream()
                .map(fc -> {
                    var e = new DeckImportFailureEntity();
                    e.setDeckId(deckId);
                    e.setCardName(fc.name());
                    e.setFailedAt(now);
                    e.setDiagnostics(fc.diagnosticsJson());
                    return e;
                })
                .toList();
        repo.saveAll(entities);
    }

    @Override
    public void clearByDeckId(Long deckId) {
        repo.deleteByDeckId(deckId);
    }
}
