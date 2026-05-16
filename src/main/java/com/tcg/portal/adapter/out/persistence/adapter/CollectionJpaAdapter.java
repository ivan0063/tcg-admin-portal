package com.tcg.portal.adapter.out.persistence.adapter;

import com.tcg.portal.adapter.out.persistence.entity.CollectionEntity;
import com.tcg.portal.adapter.out.persistence.jpa.CollectionJpaRepository;
import com.tcg.portal.adapter.out.persistence.mapper.CollectionMapper;
import com.tcg.portal.application.port.out.CollectionRepository;
import com.tcg.portal.domain.model.Collection;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

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
}
