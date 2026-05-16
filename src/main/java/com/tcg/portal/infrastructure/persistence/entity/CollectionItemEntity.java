package com.tcg.portal.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "collection_item")
public class CollectionItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id", nullable = false)
    private CollectionEntity collection;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "card_id", nullable = false)
    private CardCacheEntity card;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private String condition;

    @Column(nullable = false)
    private boolean foil;

    public CollectionItemEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public CollectionEntity getCollection() { return collection; }
    public void setCollection(CollectionEntity collection) { this.collection = collection; }
    public CardCacheEntity getCard() { return card; }
    public void setCard(CardCacheEntity card) { this.card = card; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public boolean isFoil() { return foil; }
    public void setFoil(boolean foil) { this.foil = foil; }
}
