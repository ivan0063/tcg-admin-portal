package com.tcg.portal.domain.exception;

public class SetNotFoundException extends RuntimeException {
    public SetNotFoundException(String setCode) {
        super("Set not found: " + setCode);
    }
}
