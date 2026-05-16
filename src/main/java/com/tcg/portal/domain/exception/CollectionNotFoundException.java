package com.tcg.portal.domain.exception;

public class CollectionNotFoundException extends RuntimeException {
    public CollectionNotFoundException(Long id) {
        super("Collection not found: " + id);
    }
}
