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
            PdfWriter.getInstance(doc, baos);
            doc.open();

            Font fBold12 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font fBold11 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            Font fNorm11 = FontFactory.getFont(FontFactory.HELVETICA, 11);
            Font fItalic10 = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10);
            Font fBold18 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);

            // ══════════════════════
            // EN-TÊTE : gauche = République + Ministère + Office, droite = N° + date
            // ══════════════════════
            PdfPTable header = new PdfPTable(new float[]{55f, 45f});
            header.setWidthPercentage(100);
            header.getDefaultCell().setBorder(0);

            PdfPCell left = new PdfPCell();
            left.setBorder(0);
            left.addElement(new Paragraph("REPUBLIQUE DU SENEGAL", fBold12));
            PdfPTable underline = new PdfPTable(1);
            underline.setWidthPercentage(55);
            underline.setHorizontalAlignment(Element.ALIGN_LEFT);
            PdfPCell uc = new PdfPCell(new Phrase(""));
            uc.setBorderWidthBottom(1f); uc.setBorderWidthTop(0);
            uc.setBorderWidthLeft(0); uc.setBorderWidthRight(0);
            uc.setFixedHeight(4f);
            underline.addCell(uc);
            left.addElement(underline);

            try {
                ClassPathResource flagFile = new ClassPathResource("images/drapeau.png");
                if (flagFile.exists()) {
                    Image flag = Image.getInstance(flagFile.getInputStream().readAllBytes());
                    flag.scaleToFit(50f, 35f);
                    left.addElement(flag);
                }
            } catch (Exception e) {
                log.warn("Drapeau non trouvé (images/drapeau.png)", e);
            }

            left.addElement(new Paragraph("Un Peuple – Un But – Une Foi", fItalic10));
            left.addElement(new Paragraph(" ", fNorm11));
            left.addElement(new Paragraph("Ministère de l'Enseignement", fBold11));
            left.addElement(new Paragraph("supérieur, de la Recherche et de", fBold11));
            left.addElement(new Paragraph("l'Innovation", fBold11));
            left.addElement(new Paragraph(" ", fNorm11));
            left.addElement(new Paragraph("OFFICE DU BACCALAUREAT", fBold11));
            header.addCell(left);

            PdfPCell right = new PdfPCell();
            right.setBorder(0);
            right.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Paragraph numero = new Paragraph("N° .......................... MESRI/SG/OB/DOB/kt", fNorm11);
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

            corpsCell.addElement(new Paragraph("Le Directeur de l'Office du Baccalauréat autorise :", fNorm11));
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
            // SIGNATURE (reprend exactement la structure de ConvocationItextService.buildSignature)
            // ══════════════════════
            PdfPTable sigTable = new PdfPTable(new float[]{50f, 50f});
            sigTable.setWidthPercentage(100);

            PdfPCell leftRef = new PdfPCell();
            leftRef.setBorder(0);
            leftRef.addElement(new Paragraph(" ", fNorm11));
            sigTable.addCell(leftRef);

            PdfPCell rightSig = new PdfPCell();
            rightSig.setBorder(0);
            rightSig.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Paragraph faitA = new Paragraph("Fait à Dakar, le " + dateEmission.format(DATE_COURTE), fNorm11);
            faitA.setSpacingAfter(0f);
            rightSig.addElement(faitA);
            Paragraph directeurLabel = new Paragraph("Le Directeur", fNorm11);
            directeurLabel.setIndentationRight(40f);
            directeurLabel.setSpacingAfter(0f);
            rightSig.addElement(directeurLabel);
            sigTable.addCell(rightSig);

            doc.add(sigTable);

            try {
                ClassPathResource sigFile = new ClassPathResource("images/signature.png");
                if (sigFile.exists()) {
                    PdfPTable sigImgTable = new PdfPTable(1);
                    sigImgTable.setWidthPercentage(100);
                    PdfPCell sigImgCell = new PdfPCell();
                    sigImgCell.setBorder(0);
                    sigImgCell.setPaddingRight(35f);
                    sigImgCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    Image signature = Image.getInstance(sigFile.getInputStream().readAllBytes());
                    signature.scaleAbsoluteWidth(70f);
                    sigImgCell.addElement(signature);
                    sigImgTable.addCell(sigImgCell);
                    doc.add(sigImgTable);
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
