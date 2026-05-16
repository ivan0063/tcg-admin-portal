package com.tcg.portal.adapter.out.persistence.jpa;

import com.tcg.portal.adapter.out.persistence.entity.CardCacheEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardCacheJpaRepository extends JpaRepository<CardCacheEntity, String> {
}
