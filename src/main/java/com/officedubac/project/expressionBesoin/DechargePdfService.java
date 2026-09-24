package com.officedubac.project.expressionBesoin;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// Décharge générée au traitement comptable d'une expression de besoin : l'agent bénéficiaire
// la signe pour attester qu'il a bien reçu le montant traité.
@Slf4j
@Service
public class DechargePdfService {

    private static final DateTimeFormatter DATE_COURTE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] generer(ExpressionBesoin eb) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A4, 60, 60, 50, 50);
            PdfWriter writer = PdfWriter.getInstance(doc, baos);
            writer.setPageEvent(new PiedDePageEvent());
            doc.open();

            Font fBold11 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            Font fNorm11 = FontFactory.getFont(FontFactory.HELVETICA, 11);
            Font fBold16 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);

            PdfPTable header = new PdfPTable(1);
            header.setWidthPercentage(100);
            PdfPCell headerCell = new PdfPCell();
            headerCell.setBorder(0);
            headerCell.addElement(new Paragraph("UNIVERSITE CHEIKH ANTA DIOP", fBold11));
            try {
                ClassPathResource logoFile = new ClassPathResource("images/logo-UCAD.png");
                if (logoFile.exists()) {
                    Image logo = Image.getInstance(logoFile.getInputStream().readAllBytes());
                    logo.scaleToFit(50f, 50f);
                    Paragraph logoPara = new Paragraph(new Chunk(logo, 0, 0));
                    logoPara.setIndentationLeft(60f);
                    logoPara.setSpacingBefore(10f);
                    headerCell.addElement(logoPara);
                }
            } catch (Exception e) {
                log.warn("Logo UCAD non trouvé (images/logo-UCAD.png)", e);
            }
            headerCell.addElement(new Paragraph("OFFICE DU BACCALAUREAT", fBold11));
            header.addCell(headerCell);
            doc.add(header);
            doc.add(new Paragraph(" ", fNorm11));
            doc.add(new Paragraph(" ", fNorm11));

            Paragraph titre = new Paragraph("DECHARGE", fBold16);
            titre.setAlignment(Element.ALIGN_CENTER);
            doc.add(titre);
            doc.add(new Paragraph(" ", fNorm11));
            doc.add(new Paragraph(" ", fNorm11));

            String agent = eb.getBeneficiaireNom() != null && !eb.getBeneficiaireNom().isBlank()
                    ? eb.getBeneficiaireNom() : (eb.getCreeParNom() != null ? eb.getCreeParNom() : eb.getCreePar());
            String designation = eb.getMotifLibelle() != null ? eb.getMotifLibelle() : "—";
            BigDecimal montant = eb.getMontantReel() != null ? eb.getMontantReel() : eb.getMontantInitial();

            Paragraph corps = new Paragraph();
            corps.setLeading(20f);
            corps.setAlignment(Element.ALIGN_JUSTIFIED);
            corps.add(new Chunk("Je soussigné(e) ", fNorm11));
            corps.add(new Chunk(agent, fBold11));
            corps.add(new Chunk(", reconnais avoir reçu de l'Office du Baccalauréat la somme de ", fNorm11));
            corps.add(new Chunk(fmt(montant) + " FCFA", fBold11));
            corps.add(new Chunk(", au titre de : ", fNorm11));
            corps.add(new Chunk(designation, fBold11));
            corps.add(new Chunk(".", fNorm11));
            doc.add(corps);
            doc.add(new Paragraph(" ", fNorm11));

            PdfPTable detail = new PdfPTable(new float[]{40f, 20f, 20f, 20f});
            detail.setWidthPercentage(100);
            detail.setSpacingBefore(6f);
            addEntete(detail, "Désignation", fBold11);
            addEntete(detail, "Quantité", fBold11);
            addEntete(detail, "Prix unitaire", fBold11);
            addEntete(detail, "Montant (FCFA)", fBold11);
            addCellule(detail, designation, fNorm11, Element.ALIGN_LEFT);
            addCellule(detail, eb.getQuantite() != null ? String.valueOf(eb.getQuantite()) : "—", fNorm11, Element.ALIGN_CENTER);
            addCellule(detail, eb.getPrixUnitaire() != null ? fmt(eb.getPrixUnitaire()) : "—", fNorm11, Element.ALIGN_RIGHT);
            addCellule(detail, fmt(montant), fNorm11, Element.ALIGN_RIGHT);
            doc.add(detail);

            doc.add(new Paragraph(" ", fNorm11));
            doc.add(new Paragraph(" ", fNorm11));

            String dateTexte = (eb.getDateTraitement() != null ? eb.getDateTraitement().toLocalDate() : LocalDate.now()).format(DATE_COURTE);
            Paragraph faitA = new Paragraph("Fait à Dakar, le " + dateTexte, fNorm11);
            faitA.setAlignment(Element.ALIGN_RIGHT);
            doc.add(faitA);
            doc.add(new Paragraph(" ", fNorm11));

            // Deux blocs de signature côte à côte, non sécables
            PdfPTable sigs = new PdfPTable(2);
            sigs.setWidthPercentage(100);
            sigs.setKeepTogether(true);
            sigs.addCell(blocSignature("Le comptable", eb.getTraiteParNom(), fBold11, fNorm11));
            sigs.addCell(blocSignature("L'agent (signature précédée de « Lu et approuvé »)", agent, fBold11, fNorm11));
            doc.add(sigs);

            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Erreur génération PDF décharge", e);
            throw new RuntimeException("Erreur génération PDF décharge", e);
        }
    }

    private PdfPCell blocSignature(String role, String nom, Font fBold, Font fNorm) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(0);
        cell.setPadding(4f);
        cell.addElement(new Paragraph(role, fBold));
        cell.addElement(new Paragraph(nom != null ? nom : "", fNorm));
        Paragraph espace = new Paragraph(" ");
        espace.setLeading(80f);
        cell.addElement(espace);
        return cell;
    }

    private String fmt(BigDecimal n) {
        return String.format("%,.0f", n).replace(",", " ");
    }

    private void addEntete(PdfPTable table, String texte, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texte, font));
        cell.setBackgroundColor(new java.awt.Color(230, 230, 230));
        cell.setPadding(6f);
        table.addCell(cell);
    }

    private void addCellule(PdfPTable table, String texte, Font font, int alignement) {
        PdfPCell cell = new PdfPCell(new Phrase(texte, font));
        cell.setPadding(5f);
        cell.setHorizontalAlignment(alignement);
        table.addCell(cell);
    }

    private static class PiedDePageEvent extends PdfPageEventHelper {
        private static final Font POLICE = FontFactory.getFont(FontFactory.HELVETICA, 8);
        private static final String TEXTE = "Office du Baccalauréat – Université Cheikh Anta Diop – BP : 5005, "
                + "Dakar, Fann Sénégal – Email : officedubac@ucad.edu.sn - Site internet : "
                + "www.officedubac.sn / https://extrantsbac.ucad.sn/";

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                ColumnText ct = new ColumnText(writer.getDirectContent());
                ct.setSimpleColumn(document.left(), document.bottom() - 40, document.right(), document.bottom() - 5);
                ct.setAlignment(Element.ALIGN_CENTER);
                ct.setLeading(9f);
                ct.addText(new Phrase(TEXTE, POLICE));
                ct.go();
            } catch (DocumentException e) {
                log.warn("Erreur pied de page PDF", e);
            }
        }
    }
}
