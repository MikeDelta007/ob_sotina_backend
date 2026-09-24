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
// jours, le montant total et le nom du demandeur (cachet et signature à apposer) — même gabarit que le PDF d'autorisation
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

            List<LocalDate> dates = ticket.getDates() != null ? ticket.getDates() : new ArrayList<>();
            Paragraph periode = new Paragraph(
                    "Dates concernées (" + ticket.getNombreJours() + " jour(s)) : "
                            + dates.stream().map(d -> d.format(DATE_COURTE)).collect(java.util.stream.Collectors.joining(", ")),
                    fNorm10);
            doc.add(periode);
            doc.add(new Paragraph(" ", fNorm11));

            // ══════════════════════
            // TABLEAU DES AGENTS
            // ══════════════════════
            PdfPTable table = new PdfPTable(new float[]{8f, 50f, 42f});
            table.setWidthPercentage(100);
            table.setSpacingBefore(6f);
            table.setHeaderRows(1);
            addEntete(table, "N°", fBold11);
            addEntete(table, "Prénom et nom", fBold11);
            addEntete(table, "Service / Division", fBold11);

            List<String> noms = ticket.getAgentNoms() != null ? ticket.getAgentNoms() : new ArrayList<>();
            List<String> services = ticket.getAgentServices();
            for (int i = 0; i < noms.size(); i++) {
                addCellule(table, String.valueOf(i + 1), fNorm11, Element.ALIGN_CENTER);
                addCellule(table, noms.get(i), fNorm11, Element.ALIGN_LEFT);
                String service = services != null && i < services.size() && services.get(i) != null ? services.get(i) : "—";
                addCellule(table, service, fNorm11, Element.ALIGN_LEFT);
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

            // ══════════════════════
            // SIGNATURE : bloc unique (table non sécable, alignée à droite) pour que la date,
            // la signature et le nom restent ensemble et passent à la page suivante si besoin.
            // ══════════════════════
            PdfPTable sig = new PdfPTable(1);
            sig.setWidthPercentage(40);
            sig.setHorizontalAlignment(Element.ALIGN_RIGHT);
            sig.setKeepTogether(true);
            sig.setSplitLate(true);

            String dateTexte = ticket.getDateCreation() != null
                    ? ticket.getDateCreation().toLocalDate().format(DATE_COURTE)
                    : LocalDate.now().format(DATE_COURTE);
            addCelluleSignature(sig, new Paragraph("Fait à Dakar, le " + dateTexte, fNorm11));
            addCelluleSignature(sig, new Paragraph("Le demandeur", fBold11));
            addCelluleSignature(sig, new Paragraph(
                    ticket.getCreeParNom() != null ? ticket.getCreeParNom() : ticket.getCreePar(), fBold11));
            // Espace laissé libre pour le cachet et la signature du demandeur
            PdfPCell espace = new PdfPCell(new Phrase(" ", fNorm11));
            espace.setBorder(0);
            espace.setFixedHeight(90f);
            sig.addCell(espace);
            doc.add(sig);

            doc.close();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Erreur génération PDF liste tickets restaurant", e);
            throw new RuntimeException("Erreur génération PDF liste tickets restaurant", e);
        }
    }

    private void addCelluleSignature(PdfPTable table, Paragraph contenu) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(0);
        cell.addElement(contenu);
        table.addCell(cell);
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
