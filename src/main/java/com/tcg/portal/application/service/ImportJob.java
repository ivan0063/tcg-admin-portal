package com.tcg.portal.application.service;

import java.util.List;

public record ImportJob(State state, int processed, int total, int importedCount, int failedCount, List<String> failedCards) {

    public enum State { PENDING, DONE, IDLE }

    static final ImportJob IDLE_JOB = new ImportJob(State.IDLE, 0, 0, 0, 0, List.of());

    static ImportJob pending(int total) {
        return new ImportJob(State.PENDING, 0, total, 0, 0, List.of());
    }

    static ImportJob running(int processed, int total, int imported, int failed) {
        return new ImportJob(State.PENDING, processed, total, imported, failed, List.of());
    }

    static ImportJob done(int imported, int failed, List<String> cards) {
        return new ImportJob(State.DONE, imported + failed, imported + failed, imported, failed, List.copyOf(cards));
    }
}
