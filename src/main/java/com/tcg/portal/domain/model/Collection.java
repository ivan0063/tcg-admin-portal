package com.tcg.portal.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Collection {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private List<CollectionItem> items;

    public Collection(Long id, String name, String description, LocalDateTime createdAt, List<CollectionItem> items) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.items = new ArrayList<>(items);
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<CollectionItem> getItems() { return items; }

    public int getTotalCards() {
        return items.stream().mapToInt(CollectionItem::getQuantity).sum();
    }

    public int getDistinctCards() {
        return items.size();
    }

    public BigDecimal getTotalValue() {
        return items.stream()
                .map(item -> {
                    BigDecimal price = item.isFoil()
                            ? item.getCard().usdFoilPrice()
                            : item.getCard().usdPrice();
                    if (price == null) return BigDecimal.ZERO;
                    return price.multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
