package com.tcg.portal.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "card_cache")
public class CardCacheEntity {

    @Id
    @Column(name = "scryfall_id")
    private String scryfallId;

    @Column(nullable = false)
    private String name;

    @Column(name = "mana_cost")
    private String manaCost;

    private double cmc;

    @Column(name = "type_line", length = 500)
    private String typeLine;

    @Column(name = "oracle_text", length = 2000)
    private String oracleText;

    private String colors;

    @Column(name = "color_identity")
    private String colorIdentity;

    private String rarity;

    @Column(name = "set_code")
    private String setCode;

    @Column(name = "set_name")
    private String setName;

    @Column(name = "image_uri", length = 1000)
    private String imageUri;

    @Column(name = "small_image_uri", length = 1000)
    private String smallImageUri;

    @Column(name = "usd_price", precision = 10, scale = 2)
    private BigDecimal usdPrice;

    @Column(name = "usd_foil_price", precision = 10, scale = 2)
    private BigDecimal usdFoilPrice;

    public CardCacheEntity() {}

    public String getScryfallId() { return scryfallId; }
    public void setScryfallId(String scryfallId) { this.scryfallId = scryfallId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getManaCost() { return manaCost; }
    public void setManaCost(String manaCost) { this.manaCost = manaCost; }
    public double getCmc() { return cmc; }
    public void setCmc(double cmc) { this.cmc = cmc; }
    public String getTypeLine() { return typeLine; }
    public void setTypeLine(String typeLine) { this.typeLine = typeLine; }
    public String getOracleText() { return oracleText; }
    public void setOracleText(String oracleText) { this.oracleText = oracleText; }
    public String getColors() { return colors; }
    public void setColors(String colors) { this.colors = colors; }
    public String getColorIdentity() { return colorIdentity; }
    public void setColorIdentity(String colorIdentity) { this.colorIdentity = colorIdentity; }
    public String getRarity() { return rarity; }
    public void setRarity(String rarity) { this.rarity = rarity; }
    public String getSetCode() { return setCode; }
    public void setSetCode(String setCode) { this.setCode = setCode; }
    public String getSetName() { return setName; }
    public void setSetName(String setName) { this.setName = setName; }
    public String getImageUri() { return imageUri; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }
    public String getSmallImageUri() { return smallImageUri; }
    public void setSmallImageUri(String smallImageUri) { this.smallImageUri = smallImageUri; }
    public BigDecimal getUsdPrice() { return usdPrice; }
    public void setUsdPrice(BigDecimal usdPrice) { this.usdPrice = usdPrice; }
    public BigDecimal getUsdFoilPrice() { return usdFoilPrice; }
    public void setUsdFoilPrice(BigDecimal usdFoilPrice) { this.usdFoilPrice = usdFoilPrice; }
}
