package com.tcg.portal.application.port.out;

import com.tcg.portal.domain.model.Collection;

import java.util.List;
import java.util.Optional;

public interface CollectionRepository {
    List<Collection> findAll();
    Optional<Collection> findById(Long id);
    Collection save(Collection collection);
    void deleteById(Long id);
}
