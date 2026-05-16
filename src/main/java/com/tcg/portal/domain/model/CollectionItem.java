package com.tcg.portal.domain.model;

public class CollectionItem {
    private Long id;
    private Card card;
    private int quantity;
    private CardCondition condition;
    private boolean foil;

    public CollectionItem(Long id, Card card, int quantity, CardCondition condition, boolean foil) {
        this.id = id;
        this.card = card;
        this.quantity = quantity;
        this.condition = condition;
        this.foil = foil;
    }

    public Long getId() { return id; }
    public Card getCard() { return card; }
    public int getQuantity() { return quantity; }
    public CardCondition getCondition() { return condition; }
    public boolean isFoil() { return foil; }

    public void setQuantity(int quantity) { this.quantity = quantity; }
}
