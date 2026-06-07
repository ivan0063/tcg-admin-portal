package com.tcg.portal.application.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.tcg.portal.domain.model.Card;
import com.tcg.portal.domain.model.Collection;
import com.tcg.portal.domain.model.CollectionItem;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
public class CollectionPdfExportService {

    private static final Color COLOR_HEADER_BG   = new Color(0x1a, 0x1a, 0x2e);
    private static final Color COLOR_HEADER_TEXT  = new Color(0xf0, 0xe6, 0xff);
    private static final Color COLOR_ROW_ALT      = new Color(0xf5, 0xf0, 0xff);
    private static final Color COLOR_BORDER       = new Color(0xcc, 0xbb, 0xee);
    private static final Color COLOR_TITLE        = new Color(0x2d, 0x06, 0x52);
    private static final Color COLOR_SUB          = new Color(0x66, 0x44, 0x88);
    private static final Color COLOR_BODY         = new Color(0x22, 0x22, 0x22);
    private static final Color COLOR_ORACLE       = new Color(0x44, 0x33, 0x55);

    private static final Font FONT_TITLE   = new Font(Font.HELVETICA, 18, Font.BOLD,   COLOR_TITLE);
    private static final Font FONT_SUB     = new Font(Font.HELVETICA, 10, Font.NORMAL, COLOR_SUB);
    private static final Font FONT_HEADER  = new Font(Font.HELVETICA, 9,  Font.BOLD,   COLOR_HEADER_TEXT);
    private static final Font FONT_NAME    = new Font(Font.HELVETICA, 8,  Font.BOLD,   COLOR_BODY);
    private static final Font FONT_BODY    = new Font(Font.HELVETICA, 8,  Font.NORMAL, COLOR_BODY);
    private static final Font FONT_ORACLE  = new Font(Font.HELVETICA, 7,  Font.ITALIC, COLOR_ORACLE);

    /** Export a personal collection (includes Qty column). */
    public byte[] exportCollection(Collection collection) {
        List<CollectionItem> items = collection.getItems().stream()
                .sorted(Comparator.comparing(i -> i.getCard().name()))
                .toList();
        String subtitle = collection.getTotalCards() + " cards  ·  "
                + collection.getDistinctCards() + " distinct  ·  exported "
                + today();
        if (collection.getDescription() != null && !collection.getDescription().isBlank()) {
            subtitle = collection.getDescription() + "  ·  " + subtitle;
        }
        return buildDocument(collection.getName(), subtitle, () -> {
            if (items.isEmpty()) return new Paragraph("No cards in this collection.", FONT_BODY);
            return buildCollectionTable(items);
        });
    }

    /** Export a flat card list (e.g. all cards in a Magic set — no Qty column). */
    public byte[] exportCardList(String title, String subtitle, List<Card> cards) {
        List<Card> sorted = cards.stream()
                .sorted(Comparator.comparing(Card::name))
                .toList();
        return buildDocument(title, subtitle, () -> {
            if (sorted.isEmpty()) return new Paragraph("No cards found.", FONT_BODY);
            return buildSetTable(sorted);
        });
    }

    // ── private helpers ──────────────────────────────────────────────────────

    private byte[] buildDocument(String title, String subtitle, ElementSupplier body) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 36, 36, 40, 36);
            PdfWriter.getInstance(doc, out);
            doc.open();
            doc.add(new Paragraph(title, FONT_TITLE));
            doc.add(new Paragraph(subtitle, FONT_SUB));
            doc.add(Chunk.NEWLINE);
            doc.add(body.get());
            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed: " + e.getMessage(), e);
        }
    }

    /** Qty | Card Name | Type | Mana Cost | P/T | Oracle Text */
    private PdfPTable buildCollectionTable(List<CollectionItem> items) throws DocumentException {
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(4);
        table.setWidths(new float[]{1f, 7f, 6f, 3.5f, 2f, 14f});

        addHeaderCell(table, "Qty");
        addHeaderCell(table, "Card Name");
        addHeaderCell(table, "Type");
        addHeaderCell(table, "Mana Cost");
        addHeaderCell(table, "P/T");
        addHeaderCell(table, "Oracle Text");

        boolean alt = false;
        for (CollectionItem item : items) {
            Color rowBg = alt ? COLOR_ROW_ALT : Color.WHITE;
            alt = !alt;
            addBodyCell(table, String.valueOf(item.getQuantity()), rowBg, FONT_BODY);
            addBodyCell(table, nullSafe(item.getCard().name()), rowBg, FONT_NAME);
            addBodyCell(table, nullSafe(item.getCard().typeLine()), rowBg, FONT_BODY);
            addBodyCell(table, nullSafe(item.getCard().manaCost()), rowBg, FONT_BODY);
            String pt = item.getCard().powerToughness();
            addBodyCell(table, pt != null ? pt : "—", rowBg, FONT_BODY);
            addOracleCell(table, nullSafe(item.getCard().oracleText()), rowBg);
        }
        return table;
    }

    /** Card Name | Type | Mana Cost | P/T | Oracle Text (no Qty) */
    private PdfPTable buildSetTable(List<Card> cards) throws DocumentException {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(4);
        table.setWidths(new float[]{7f, 6f, 3.5f, 2f, 16f});

        addHeaderCell(table, "Card Name");
        addHeaderCell(table, "Type");
        addHeaderCell(table, "Mana Cost");
        addHeaderCell(table, "P/T");
        addHeaderCell(table, "Oracle Text");

        boolean alt = false;
        for (Card card : cards) {
            Color rowBg = alt ? COLOR_ROW_ALT : Color.WHITE;
            alt = !alt;
            addBodyCell(table, nullSafe(card.name()), rowBg, FONT_NAME);
            addBodyCell(table, nullSafe(card.typeLine()), rowBg, FONT_BODY);
            addBodyCell(table, nullSafe(card.manaCost()), rowBg, FONT_BODY);
            String pt = card.powerToughness();
            addBodyCell(table, pt != null ? pt : "—", rowBg, FONT_BODY);
            addOracleCell(table, nullSafe(card.oracleText()), rowBg);
        }
        return table;
    }

    private void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FONT_HEADER));
        cell.setBackgroundColor(COLOR_HEADER_BG);
        cell.setBorderColor(COLOR_BORDER);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private void addBodyCell(PdfPTable table, String text, Color bg, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setBorderColor(COLOR_BORDER);
        cell.setPadding(4);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    private void addOracleCell(PdfPTable table, String text, Color bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FONT_ORACLE));
        cell.setBackgroundColor(bg);
        cell.setBorderColor(COLOR_BORDER);
        cell.setPadding(4);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    private String nullSafe(String s) { return s != null ? s : ""; }
    private String today() { return LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE); }

    @FunctionalInterface
    private interface ElementSupplier {
        Element get() throws DocumentException;
    }
}
