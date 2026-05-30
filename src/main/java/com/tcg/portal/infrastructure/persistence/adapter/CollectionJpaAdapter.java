package com.tcg.portal.infrastructure.persistence.adapter;

import com.tcg.portal.infrastructure.persistence.entity.CollectionEntity;
import com.tcg.portal.infrastructure.persistence.jpa.CollectionJpaRepository;
import com.tcg.portal.infrastructure.persistence.mapper.CollectionMapper;
import com.tcg.portal.application.port.out.CollectionRepository;
import com.tcg.portal.domain.model.Collection;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
public class CollectionJpaAdapter implements CollectionRepository {

    private final CollectionJpaRepository repository;

    public CollectionJpaAdapter(CollectionJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Collection> findAll() {
        return repository.findAll().stream()
                .map(CollectionMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Collection> findById(Long id) {
        return repository.findById(id).map(CollectionMapper::toDomain);
    }

    @Override
    public Collection save(Collection collection) {
        CollectionEntity entity = CollectionMapper.toEntity(collection);
        CollectionEntity saved = repository.save(entity);
        return CollectionMapper.toDomain(saved);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Set<String> findAllOwnedScryfallIds() {
        return repository.findAllOwnedScryfallIds();
    }

    @Override
    public Map<String, Integer> getGlobalInventoryByCard() {
        Map<String, Integer> inventory = new HashMap<>();
        for (Object[] row : repository.findTotalQuantityPerCard()) {
            inventory.put((String) row[0], ((Number) row[1]).intValue());
        }
        return inventory;
    }
}
