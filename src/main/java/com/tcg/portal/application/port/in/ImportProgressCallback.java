package com.tcg.portal.application.port.in;

@FunctionalInterface
public interface ImportProgressCallback {
    void onCardProcessed(int processed, int total, int imported, int failed);
}
