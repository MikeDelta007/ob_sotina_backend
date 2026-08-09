package com.officedubac.project.caisseAvance;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class DecaissementPdfService {

    private static final DateTimeFormatter DATE_FR =
            DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH);

    // ══════════════════════════════════════════════════════════════════
    // Génère le PDF de décaissement pour un mandatement
    // ══════════════════════════════════════════════════════════════════
    public byte[] genererDecaissement(Mandatement mandatement) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A4, 70, 70, 60, 60);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            // ── Polices ──
            Font fBold12  = FontFactory.getFont(FontFactory.HELVETICA_BOLD,  12);
            Font fBold11  = FontFactory.getFont(FontFactory.HELVETICA_BOLD,  11);
            Font fNorm11  = FontFactory.getFont(FontFactory.HELVETICA,       11);
            Font fNorm10  = FontFactory.getFont(FontFactory.HELVETICA,       10);
            Font fBold10  = FontFactory.getFont(FontFactory.HELVETICA_BOLD,  10);
            Font fBold9   = FontFactory.getFont(FontFactory.HELVETICA_BOLD,   9);
            Font fNorm9   = FontFactory.getFont(FontFactory.HELVETICA,        9);
            Font fBold14  = FontFactory.getFont(FontFactory.HELVETICA_BOLD,  14);

            // ══════════════════════
            // EN-TÊTE : gauche=entité  droite=lieu+date
            // ══════════════════════
            PdfPTable header = new PdfPTable(new float[]{50f, 50f});
            header.setWidthPercentage(100);
            header.getDefaultCell().setBorder(0);

            // Colonne gauche
            PdfPCell left = new PdfPCell();
            left.setBorder(0);
            left.addElement(new Paragraph("REPUBLIQUE DU SENEGAL", fBold11));
            // Trait souligné sous République
            PdfPTable underline = new PdfPTable(1);
            underline.setWidthPercentage(60);
            underline.setHorizontalAlignment(Element.ALIGN_LEFT);
            PdfPCell uc = new PdfPCell(new Phrase(""));
            uc.setBorderWidthBottom(1f); uc.setBorderWidthTop(0);
            uc.setBorderWidthLeft(0); uc.setBorderWidthRight(0);
            uc.setFixedHeight(4f);
            underline.addCell(uc);
            left.addElement(underline);

            left.addElement(new Paragraph(" ", fNorm9));
            Paragraph devise = new Paragraph("Un Peuple - Un But - Une Foi", fNorm9);
            devise.setAlignment(Element.ALIGN_CENTER);
            left.addElement(devise);
            left.addElement(new Paragraph(" ", fNorm9));
            left.addElement(new Paragraph("MINISTERE DE L'ENSEIGNEMENT SUPERIEUR", fBold9));
            left.addElement(new Paragraph("DE LA RECHERCHE ET DE L'INNOVATION",    fBold9));
            left.addElement(new Paragraph("OFFICE DU BACCALAUREAT",                fBold9));
            left.addElement(new Paragraph("Site web: www.officedubac.sn",          fNorm9));
            left.addElement(new Paragraph("Email: officedubac@ucad.edu.sn",        fNorm9));
            header.addCell(left);

            // Colonne droite : lieu + date + LE DIRECTEUR
            PdfPCell right = new PdfPCell();
            right.setBorder(0);
            right.setHorizontalAlignment(Element.ALIGN_RIGHT);
            String dateStr = "Dakar, le " + LocalDate.now().format(DATE_FR);
            Paragraph datePara = new Paragraph(dateStr, fNorm11);
            datePara.setAlignment(Element.ALIGN_RIGHT);
            right.addElement(new Paragraph(" ", fNorm9));
            right.addElement(new Paragraph(" ", fNorm9));
            right.addElement(new Paragraph(" ", fNorm9));
            right.addElement(datePara);
            right.addElement(new Paragraph(" ", fNorm9));
            right.addElement(new Paragraph(" ", fNorm9));
            Paragraph dir = new Paragraph("LE DIRECTEUR,", fNorm11);
            dir.setAlignment(Element.ALIGN_RIGHT);
            right.addElement(dir);
            header.addCell(right);

            doc.add(header);
            doc.add(new Paragraph(" ", fNorm11));
            doc.add(new Paragraph(" ", fNorm11));
            doc.add(new Paragraph(" ", fNorm11));

            // ══════════════════════
            // TITRE CENTRÉ
            // ══════════════════════
            Font fBold14Underline = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            fBold14Underline.setStyle(Font.BOLD | Font.UNDERLINE);
            Paragraph titre = new Paragraph("DECAISSEMENT", fBold14Underline);
            titre.setAlignment(Element.ALIGN_CENTER);

            doc.add(new Paragraph(" ", fNorm11));
            doc.add(new Paragraph(" ", fNorm11));

            // ══════════════════════
            // CORPS : autorisation
            // ══════════════════════
            // Le PDF documente le décaissement réel : le montant de l'avance en cas de
            // paiement AVANCE (montantAvance == montantTotal en cas de paiement TOTALITE).
            BigDecimal montant = mandatement.getMontantAvance() != null
                    ? mandatement.getMontantAvance() : mandatement.getMontantTotal();
            String montantLettre = nombreEnLettres(montant.longValue());
            String montantChiffre = String.format("%,.0f", montant).replace(",", " ");

            // Espèces ou Chèque → formulation différente
            String corps;
            if (mandatement.getModePaiement() == Mandatement.ModePaiement.ESPECES) {
                corps = "Est autorisé à Mme FALL Penda, Comptable, de procéder à un décaissement sur la "
                      + "fonction de service d'un montant de " + montantLettre
                      + " (" + montantChiffre + " Frs) pour règlement de facture.";
            } else {
                corps = "Est autorisé à Mme FALL Penda, Comptable, d'émettre un chèque sur la fonction de "
                      + "service d'un montant de " + montantLettre
                      + " (" + montantChiffre + " Frs) pour règlement de facture.";
            }

            Paragraph corpsPara = new Paragraph(corps, fNorm11);
            corpsPara.setAlignment(Element.ALIGN_JUSTIFIED);
            corpsPara.setLeading(18f);
            doc.add(corpsPara);

            doc.add(new Paragraph(" ", fNorm11));
            doc.add(new Paragraph(" ", fNorm11));

            // ══════════════════════
            // MOTIF
            // ══════════════════════
            String motifTexte = buildMotifTexte(mandatement);

            Paragraph motifPara = new Paragraph();
            motifPara.setAlignment(Element.ALIGN_JUSTIFIED);
            motifPara.setLeading(18f);
            motifPara.add(new Chunk("Motif : ", fBold11));
            motifPara.add(new Chunk(motifTexte, fNorm11));
            doc.add(motifPara);

            doc.add(new Paragraph(" ", fNorm11));
            doc.add(new Paragraph(" ", fNorm11));

            // ══════════════════════
            // ARRÊTÉ
            // ══════════════════════
            Paragraph arrete = new Paragraph(
                "Arrêté le présent état à la somme de " + montantLettre + " Francs CFA.",
                fNorm11);
            arrete.setAlignment(Element.ALIGN_JUSTIFIED);
            arrete.setLeading(18f);
            doc.add(arrete);

            doc.add(new Paragraph(" ", fNorm11));
            doc.add(new Paragraph(" ", fNorm11));
            doc.add(new Paragraph(" ", fNorm11));

            // ══════════════════════
            // SIGNATURE (alignée à droite)
            // ══════════════════════
            Paragraph sign = new Paragraph("Cheikh Ahmadou Bamba GUEYE", fBold11);
            sign.setAlignment(Element.ALIGN_RIGHT);
            doc.add(sign);

            doc.close();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Erreur génération PDF décaissement", e);
            throw new RuntimeException("Erreur génération PDF décaissement", e);
        }
    }

    // ── Construit le texte des motifs depuis les factures embedded ──
    private String buildMotifTexte(Mandatement mandatement) {
        List<Mandatement.FactureEmbedded> factures = mandatement.getFactures();
        if (factures == null || factures.isEmpty()) return "—";
        if (factures.size() == 1) {
            return factures.get(0).getMotifLibelle() != null
                    ? factures.get(0).getMotifLibelle() : "—";
        }
        // Cumulatif → concaténer les motifs avec "+"
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < factures.size(); i++) {
            if (i > 0) sb.append("+");
            String lib = factures.get(i).getMotifLibelle();
            sb.append(lib != null ? lib : "—");
        }
        return sb.toString();
    }

    // ══════════════════════════════════════════════════════════════════
    // Conversion nombre → lettres (FCFA)
    // ══════════════════════════════════════════════════════════════════
    private String nombreEnLettres(long n) {
        if (n == 0) return "Zéro";
        return capitaliser(convertir(n)) + " Francs CFA";
    }

    private String convertir(long n) {
        if (n < 0)  return "moins " + convertir(-n);
        if (n == 0) return "";
        if (n < 17) return new String[]{
            "","un","deux","trois","quatre","cinq","six","sept","huit",
            "neuf","dix","onze","douze","treize","quatorze","quinze","seize"}[(int)n];
        if (n < 20) return "dix-" + convertir(n - 10);
        if (n < 30) return "vingt" + (n > 20 ? "-" + convertir(n - 20) : "");
        if (n < 40) return "trente" + (n > 30 ? "-" + convertir(n - 30) : "");
        if (n < 50) return "quarante" + (n > 40 ? "-" + convertir(n - 40) : "");
        if (n < 60) return "cinquante" + (n > 50 ? "-" + convertir(n - 50) : "");
        if (n < 70) return "soixante" + (n > 60 ? "-" + convertir(n - 60) : "");
        if (n < 80) return "soixante-" + convertir(n - 60);
        if (n < 100) return "quatre-vingt" + (n == 80 ? "s" : "-" + convertir(n - 80));
        if (n < 200) return "cent" + (n > 100 ? " " + convertir(n - 100) : "");
        if (n < 1_000) return convertir(n / 100) + " cent" + (n % 100 == 0 ? "s" : " " + convertir(n % 100));
        if (n < 2_000) return "mille" + (n > 1_000 ? " " + convertir(n - 1_000) : "");
        if (n < 1_000_000) return convertir(n / 1_000) + " mille" + (n % 1_000 != 0 ? " " + convertir(n % 1_000) : "");
        if (n < 1_000_000_000) return convertir(n / 1_000_000) + " million" + (n / 1_000_000 > 1 ? "s" : "")
                + (n % 1_000_000 != 0 ? " " + convertir(n % 1_000_000) : "");
        return convertir(n / 1_000_000_000) + " milliard" + (n / 1_000_000_000 > 1 ? "s" : "")
                + (n % 1_000_000_000 != 0 ? " " + convertir(n % 1_000_000_000) : "");
    }

    private String capitaliser(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
