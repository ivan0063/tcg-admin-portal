package com.tcg.portal.application.service;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ImportJobStore {

    private final ConcurrentHashMap<Long, ImportJob> jobs = new ConcurrentHashMap<>();

    public void markPending(Long deckId, int total) {
        jobs.put(deckId, ImportJob.pending(total));
    }

    public void markProgress(Long deckId, int processed, int total, int imported, int failed) {
        jobs.computeIfPresent(deckId, (k, existing) -> {
            if (existing.state() == ImportJob.State.DONE) return existing;
            return ImportJob.running(processed, total, imported, failed);
        });
    }

    public void markDone(Long deckId, int imported, int failed, List<String> cards) {
        jobs.put(deckId, ImportJob.done(imported, failed, cards));
    }

    /**
     * Returns the current job status.
     * If DONE, removes it from the store — the result is acknowledged exactly once.
     */
    public ImportJob pollStatus(Long deckId) {
        ImportJob job = jobs.get(deckId);
        if (job == null) return ImportJob.IDLE_JOB;
        if (job.state() == ImportJob.State.DONE) {
            jobs.remove(deckId);
        }
        return job;
    }
}
