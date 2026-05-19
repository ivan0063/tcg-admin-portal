package com.tcg.portal.infrastructure.persistence.jpa;

import com.tcg.portal.infrastructure.persistence.entity.CollectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface CollectionJpaRepository extends JpaRepository<CollectionEntity, Long> {

    @Query("SELECT DISTINCT ci.card.scryfallId FROM CollectionItemEntity ci")
    Set<String> findAllOwnedScryfallIds();
}
