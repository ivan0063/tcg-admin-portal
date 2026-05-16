package com.tcg.portal.application.port.in;

import com.tcg.portal.domain.model.CardCondition;
import com.tcg.portal.domain.model.Collection;

import java.util.List;

public interface ManageCollectionUseCase {
    List<Collection> getAllCollections();
    Collection getCollection(Long id);
    Collection createCollection(String name, String description);
    Collection addCard(Long collectionId, String scryfallId, int quantity, CardCondition condition, boolean foil);
    Collection removeCard(Long collectionId, Long itemId);
    Collection updateItemQuantity(Long collectionId, Long itemId, int quantity);
    void deleteCollection(Long id);
}
