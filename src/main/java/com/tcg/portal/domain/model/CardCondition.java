package com.tcg.portal.domain.model;

public enum CardCondition {
    MINT("Mint"),
    NEAR_MINT("Near Mint"),
    LIGHTLY_PLAYED("Lightly Played"),
    MODERATELY_PLAYED("Moderately Played"),
    HEAVILY_PLAYED("Heavily Played"),
    DAMAGED("Damaged");

    private final String label;

    CardCondition(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
