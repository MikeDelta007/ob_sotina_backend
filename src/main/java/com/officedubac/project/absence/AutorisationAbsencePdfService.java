package com.officedubac.project.absence;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.officedubac.project.models.User;
import com.officedubac.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// Reproduit le formulaire officiel "AUTORISATION D'ABSENCE" (Office du Bac / MESRI) :
// en-tête République + Ministère + logo, encadré Monsieur/Madame - Qualité et fonction -
// Matricule - période - motif, puis la signature du Directeur.
@Slf4j
@Service
@RequiredArgsConstructor
public class AutorisationAbsencePdfService {

    private final UserRepository userRepository;

    private static final DateTimeFormatter DATE_COURTE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] genererAutorisation(DemandeAbsence demande) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A4, 60, 60, 50, 50);
            PdfWriter writer = PdfWriter.getInstance(doc, baos);
            writer.setPageEvent(new PiedDePageEvent());
            doc.open();

            Font fBold11 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            Font fNorm11 = FontFactory.getFont(FontFactory.HELVETICA, 11);
            Font fBold18 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);

            // ══════════════════════
            // EN-TÊTE : gauche = Université + logo + Office, droite = N°
            // ══════════════════════
            PdfPTable header = new PdfPTable(new float[]{50f, 50f});
            header.setWidthPercentage(100);
            header.getDefaultCell().setBorder(0);

            PdfPCell left = new PdfPCell();
            left.setBorder(0);
            left.setHorizontalAlignment(Element.ALIGN_LEFT);
            Paragraph universite = new Paragraph("UNIVERSITE CHEIKH ANTA DIOP", fBold11);
            universite.setAlignment(Element.ALIGN_LEFT);
            left.addElement(universite);

            try {
                ClassPathResource logoFile = new ClassPathResource("images/logo-UCAD.png");
                if (logoFile.exists()) {
                    Image logo = Image.getInstance(logoFile.getInputStream().readAllBytes());
                    logo.scaleToFit(50f, 50f);
                    Paragraph logoPara = new Paragraph(new Chunk(logo, 0, 0));
                    logoPara.setIndentationLeft(60f);
                    logoPara.setSpacingBefore(10f);
                    left.addElement(logoPara);
                }
            } catch (Exception e) {
                log.warn("Logo UCAD non trouvé (images/logo-UCAD.png)", e);
            }
            Paragraph office = new Paragraph("OFFICE DU BACCALAUREAT", fBold11);
            office.setAlignment(Element.ALIGN_LEFT);
            left.addElement(office);
            header.addCell(left);

            PdfPCell right = new PdfPCell();
            right.setBorder(0);
            right.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Paragraph numero = new Paragraph("N° .......................... MESRI/UCAD/OB/CSA/ot", fNorm11);
            numero.setAlignment(Element.ALIGN_RIGHT);
            right.addElement(numero);
            header.addCell(right);

            // Date d'émission — affichée une seule fois, avec "Fait à Dakar, le" près de la signature
            LocalDate dateEmission = demande.getDateValidationDirecteur() != null
                    ? demande.getDateValidationDirecteur().toLocalDate() : LocalDate.now();

            doc.add(header);
            doc.add(new Paragraph(" ", fNorm11));
            doc.add(new Paragraph(" ", fNorm11));

            // ══════════════════════
            // TITRE CENTRÉ
            // ══════════════════════
            Paragraph titre = new Paragraph("AUTORISATION D'ABSENCE", fBold18);
            titre.setAlignment(Element.ALIGN_CENTER);
            doc.add(titre);
            doc.add(new Paragraph(" ", fNorm11));
            doc.add(new Paragraph(" ", fNorm11));

            // ══════════════════════
            // CORPS encadré (barre verticale à gauche, comme le formulaire officiel)
            // ══════════════════════
            User demandeur = userRepository.findById(demande.getDemandeurId()).orElse(null);
            String qualiteFonction = demandeur != null && demandeur.getPersonnel() != null
                    && demandeur.getPersonnel().getFonction() != null
                    ? demandeur.getPersonnel().getFonction().getLibelle() : "";
            String matricule = demandeur != null && demandeur.getPersonnel() != null
                    && demandeur.getPersonnel().getMatricule() != null
                    ? demandeur.getPersonnel().getMatricule() : "";

            PdfPTable corpsTable = new PdfPTable(1);
            corpsTable.setWidthPercentage(100);
            PdfPCell corpsCell = new PdfPCell();
            corpsCell.setBorderWidthLeft(2.5f);
            corpsCell.setBorderWidthTop(0);
            corpsCell.setBorderWidthRight(0);
            corpsCell.setBorderWidthBottom(0);
            corpsCell.setPaddingLeft(14f);
            corpsCell.setPaddingTop(8f);
            corpsCell.setPaddingBottom(8f);

            Paragraph autorisePar = new Paragraph();
            autorisePar.add(new Chunk("Le ", fNorm11));
            autorisePar.add(new Chunk("Directeur", fBold11));
            autorisePar.add(new Chunk(" de l'Office du Baccalauréat autorise :", fNorm11));
            corpsCell.addElement(autorisePar);
            corpsCell.addElement(new Paragraph(" ", fNorm11));
            corpsCell.addElement(champ("Monsieur / Madame", demande.getDemandeurNom(), fNorm11, fBold11));
            corpsCell.addElement(champ("Qualité et fonction", qualiteFonction, fNorm11, fBold11));
            corpsCell.addElement(champ("Matricule", matricule, fNorm11, fBold11));
            Paragraph periode = new Paragraph();
            periode.setLeading(16f);
            periode.add(new Chunk("à s'absenter de son poste de travail du ", fNorm11));
            periode.add(new Chunk(demande.getDateDebut().format(DATE_COURTE), fBold11));
            periode.add(new Chunk(" au ", fNorm11));
            periode.add(new Chunk(demande.getDateFin().format(DATE_COURTE), fBold11));
            corpsCell.addElement(periode);
            corpsCell.addElement(new Paragraph("soit " + demande.getNombreJours() + " jour(s),", fNorm11));
            corpsCell.addElement(champ("pour le motif suivant", demande.getMotif(), fNorm11, fBold11));

            corpsTable.addCell(corpsCell);
            doc.add(corpsTable);

            doc.add(new Paragraph(" ", fNorm11));
            doc.add(new Paragraph(" ", fNorm11));

            Paragraph chefSA = new Paragraph(
                    "Le Chef des Services Administratifs est chargé de l'application de la présente autorisation.",
                    fNorm11);
            chefSA.setLeading(16f);
            doc.add(chefSA);

            doc.add(new Paragraph(" ", fNorm11));
            doc.add(new Paragraph(" ", fNorm11));
            doc.add(new Paragraph(" ", fNorm11));

            // ══════════════════════
            // SIGNATURE
            // ══════════════════════
            Paragraph faitA = new Paragraph("Fait à Dakar, le " + dateEmission.format(DATE_COURTE), fNorm11);
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
                    signature.setIndentationRight(20f);
                    // Décale temporairement la marge droite pour reproduire l'indentation sans
                    // casser l'enchaînement vertical du document (un Chunk/Paragraph autour de
                    // l'image casse le flux et fait chevaucher tout ce qui suit).
                    doc.setMargins(60, 120, 50, 50);
                    doc.add(signature);
                    doc.setMargins(60, 60, 50, 50);
                } else {
                    log.warn("Signature non trouvée (images/signature.png)");
                }
            } catch (Exception e) {
                log.warn("Erreur chargement de la signature pour le PDF d'autorisation d'absence", e);
            }

            Paragraph directeurNom = new Paragraph(nomDirecteur(demande), fBold11);
            directeurNom.setAlignment(Element.ALIGN_RIGHT);
            directeurNom.setSpacingBefore(-15f);
            doc.add(directeurNom);

            doc.close();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Erreur génération PDF autorisation d'absence", e);
            throw new RuntimeException("Erreur génération PDF autorisation d'absence", e);
        }
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
                // Colonne bornée (pas showTextAligned, qui ne retourne pas à la ligne) : le
                // texte se répartit sur deux lignes plutôt que de déborder des marges.
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

    private Paragraph champ(String label, String valeur, Font fNorm, Font fBold) {
        Paragraph p = new Paragraph();
        p.setLeading(16f);
        p.add(new Chunk(label + " : ", fNorm));
        p.add(new Chunk(valeur != null ? valeur : "", fBold));
        return p;
    }

    // Nom de celui qui a effectivement validé en tant que Directeur ; à défaut (demande
    // ancienne, compte supprimé...), on retombe sur le Directeur en poste.
    private String nomDirecteur(DemandeAbsence demande) {
        if (demande.getValidateurDirecteur() != null) {
            User directeur = userRepository.findByLogin(demande.getValidateurDirecteur()).orElse(null);
            if (directeur != null && directeur.getPersonnel() != null) {
                String nom = (directeur.getPersonnel().getFirstname() + " " + directeur.getPersonnel().getLastname()).trim();
                if (!nom.isEmpty()) return nom;
            }
        }
        return "Cheikh Ahmadou Bamba GUEYE";
    }
}
