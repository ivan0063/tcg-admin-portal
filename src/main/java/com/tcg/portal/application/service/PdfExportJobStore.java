package com.tcg.portal.application.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class PdfExportJobStore {

    private final ConcurrentHashMap<Long, PdfExportJob> jobs = new ConcurrentHashMap<>();

    public void markPending(Long deckId) {
        jobs.put(deckId, PdfExportJob.pending());
    }

    public void markDone(Long deckId, byte[] pdf) {
        jobs.put(deckId, PdfExportJob.done(pdf));
    }

    public void markError(Long deckId, String message) {
        jobs.put(deckId, PdfExportJob.error(message));
    }

    /** Returns current status; removes DONE/ERROR after first poll. */
    public PdfExportJob pollStatus(Long deckId) {
        PdfExportJob job = jobs.get(deckId);
        if (job == null) return PdfExportJob.IDLE;
        if (job.state() == PdfExportJob.State.DONE || job.state() == PdfExportJob.State.ERROR) {
            jobs.remove(deckId);
        }
        return job;
    }

    /** Retrieves PDF bytes without removing the job — used by the download endpoint. */
    public PdfExportJob getAndRemove(Long deckId) {
        return jobs.remove(deckId);
    }
}
