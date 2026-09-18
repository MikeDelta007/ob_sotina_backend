package com.officedubac.project.pdftools;

import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;

/**
 * Superpose une grille de coordonnées (points PDF, origine en bas à gauche)
 * sur un gabarit, pour lire précisément les positions des champs imprimés.
 * Usage : mvn -q -o test -Dtest=GridOverlay -Dtemplate.in=<chemin> -Dgrid.out=<chemin>
 */
public class GridOverlay {

    @Test
    void genererGrille() throws Exception {
        String in = System.getProperty("template.in",
                "src/main/resources/templates/releve-A1-template.pdf");
        String out = System.getProperty("grid.out", "target/grid.pdf");

        PdfReader reader = new PdfReader(in);
        try (FileOutputStream fos = new FileOutputStream(out)) {
            PdfStamper stamper = new PdfStamper(reader, fos);
            BaseFont font = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            com.lowagie.text.Rectangle size = reader.getPageSize(1);
            float w = size.getWidth();
            float h = size.getHeight();

            PdfContentByte cb = stamper.getOverContent(1);
            cb.saveState();
            cb.setLineWidth(0.3f);

            for (float x = 0; x <= w; x += 10) {
                boolean major = Math.round(x) % 50 == 0;
                cb.setColorStroke(major ? new java.awt.Color(255, 0, 0) : new java.awt.Color(255, 180, 180));
                cb.moveTo(x, 0);
                cb.lineTo(x, h);
                cb.stroke();
            }
            for (float y = 0; y <= h; y += 10) {
                boolean major = Math.round(y) % 50 == 0;
                cb.setColorStroke(major ? new java.awt.Color(0, 0, 255) : new java.awt.Color(180, 180, 255));
                cb.moveTo(0, y);
                cb.lineTo(w, y);
                cb.stroke();
            }
            cb.restoreState();

            cb.beginText();
            cb.setFontAndSize(font, 6f);
            cb.setColorFill(new java.awt.Color(0, 120, 0));
            for (float x = 0; x <= w; x += 50) {
                for (float y = 0; y <= h; y += 50) {
                    cb.setTextMatrix(x + 1, y + 1);
                    cb.showText(((int) x) + "," + ((int) y));
                }
            }
            cb.endText();

            stamper.close();
        }
        reader.close();
    }
}
