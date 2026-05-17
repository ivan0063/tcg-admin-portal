package com.tcg.portal.application.service;

import java.util.List;

public record ImportJob(State state, int importedCount, int failedCount, List<String> failedCards) {

    public enum State { PENDING, DONE, IDLE }

    static final ImportJob IDLE_JOB = new ImportJob(State.IDLE, 0, 0, List.of());

    static ImportJob pending() {
        return new ImportJob(State.PENDING, 0, 0, List.of());
    }

    static ImportJob done(int imported, int failed, List<String> cards) {
        return new ImportJob(State.DONE, imported, failed, List.copyOf(cards));
    }
}
