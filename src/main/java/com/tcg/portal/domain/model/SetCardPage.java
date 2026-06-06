package com.tcg.portal.domain.model;

import java.util.List;

public record SetCardPage(List<Card> cards, boolean hasMore) {}
