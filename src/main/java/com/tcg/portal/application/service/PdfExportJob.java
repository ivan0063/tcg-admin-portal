package com.tcg.portal.application.service;

public record PdfExportJob(State state, byte[] pdfBytes, String error) {

    public enum State { PENDING, DONE, ERROR, IDLE }

    static final PdfExportJob IDLE = new PdfExportJob(State.IDLE, null, null);

    static PdfExportJob pending() {
        return new PdfExportJob(State.PENDING, null, null);
    }

    static PdfExportJob done(byte[] pdf) {
        return new PdfExportJob(State.DONE, pdf, null);
    }

    static PdfExportJob error(String msg) {
        return new PdfExportJob(State.ERROR, null, msg);
    }
}
