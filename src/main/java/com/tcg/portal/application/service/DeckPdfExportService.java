package com.tcg.portal.application.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.tcg.portal.domain.model.Deck;
import com.tcg.portal.domain.model.DeckEntry;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class DeckPdfExportService {

    private static final Color COLOR_HEADER_BG  = new Color(0x1a, 0x1a, 0x2e);
    private static final Color COLOR_HEADER_TEXT = new Color(0xf0, 0xe6, 0xff);
    private static final Color COLOR_ROW_ALT     = new Color(0xf5, 0xf0, 0xff);
    private static final Color COLOR_SECTION_BG  = new Color(0x3d, 0x1a, 0x6e);
    private static final Color COLOR_SECTION_TEXT= new Color(0xff, 0xff, 0xff);
    private static final Color COLOR_BORDER      = new Color(0xcc, 0xbb, 0xee);

    private static final Font FONT_TITLE   = new Font(Font.HELVETICA, 18, Font.BOLD,  new Color(0x2d, 0x06, 0x52));
    private static final Font FONT_SUB     = new Font(Font.HELVETICA, 10, Font.NORMAL,new Color(0x66, 0x44, 0x88));
    private static final Font FONT_HEADER  = new Font(Font.HELVETICA, 9,  Font.BOLD,  COLOR_HEADER_TEXT);
    private static final Font FONT_SECTION = new Font(Font.HELVETICA, 10, Font.BOLD,  COLOR_SECTION_TEXT);
    private static final Font FONT_BODY    = new Font(Font.HELVETICA, 8,  Font.NORMAL,new Color(0x22, 0x22, 0x22));
    private static final Font FONT_ORACLE  = new Font(Font.HELVETICA, 7,  Font.ITALIC,new Color(0x44, 0x33, 0x55));

    public byte[] generatePdf(Deck deck, Map<String, byte[]> imageCache) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4.rotate(), 30, 30, 36, 36);
            PdfWriter.getInstance(doc, out);
            doc.open();

            addHeader(doc, deck);
            doc.add(Chunk.NEWLINE);

            if (!deck.getMainboard().isEmpty()) {
                addSectionBanner(doc, "Mainboard — " + deck.getMainboardCount() + " cards");
                doc.add(buildCardTable(deck.getMainboard(), imageCache));
            }

            if (!deck.getSideboard().isEmpty()) {
                doc.add(Chunk.NEWLINE);
                addSectionBanner(doc, "Sideboard — " + deck.getSideboardCount() + " cards");
                doc.add(buildCardTable(deck.getSideboard(), imageCache));
            }

            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed: " + e.getMessage(), e);
        }
    }

    private void addHeader(Document doc, Deck deck) throws DocumentException {
        doc.add(new Paragraph(deck.getName(), FONT_TITLE));
        String sub = deck.getFormat().getLabel()
                + "   •   Generated " + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        if (!deck.getColorIdentity().isEmpty()) {
            sub += "   •   Colors: " + String.join(" ", deck.getColorIdentity());
        }
        doc.add(new Paragraph(sub, FONT_SUB));
    }

    private void addSectionBanner(Document doc, String title) throws DocumentException {
        PdfPTable banner = new PdfPTable(1);
        banner.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell(new Phrase(title, FONT_SECTION));
        cell.setBackgroundColor(COLOR_SECTION_BG);
        cell.setPadding(6);
        cell.setBorder(Rectangle.NO_BORDER);
        banner.addCell(cell);
        doc.add(banner);
    }

    private PdfPTable buildCardTable(List<DeckEntry> entries, Map<String, byte[]> imageCache)
            throws DocumentException {

        boolean hasImages = !imageCache.isEmpty();
        int cols = hasImages ? 7 : 6;
        PdfPTable table = new PdfPTable(cols);
        table.setWidthPercentage(100);
        table.setSpacingBefore(4);

        float[] widths = hasImages
                ? new float[]{3.5f, 1f, 7f, 6f, 3f, 12f, 3.5f}
                : new float[]{1f, 7f, 6f, 3f, 12f, 3.5f};
        table.setWidths(widths);

        if (hasImages) addHeaderCell(table, "");
        addHeaderCell(table, "#");
        addHeaderCell(table, "Card Name");
        addHeaderCell(table, "Type");
        addHeaderCell(table, "Mana Cost");
        addHeaderCell(table, "Oracle Text");
        addHeaderCell(table, "Rarity");

        boolean alt = false;
        for (DeckEntry entry : entries) {
            Color rowBg = alt ? COLOR_ROW_ALT : Color.WHITE;
            alt = !alt;
            String sid = entry.getCard().scryfallId();

            if (hasImages) {
                PdfPCell imgCell = new PdfPCell();
                imgCell.setBackgroundColor(rowBg);
                imgCell.setBorderColor(COLOR_BORDER);
                imgCell.setPadding(2);
                byte[] imgBytes = imageCache.get(sid);
                if (imgBytes != null) {
                    try {
                        Image img = Image.getInstance(imgBytes);
                        img.scaleToFit(45, 63);
                        imgCell.addElement(img);
                    } catch (Exception ignored) {
                        imgCell.addElement(new Phrase("", FONT_BODY));
                    }
                }
                table.addCell(imgCell);
            }

            addBodyCell(table, String.valueOf(entry.getQuantity()), rowBg);
            addBodyCell(table, entry.getCard().name(), rowBg);
            addBodyCell(table, nullSafe(entry.getCard().typeLine()), rowBg);
            addBodyCell(table, nullSafe(entry.getCard().manaCost()), rowBg);
            addOracleCell(table, nullSafe(entry.getCard().oracleText()), rowBg);
            addBodyCell(table, capitalize(nullSafe(entry.getCard().rarity())), rowBg);
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

    private void addBodyCell(PdfPTable table, String text, Color bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FONT_BODY));
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

    private String nullSafe(String s) {
        return s != null ? s : "";
    }

    private String capitalize(String s) {
        if (s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }
}
