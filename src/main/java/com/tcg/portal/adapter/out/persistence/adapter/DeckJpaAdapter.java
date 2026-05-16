package com.tcg.portal.adapter.out.persistence.adapter;

import com.tcg.portal.adapter.out.persistence.entity.DeckEntity;
import com.tcg.portal.adapter.out.persistence.jpa.DeckJpaRepository;
import com.tcg.portal.adapter.out.persistence.mapper.DeckMapper;
import com.tcg.portal.application.port.out.DeckRepository;
import com.tcg.portal.domain.model.Deck;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DeckJpaAdapter implements DeckRepository {

    private final DeckJpaRepository repository;

    public DeckJpaAdapter(DeckJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Deck> findAll() {
        return repository.findAll().stream()
                .map(DeckMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Deck> findById(Long id) {
        return repository.findById(id).map(DeckMapper::toDomain);
    }

    @Override
    public Deck save(Deck deck) {
        DeckEntity entity = DeckMapper.toEntity(deck);
        DeckEntity saved = repository.save(entity);
        return DeckMapper.toDomain(saved);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
