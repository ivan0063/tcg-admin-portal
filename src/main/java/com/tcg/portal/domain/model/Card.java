package com.tcg.portal.domain.model;

import java.math.BigDecimal;
import java.util.List;

public record Card(
        String scryfallId,
        String name,
        String manaCost,
        double cmc,
        String typeLine,
        String oracleText,
        List<String> colors,
        List<String> colorIdentity,
        String rarity,
        String setCode,
        String setName,
        String imageUri,
        String smallImageUri,
        BigDecimal usdPrice,
        BigDecimal usdFoilPrice
) {
    public String displayPrice() {
        if (usdPrice != null) return "$" + usdPrice.toPlainString();
        return "N/A";
    }

    public String manaCostDisplay() {
        return manaCost != null ? manaCost : "";
    }
}
