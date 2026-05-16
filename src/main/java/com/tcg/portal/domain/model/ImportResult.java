package com.tcg.portal.domain.model;

import java.util.List;

public record ImportResult(
        Deck deck,
        List<String> importedNames,
        List<String> failedNames
) {
    public boolean hasFailures() { return !failedNames.isEmpty(); }
    public int importedCount()   { return importedNames.size(); }
    public int failedCount()     { return failedNames.size(); }
}
