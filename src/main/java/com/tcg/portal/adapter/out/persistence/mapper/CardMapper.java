package com.tcg.portal.adapter.out.persistence.mapper;

import com.tcg.portal.adapter.out.persistence.entity.CardCacheEntity;
import com.tcg.portal.domain.model.Card;

import java.util.Arrays;
import java.util.List;

public final class CardMapper {

    private CardMapper() {}

    public static Card toDomain(CardCacheEntity e) {
        return new Card(
                e.getScryfallId(),
                e.getName(),
                e.getManaCost(),
                e.getCmc(),
                e.getTypeLine(),
                e.getOracleText(),
                parseList(e.getColors()),
                parseList(e.getColorIdentity()),
                e.getRarity(),
                e.getSetCode(),
                e.getSetName(),
                e.getImageUri(),
                e.getSmallImageUri(),
                e.getUsdPrice(),
                e.getUsdFoilPrice()
        );
    }

    public static CardCacheEntity toEntity(Card c) {
        CardCacheEntity e = new CardCacheEntity();
        e.setScryfallId(c.scryfallId());
        e.setName(c.name());
        e.setManaCost(c.manaCost());
        e.setCmc(c.cmc());
        e.setTypeLine(c.typeLine());
        e.setOracleText(c.oracleText());
        e.setColors(joinList(c.colors()));
        e.setColorIdentity(joinList(c.colorIdentity()));
        e.setRarity(c.rarity());
        e.setSetCode(c.setCode());
        e.setSetName(c.setName());
        e.setImageUri(c.imageUri());
        e.setSmallImageUri(c.smallImageUri());
        e.setUsdPrice(c.usdPrice());
        e.setUsdFoilPrice(c.usdFoilPrice());
        return e;
    }

    private static List<String> parseList(String csv) {
        if (csv == null || csv.isBlank()) return List.of();
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    private static String joinList(List<String> list) {
        if (list == null || list.isEmpty()) return "";
        return String.join(",", list);
    }
}
