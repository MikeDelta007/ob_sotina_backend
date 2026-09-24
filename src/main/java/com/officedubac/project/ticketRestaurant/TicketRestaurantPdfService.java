package com.officedubac.project.ticketRestaurant;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Liste des agents concernés par une demande de tickets restaurant validée, avec le nombre de
// jours, le montant total et la signature du Directeur — même gabarit que le PDF d'autorisation
// d'absence (en-tête Université/logo, pied de page, signature).
@Slf4j
@Service
public class TicketRestaurantPdfService {

    private static final DateTimeFormatter DATE_COURTE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] genererListe(TicketRestaurant ticket) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A4, 60, 60, 50, 50);
            PdfWriter writer = PdfWriter.getInstance(doc, baos);
            writer.setPageEvent(new PiedDePageEvent());
            doc.open();

            Font fBold11 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            Font fNorm11 = FontFactory.getFont(FontFactory.HELVETICA, 11);
            Font fBold16 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font fNorm10 = FontFactory.getFont(FontFactory.HELVETICA, 10);

            // ══════════════════════
            // EN-TÊTE : Université + logo + Office, dans une cellule (comme le PDF
            // d'autorisation d'absence) — un Chunk/Paragraph autour d'une image ajoutée
            // directement au document (hors cellule) casse l'enchaînement vertical et fait
            // chevaucher le texte qui suit.
            // ══════════════════════
            PdfPTable header = new PdfPTable(1);
            header.setWidthPercentage(100);
            PdfPCell headerCell = new PdfPCell();
            headerCell.setBorder(0);
            Paragraph universite = new Paragraph("UNIVERSITE CHEIKH ANTA DIOP", fBold11);
            headerCell.addElement(universite);

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

            // ══════════════════════
            // TITRE
            // ══════════════════════
            Paragraph titre = new Paragraph("LISTE TICKETS RESTAURANT", fBold16);
            titre.setAlignment(Element.ALIGN_CENTER);
            doc.add(titre);
            doc.add(new Paragraph(" ", fNorm11));

            String jours = joursCoches(ticket);
            Paragraph periode = new Paragraph(
                    "Période : du " + ticket.getDateDebut().format(DATE_COURTE)
                            + " au " + ticket.getDateFin().format(DATE_COURTE)
                            + "  —  Jours concernés : " + jours
                            + "  —  " + ticket.getNombreJours() + " jour(s)",
                    fNorm10);
            doc.add(periode);
            doc.add(new Paragraph(" ", fNorm11));

            // ══════════════════════
            // TABLEAU DES AGENTS
            // ══════════════════════
            PdfPTable table = new PdfPTable(new float[]{10f, 90f});
            table.setWidthPercentage(100);
            table.setSpacingBefore(6f);
            addEntete(table, "N°", fBold11);
            addEntete(table, "Agent", fBold11);

            List<String> noms = ticket.getAgentNoms() != null ? ticket.getAgentNoms() : new ArrayList<>();
            for (int i = 0; i < noms.size(); i++) {
                addCellule(table, String.valueOf(i + 1), fNorm11, Element.ALIGN_CENTER);
                addCellule(table, noms.get(i), fNorm11, Element.ALIGN_LEFT);
            }
            doc.add(table);

            doc.add(new Paragraph(" ", fNorm11));
            Paragraph total = new Paragraph(
                    "Total : " + noms.size() + " agent(s) × " + ticket.getNombreJours()
                            + " jour(s) × 1500 FCFA = " + fmt(ticket.getMontantTotal()) + " FCFA",
                    fBold11);
            doc.add(total);

            doc.add(new Paragraph(" ", fNorm11));
            doc.add(new Paragraph(" ", fNorm11));
            doc.add(new Paragraph(" ", fNorm11));

            // ══════════════════════
            // SIGNATURE
            // ══════════════════════
            Paragraph faitA = new Paragraph("Fait à Dakar, le "
                    + (ticket.getDateValidationDirecteur() != null
                        ? ticket.getDateValidationDirecteur().toLocalDate().format(DATE_COURTE)
                        : LocalDate.now().format(DATE_COURTE)),
                    fNorm11);
            faitA.setAlignment(Element.ALIGN_RIGHT);
            doc.add(faitA);

            Paragraph directeurLabel = new Paragraph("Le Directeur", fBold11);
            directeurLabel.setAlignment(Element.ALIGN_RIGHT);
            directeurLabel.setIndentationRight(40f);
            doc.add(directeurLabel);

            try {
                ClassPathResource sigFile = new ClassPathResource("images/signature.png");
                if (sigFile.exists()) {
                    Image signature = Image.getInstance(sigFile.getInputStream().readAllBytes());
                    signature.scaleToFit(90f, 90f);
                    signature.setAlignment(Image.ALIGN_RIGHT);
                    doc.setMargins(60, 120, 50, 50);
                    doc.add(signature);
                    doc.setMargins(60, 60, 50, 50);
                } else {
                    log.warn("Signature non trouvée (images/signature.png)");
                }
            } catch (Exception e) {
                log.warn("Erreur chargement de la signature pour le PDF de tickets restaurant", e);
            }

            Paragraph directeurNom = new Paragraph(
                    ticket.getValidateurDirecteurNom() != null ? ticket.getValidateurDirecteurNom() : "Cheikh Ahmadou Bamba GUEYE",
                    fBold11);
            directeurNom.setAlignment(Element.ALIGN_RIGHT);
            directeurNom.setSpacingBefore(-15f);
            doc.add(directeurNom);

            doc.close();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Erreur génération PDF liste tickets restaurant", e);
            throw new RuntimeException("Erreur génération PDF liste tickets restaurant", e);
        }
    }

    private String joursCoches(TicketRestaurant t) {
        List<String> jours = new ArrayList<>();
        if (t.isLundi()) jours.add("Lundi");
        if (t.isMardi()) jours.add("Mardi");
        if (t.isMercredi()) jours.add("Mercredi");
        if (t.isJeudi()) jours.add("Jeudi");
        if (t.isVendredi()) jours.add("Vendredi");
        return String.join(", ", jours);
    }

    private String fmt(java.math.BigDecimal n) {
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

    // Pied de page (coordonnées de l'Office), répété en bas de chaque page du document.
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
