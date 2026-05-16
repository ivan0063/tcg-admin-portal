package com.tcg.portal.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Deck {
    private Long id;
    private String name;
    private String description;
    private DeckFormat format;
    private LocalDateTime createdAt;
    private List<DeckEntry> entries;

    public Deck(Long id, String name, String description, DeckFormat format,
                LocalDateTime createdAt, List<DeckEntry> entries) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.format = format;
        this.createdAt = createdAt;
        this.entries = new ArrayList<>(entries);
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public DeckFormat getFormat() { return format; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<DeckEntry> getEntries() { return entries; }

    public List<DeckEntry> getMainboard() {
        return entries.stream().filter(e -> !e.isSideboard()).toList();
    }

    public List<DeckEntry> getSideboard() {
        return entries.stream().filter(DeckEntry::isSideboard).toList();
    }

    public int getMainboardCount() {
        return getMainboard().stream().mapToInt(DeckEntry::getQuantity).sum();
    }

    public int getSideboardCount() {
        return getSideboard().stream().mapToInt(DeckEntry::getQuantity).sum();
    }

    public List<String> getColorIdentity() {
        return entries.stream()
                .flatMap(e -> e.getCard().colorIdentity().stream())
                .distinct()
                .sorted()
                .toList();
    }
}
