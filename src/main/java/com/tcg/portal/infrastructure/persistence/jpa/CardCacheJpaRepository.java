package com.tcg.portal.infrastructure.persistence.jpa;

import com.tcg.portal.infrastructure.persistence.entity.CardCacheEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardCacheJpaRepository extends JpaRepository<CardCacheEntity, String> {
}
