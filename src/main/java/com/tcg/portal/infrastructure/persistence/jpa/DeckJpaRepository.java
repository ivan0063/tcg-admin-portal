package com.tcg.portal.infrastructure.persistence.jpa;

import com.tcg.portal.infrastructure.persistence.entity.DeckEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeckJpaRepository extends JpaRepository<DeckEntity, Long> {
}
