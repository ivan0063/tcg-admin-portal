package com.tcg.portal.application.service;

import com.tcg.portal.application.port.in.ManageCollectionUseCase;
import com.tcg.portal.application.port.out.CardCachePort;
import com.tcg.portal.application.port.out.CardSearchPort;
import com.tcg.portal.application.port.out.CollectionRepository;
import com.tcg.portal.domain.exception.CardNotFoundException;
import com.tcg.portal.domain.exception.CollectionNotFoundException;
import com.tcg.portal.domain.model.Card;
import com.tcg.portal.domain.model.CardCondition;
import com.tcg.portal.domain.model.Collection;
import com.tcg.portal.domain.model.CollectionItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CollectionService implements ManageCollectionUseCase {

    private final CollectionRepository collectionRepository;
    private final CardCachePort cardCachePort;
    private final CardSearchPort cardSearchPort;

    public CollectionService(CollectionRepository collectionRepository,
                             CardCachePort cardCachePort,
                             CardSearchPort cardSearchPort) {
        this.collectionRepository = collectionRepository;
        this.cardCachePort = cardCachePort;
        this.cardSearchPort = cardSearchPort;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Collection> getAllCollections() {
        return collectionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Collection getCollection(Long id) {
        return collectionRepository.findById(id)
                .orElseThrow(() -> new CollectionNotFoundException(id));
    }

    @Override
    public Collection createCollection(String name, String description) {
        Collection collection = new Collection(null, name, description, LocalDateTime.now(), new ArrayList<>());
        return collectionRepository.save(collection);
    }

    @Override
    public Collection addCard(Long collectionId, String scryfallId, int quantity,
                              CardCondition condition, boolean foil) {
        Collection collection = getCollection(collectionId);
        Card card = resolveCard(scryfallId);

        collection.getItems().stream()
                .filter(i -> i.getCard().scryfallId().equals(scryfallId)
                        && i.getCondition() == condition && i.isFoil() == foil)
                .findFirst()
                .ifPresentOrElse(
                        existing -> existing.setQuantity(existing.getQuantity() + quantity),
                        () -> collection.getItems().add(new CollectionItem(null, card, quantity, condition, foil))
                );

        return collectionRepository.save(collection);
    }

    @Override
    public Collection removeCard(Long collectionId, Long itemId) {
        Collection collection = getCollection(collectionId);
        collection.getItems().removeIf(i -> i.getId().equals(itemId));
        return collectionRepository.save(collection);
    }

    @Override
    public Collection updateItemQuantity(Long collectionId, Long itemId, int quantity) {
        Collection collection = getCollection(collectionId);
        collection.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .ifPresent(i -> {
                    if (quantity <= 0) collection.getItems().remove(i);
                    else i.setQuantity(quantity);
                });
        return collectionRepository.save(collection);
    }

    @Override
    public void deleteCollection(Long id) {
        collectionRepository.deleteById(id);
    }

    private Card resolveCard(String scryfallId) {
        return cardCachePort.findById(scryfallId)
                .orElseGet(() -> {
                    Card fetched = cardSearchPort.findById(scryfallId)
                            .orElseThrow(() -> new CardNotFoundException(scryfallId));
                    return cardCachePort.save(fetched);
                });
    }
}
