package com.officedubac.project.models;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FooterEvent2 extends PdfPageEventHelper {

    private Font font;

    public FooterEvent2(Font font) {
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
                        "NB : Pour chaque épreuve, la dotation moyenne par candidat est constituée d’une (1) feuille double, \n" +
                                "de (2) deux feuilles intercalaires et deux (2) feuilles de brouillon.\n\n",
                        font
                )
        );

        PdfPCell cell2 = new PdfPCell(
                new Phrase(
                        "Bordereau généré le " + dateHeure + " / Division de la Planification \n© Office du Baccalauréat, tous droits réservés.",
                        font
                )
        );

        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);

        footer.addCell(cell);
        footer.addCell(cell2);


        // Position du footer
        footer.writeSelectedRows(
                0, -1,
                document.leftMargin(),
                document.bottomMargin() + 20,
                writer.getDirectContent()
        );
    }
}
