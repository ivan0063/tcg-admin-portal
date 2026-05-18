package com.tcg.portal.infrastructure.persistence.adapter;

import com.tcg.portal.application.port.out.ImportFailureRepository;
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
    public List<String> findCardNamesByDeckId(Long deckId) {
        return repo.findByDeckIdOrderByFailedAtAsc(deckId).stream()
                .map(DeckImportFailureEntity::getCardName)
                .toList();
    }

    @Override
    public void saveFailures(Long deckId, List<String> cardNames) {
        LocalDateTime now = LocalDateTime.now();
        List<DeckImportFailureEntity> entities = cardNames.stream()
                .map(name -> {
                    var e = new DeckImportFailureEntity();
                    e.setDeckId(deckId);
                    e.setCardName(name);
                    e.setFailedAt(now);
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
