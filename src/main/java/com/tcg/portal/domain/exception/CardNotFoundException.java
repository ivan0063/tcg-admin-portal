package com.tcg.portal.domain.exception;

public class CardNotFoundException extends RuntimeException {
    public CardNotFoundException(String scryfallId) {
        super("Card not found in Scryfall: " + scryfallId);
    }
}
