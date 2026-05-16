package com.tcg.portal.infrastructure.persistence.adapter;

import com.tcg.portal.infrastructure.persistence.entity.CardCacheEntity;
import com.tcg.portal.infrastructure.persistence.jpa.CardCacheJpaRepository;
import com.tcg.portal.infrastructure.persistence.mapper.CardMapper;
import com.tcg.portal.application.port.out.CardCachePort;
import com.tcg.portal.domain.model.Card;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CardCacheJpaAdapter implements CardCachePort {

    private final CardCacheJpaRepository repository;

    public CardCacheJpaAdapter(CardCacheJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Card> findById(String scryfallId) {
        return repository.findById(scryfallId).map(CardMapper::toDomain);
    }

    @Override
    public Optional<Card> findByName(String name) {
        return repository.findByNameIgnoreCase(name).map(CardMapper::toDomain);
    }

    @Override
    public Card save(Card card) {
        CardCacheEntity entity = CardMapper.toEntity(card);
        CardCacheEntity saved = repository.save(entity);
        return CardMapper.toDomain(saved);
    }
}
