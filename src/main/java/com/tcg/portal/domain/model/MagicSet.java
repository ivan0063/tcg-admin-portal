package com.tcg.portal.domain.model;

import java.time.LocalDate;

public record MagicSet(
        String code,
        String name,
        String setType,
        LocalDate releasedAt,
        int cardCount,
        String iconSvgUri
) {}
