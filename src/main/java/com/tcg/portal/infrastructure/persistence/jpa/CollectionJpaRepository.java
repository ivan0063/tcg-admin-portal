package com.tcg.portal.infrastructure.persistence.jpa;

import com.tcg.portal.infrastructure.persistence.entity.CollectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectionJpaRepository extends JpaRepository<CollectionEntity, Long> {
}
