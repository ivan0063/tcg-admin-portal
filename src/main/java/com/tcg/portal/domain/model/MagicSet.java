package com.tcg.portal.domain.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record MagicSet(
        String code,
        String name,
        String setType,
        LocalDate releasedAt,
        int cardCount,
        String iconSvgUri
) {
    private static final DateTimeFormatter MONTH_YEAR = DateTimeFormatter.ofPattern("MMM yyyy");
    private static final DateTimeFormatter FULL = DateTimeFormatter.ofPattern("MMMM d, yyyy");

    public String releasedAtShort() {
        return releasedAt != null ? releasedAt.format(MONTH_YEAR) : "";
    }

    public String releasedAtFull() {
        return releasedAt != null ? releasedAt.format(FULL) : "";
    }
}
