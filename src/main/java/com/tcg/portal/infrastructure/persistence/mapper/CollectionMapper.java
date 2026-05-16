package com.tcg.portal.infrastructure.persistence.mapper;

import com.tcg.portal.infrastructure.persistence.entity.CollectionEntity;
import com.tcg.portal.infrastructure.persistence.entity.CollectionItemEntity;
import com.tcg.portal.domain.model.CardCondition;
import com.tcg.portal.domain.model.Collection;
import com.tcg.portal.domain.model.CollectionItem;

import java.util.ArrayList;
import java.util.List;

public final class CollectionMapper {

    private CollectionMapper() {}

    public static Collection toDomain(CollectionEntity e) {
        List<CollectionItem> items = e.getItems().stream()
                .map(CollectionMapper::itemToDomain)
                .toList();
        return new Collection(e.getId(), e.getName(), e.getDescription(), e.getCreatedAt(), items);
    }

    public static CollectionItem itemToDomain(CollectionItemEntity e) {
        return new CollectionItem(
                e.getId(),
                CardMapper.toDomain(e.getCard()),
                e.getQuantity(),
                CardCondition.valueOf(e.getCondition()),
                e.isFoil()
        );
    }

    public static CollectionEntity toEntity(Collection c) {
        CollectionEntity e = new CollectionEntity();
        e.setId(c.getId());
        e.setName(c.getName());
        e.setDescription(c.getDescription());
        e.setCreatedAt(c.getCreatedAt());

        List<CollectionItemEntity> itemEntities = new ArrayList<>();
        for (CollectionItem item : c.getItems()) {
            CollectionItemEntity ie = itemToEntity(item);
            ie.setCollection(e);
            itemEntities.add(ie);
        }
        e.setItems(itemEntities);
        return e;
    }

    private static CollectionItemEntity itemToEntity(CollectionItem item) {
        CollectionItemEntity ie = new CollectionItemEntity();
        ie.setId(item.getId());
        ie.setCard(CardMapper.toEntity(item.getCard()));
        ie.setQuantity(item.getQuantity());
        ie.setCondition(item.getCondition().name());
        ie.setFoil(item.isFoil());
        return ie;
    }
}
