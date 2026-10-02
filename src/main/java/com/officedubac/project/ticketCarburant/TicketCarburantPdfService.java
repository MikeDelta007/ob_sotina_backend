package com.officedubac.project.ticketCarburant;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// Fiche "DEMANDE DE CARBURANT" — même gabarit officiel (République/drapeau/Ministère) que le
// PDF de décaissement, avec la signature du Directeur (seul validateur dont la signature
// figure sur la fiche imprimée).
@Slf4j
@Service
public class TicketCarburantPdfService {

    private static final DateTimeFormatter DATE_COURTE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] genererFiche(TicketCarburant ticket) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A4, 70, 70, 60, 60);
            PdfWriter writer = PdfWriter.getInstance(doc, baos);
            writer.setPageEvent(new PiedDePageEvent());
            doc.open();

            Font fBold11 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            Font fNorm11 = FontFactory.getFont(FontFactory.HELVETICA, 11);
            Font fBold9  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
            Font fNorm9  = FontFactory.getFont(FontFactory.HELVETICA, 9);

            // ══════════════════════
            // EN-TÊTE : République / drapeau / devise / Ministère (identique au PDF de décaissement)
            // ══════════════════════
            PdfPTable ligneHaut = new PdfPTable(new float[]{50f, 50f});
            ligneHaut.setWidthPercentage(100);
            PdfPCell republique = new PdfPCell(new Paragraph("REPUBLIQUE DU SENEGAL", fBold11));
            republique.setBorder(0);
            republique.setPadding(0);
            ligneHaut.addCell(republique);
            Paragraph numeroPara = new Paragraph("N° Fiche " + (ticket.getNumeroFiche() != null ? ticket.getNumeroFiche() : "—"), fNorm11);
            numeroPara.setAlignment(Element.ALIGN_RIGHT);
            PdfPCell numeroCell = new PdfPCell(numeroPara);
            numeroCell.setBorder(0);
            numeroCell.setPadding(0);
            numeroCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            ligneHaut.addCell(numeroCell);
            doc.add(ligneHaut);
            doc.add(new Paragraph("====================", fBold11));

            try {
                ClassPathResource drapeauFile = new ClassPathResource("images/drapeau.png");
                if (drapeauFile.exists()) {
                    Image drapeau = Image.getInstance(drapeauFile.getInputStream().readAllBytes());
                    drapeau.scaleToFit(60f, 40f);
                    drapeau.setAlignment(Image.ALIGN_LEFT);
                    drapeau.setIndentationLeft(40f);
                    doc.add(drapeau);
                }
            } catch (Exception e) {
                log.warn("Drapeau non trouvé (images/drapeau.png)", e);
            }
            doc.add(new Paragraph("====================", fBold11));

            Paragraph devise = new Paragraph("Un Peuple - Un But - Une Foi", fNorm9);
            devise.setIndentationLeft(20f);
            doc.add(devise);
            doc.add(new Paragraph(" ", fNorm9));
            doc.add(new Paragraph("MINISTERE DE L'ENSEIGNEMENT SUPERIEUR", fBold9));
            doc.add(new Paragraph("DE LA RECHERCHE ET DE L'INNOVATION",    fBold9));
            doc.add(new Paragraph("OFFICE DU BACCALAUREAT",                fBold9));
            doc.add(new Paragraph(" ", fNorm11));
            doc.add(new Paragraph(" ", fNorm11));

            // ══════════════════════
            // TITRE
            // ══════════════════════
            Font fBold16Underline = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            fBold16Underline.setStyle(Font.BOLD | Font.UNDERLINE);
            Paragraph titre = new Paragraph("DEMANDE DE CARBURANT", fBold16Underline);
            titre.setAlignment(Element.ALIGN_CENTER);
            doc.add(titre);
            doc.add(new Paragraph(" ", fNorm11));
            doc.add(new Paragraph(" ", fNorm11));

            // ══════════════════════
            // CORPS : mêmes champs que la fiche papier
            // ══════════════════════
            ligne(doc, "Division ou Service", ticket.getDivisionLibelle(), fBold11, fNorm11);
            ligne(doc, "Motif du déplacement", ticket.getMotifLibelle(), fBold11, fNorm11);
            ligne(doc, "Date", ticket.getDate() != null ? ticket.getDate().format(DATE_COURTE) : "—", fBold11, fNorm11);
            ligne(doc, "Trajet", ticket.getVilleDepartNom() + " — " + ticket.getVilleArriveeNom(), fBold11, fNorm11);
            ligne(doc, "Nombre de tickets demandé", String.valueOf(ticket.getNombreTicketsDemande()), fBold11, fNorm11);
            ligne(doc, "Nombre de tickets accordé par la Direction",
                    ticket.getNombreTicketsAccorde() != null ? String.valueOf(ticket.getNombreTicketsAccorde()) : "—",
                    fBold11, fNorm11);

            doc.add(new Paragraph(" ", fNorm11));
            doc.add(new Paragraph(" ", fNorm11));
            doc.add(new Paragraph(" ", fNorm11));

            // ══════════════════════
            // SIGNATURE : Directeur seul, bloc unique non sécable aligné à droite
            // ══════════════════════
            String dateTexte = (ticket.getDateValidationDirecteur() != null
                    ? ticket.getDateValidationDirecteur().toLocalDate() : LocalDate.now()).format(DATE_COURTE);
            Paragraph faitA = new Paragraph("Fait à Dakar, le " + dateTexte, fNorm11);
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
                log.warn("Erreur chargement de la signature pour le PDF de demande de carburant", e);
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
            log.error("Erreur génération PDF demande de carburant", e);
            throw new RuntimeException("Erreur génération PDF demande de carburant", e);
        }
    }

    private void ligne(Document doc, String libelle, String valeur, Font fBold, Font fNorm) throws DocumentException {
        Paragraph p = new Paragraph();
        p.add(new Chunk(libelle + " : ", fBold));
        p.add(new Chunk(valeur != null ? valeur : "—", fNorm));
        p.setSpacingAfter(6f);
        doc.add(p);
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
