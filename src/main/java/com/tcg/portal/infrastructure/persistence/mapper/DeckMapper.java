package com.tcg.portal.infrastructure.persistence.mapper;

import com.tcg.portal.infrastructure.persistence.entity.DeckEntity;
import com.tcg.portal.infrastructure.persistence.entity.DeckEntryEntity;
import com.tcg.portal.domain.model.Deck;
import com.tcg.portal.domain.model.DeckEntry;
import com.tcg.portal.domain.model.DeckFormat;

import java.util.ArrayList;
import java.util.List;

public final class DeckMapper {

    private DeckMapper() {}

    public static Deck toDomain(DeckEntity e) {
        List<DeckEntry> entries = e.getEntries().stream()
                .map(DeckMapper::entryToDomain)
                .toList();
        return new Deck(
                e.getId(),
                e.getName(),
                e.getDescription(),
                DeckFormat.valueOf(e.getFormat()),
                e.getCreatedAt(),
                entries
        );
    }

    public static DeckEntry entryToDomain(DeckEntryEntity e) {
        return new DeckEntry(e.getId(), CardMapper.toDomain(e.getCard()), e.getQuantity(), e.isSideboard());
    }

    public static DeckEntity toEntity(Deck d) {
        DeckEntity e = new DeckEntity();
        e.setId(d.getId());
        e.setName(d.getName());
        e.setDescription(d.getDescription());
        e.setFormat(d.getFormat().name());
        e.setCreatedAt(d.getCreatedAt());

        List<DeckEntryEntity> entryEntities = new ArrayList<>();
        for (DeckEntry entry : d.getEntries()) {
            DeckEntryEntity ee = entryToEntity(entry);
            ee.setDeck(e);
            entryEntities.add(ee);
        }
        e.setEntries(entryEntities);
        return e;
    }

    private static DeckEntryEntity entryToEntity(DeckEntry entry) {
        DeckEntryEntity ee = new DeckEntryEntity();
        ee.setId(entry.getId());
        ee.setCard(CardMapper.toEntity(entry.getCard()));
        ee.setQuantity(entry.getQuantity());
        ee.setSideboard(entry.isSideboard());
        return ee;
    }
}
