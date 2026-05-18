package com.tcg.portal.infrastructure.persistence.jpa;

import com.tcg.portal.infrastructure.persistence.entity.DeckImportFailureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ImportFailureJpaRepository extends JpaRepository<DeckImportFailureEntity, Long> {
    List<DeckImportFailureEntity> findByDeckIdOrderByFailedAtAsc(Long deckId);

    @Transactional
    void deleteByDeckId(Long deckId);
}
