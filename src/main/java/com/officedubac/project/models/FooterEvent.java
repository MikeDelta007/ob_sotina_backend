package com.officedubac.project.models;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FooterEvent extends PdfPageEventHelper {

    private Font font;

    public FooterEvent(Font font) {
        this.font = font;
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {

        PdfPTable footer = new PdfPTable(1);
        footer.setTotalWidth(527);
        footer.setLockedWidth(true);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String dateHeure = LocalDateTime.now().format(formatter);

        PdfPCell cell = new PdfPCell(
                new Phrase(
                        "Bordereau généré le " + dateHeure +
                                " / Division Pédagogie \n© Office du Baccalauréat, tous droits réservés.",
                        font
                )
        );
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        footer.addCell(cell);

        // Position du footer
        footer.writeSelectedRows(
                0, -1,
                document.leftMargin(),
                document.bottomMargin() - 5,
                writer.getDirectContent()
        );
    }
}
