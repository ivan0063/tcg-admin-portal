package com.tcg.portal.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "deck_entry")
public class DeckEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", nullable = false)
    private DeckEntity deck;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "card_id", nullable = false)
    private CardCacheEntity card;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private boolean sideboard;

    public DeckEntryEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public DeckEntity getDeck() { return deck; }
    public void setDeck(DeckEntity deck) { this.deck = deck; }
    public CardCacheEntity getCard() { return card; }
    public void setCard(CardCacheEntity card) { this.card = card; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public boolean isSideboard() { return sideboard; }
    public void setSideboard(boolean sideboard) { this.sideboard = sideboard; }
}
