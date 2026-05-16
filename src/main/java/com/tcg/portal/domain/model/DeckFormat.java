package com.tcg.portal.domain.model;

public enum DeckFormat {
    STANDARD("Standard"),
    PIONEER("Pioneer"),
    MODERN("Modern"),
    LEGACY("Legacy"),
    VINTAGE("Vintage"),
    COMMANDER("Commander"),
    PAUPER("Pauper"),
    DRAFT("Draft"),
    SEALED("Sealed"),
    OTHER("Other");

    private final String label;

    DeckFormat(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
