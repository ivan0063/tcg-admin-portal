package com.tcg.portal.adapter.out.persistence.jpa;

import com.tcg.portal.adapter.out.persistence.entity.DeckEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeckJpaRepository extends JpaRepository<DeckEntity, Long> {
}
