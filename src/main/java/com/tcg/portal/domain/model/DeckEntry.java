package com.tcg.portal.domain.model;

public class DeckEntry {
    private Long id;
    private Card card;
    private int quantity;
    private boolean sideboard;

    public DeckEntry(Long id, Card card, int quantity, boolean sideboard) {
        this.id = id;
        this.card = card;
        this.quantity = quantity;
        this.sideboard = sideboard;
    }

    public Long getId() { return id; }
    public Card getCard() { return card; }
    public int getQuantity() { return quantity; }
    public boolean isSideboard() { return sideboard; }

    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setSideboard(boolean sideboard) { this.sideboard = sideboard; }
}
