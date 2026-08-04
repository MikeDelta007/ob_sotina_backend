package com.officedubac.project.modules.F1_Deuxieme_Partie.pdf;

import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.officedubac.project.modules.F1_Deuxieme_Partie.model.*;
import com.officedubac.project.modules.F1_Deuxieme_Partie.model.Enums.DecisionJury;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

import static com.officedubac.project.modules.F1_Deuxieme_Partie.pdf.ReleveF1DeuxiemePartieCoordinates.*;

/**
 * Génère le PDF du relevé F1 2ème Partie en superposant les données saisies
 * sur une copie du formulaire officiel scanné (fond de page), aux
 * coordonnées mesurées dans {@link ReleveF1DeuxiemePartieCoordinates}.
 */
@Service
public class ReleveF1DeuxiemePartiePdfService {

    private static final String TEMPLATE_PATH = "templates/releve-F1-2emePartie-template.pdf";
    private static final DateTimeFormatter DATE_FR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] genererPdf(ReleveF1DeuxiemePartie releve) {
        try (InputStream templateStream = new ClassPathResource(TEMPLATE_PATH).getInputStream();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PdfReader reader = new PdfReader(templateStream);
            PdfStamper stamper = new PdfStamper(reader, out);

            BaseFont font = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            PdfContentByte cb = stamper.getOverContent(1);

            ecrireEnTete(cb, font, releve);
            ecrireCandidat(cb, font, releve);
            ecrireEpreuvesEcrites(cb, font, releve);
            ecrireEpreuvesOrales(cb, font, releve);
            ecrireDecision(cb, font, releve);

            stamper.close();
            reader.close();
            return out.toByteArray();
        } catch (IOException | com.lowagie.text.DocumentException e) {
            throw new IllegalStateException("Erreur lors de la génération du relevé F1 2ème Partie en PDF: " + e.getMessage(), e);
        }
    }

    // ---------------------------------------------------------------

    private void texte(PdfContentByte cb, BaseFont font, float size, float x, float y, String valeur) {
        if (valeur == null || valeur.isBlank()) return;
        cb.beginText();
        cb.setFontAndSize(font, size);
        cb.setTextMatrix(x, y);
        cb.showText(valeur);
        cb.endText();
    }

    private void coche(PdfContentByte cb, float x, float y) {
        cb.saveState();
        cb.setLineWidth(1.1f);
        cb.moveTo(x, y);
        cb.lineTo(x + 6, y + 8);
        cb.moveTo(x, y + 8);
        cb.lineTo(x + 6, y);
        cb.stroke();
        cb.restoreState();
    }

    private void ecrireEnTete(PdfContentByte cb, BaseFont font, ReleveF1DeuxiemePartie r) {
        texte(cb, font, 9, JURY_NUMERO_X, JURY_NUMERO_Y, r.getJuryNumero());
    }

    private void ecrireCandidat(PdfContentByte cb, BaseFont font, ReleveF1DeuxiemePartie r) {
        Candidat c = r.getCandidat();
        if (c == null) return;
        texte(cb, font, 10, NOM_PRENOM_X, NOM_PRENOM_Y, c.getNomPrenom());
        texte(cb, font, 9, DATE_NAISSANCE_X, DATE_NAISSANCE_Y,
                c.getDateNaissance() == null ? null : c.getDateNaissance().format(DATE_FR));
        texte(cb, font, 9, LIEU_NAISSANCE_X, LIEU_NAISSANCE_Y, c.getLieuNaissance());
    }

    private void ecrireEpreuvesEcrites(PdfContentByte cb, BaseFont font, ReleveF1DeuxiemePartie r) {
        Map<String, NoteEpreuve> parCode = r.getNotesEcrites().stream()
                .collect(Collectors.toMap(NoteEpreuve::getMatiereCode, n -> n));

        ligneNote(cb, font, parCode.get(MatieresF1DeuxiemePartie.MATHS.getCode()),            EC_NOTE_X, EC_POINTS_X, EC_MATHS_Y);
        ligneNote(cb, font, parCode.get(MatieresF1DeuxiemePartie.ELECTRIC_METALLU.getCode()),  EC_NOTE_X, EC_POINTS_X, EC_ELECMET_Y);
        ligneNote(cb, font, parCode.get(MatieresF1DeuxiemePartie.MECAN.getCode()),             EC_NOTE_X, EC_POINTS_X, EC_MECAN_Y);
        ligneNote(cb, font, parCode.get(MatieresF1DeuxiemePartie.ETUDE_OU_PROJET.getCode()),   EC_NOTE_X, EC_POINTS_X, EC_ETUDEPROJ_Y);
        ligneNote(cb, font, parCode.get(MatieresF1DeuxiemePartie.A_FABR_E_DOUT.getCode()),     EC_NOTE_X, EC_POINTS_X, EC_AFABR_Y);

        texte(cb, font, 9, EC_TOTAL_POINTS_X, EC_TOTAL_Y, str(r.getTotalEcrit()));
    }

    private void ecrireEpreuvesOrales(PdfContentByte cb, BaseFont font, ReleveF1DeuxiemePartie r) {
        Map<String, NoteEpreuve> parCode = r.getNotesOrales().stream()
                .collect(Collectors.toMap(NoteEpreuve::getMatiereCode, n -> n));

        ligneNote(cb, font, parCode.get(MatieresF1DeuxiemePartie.AUTOMATISMES.getCode()),   OR_NOTE_X, OR_POINTS_X, OR_AUTOM_Y);
        ligneNote(cb, font, parCode.get(MatieresF1DeuxiemePartie.TECHNOLOGIE.getCode()),    OR_NOTE_X, OR_POINTS_X, OR_TECHNO_Y);
        ligneNote(cb, font, parCode.get(MatieresF1DeuxiemePartie.LV.getCode()),             OR_NOTE_X, OR_POINTS_X, OR_LV_Y);
        ligneNote(cb, font, parCode.get(MatieresF1DeuxiemePartie.EPREUVE_ATELIER.getCode()),OR_NOTE_X, OR_POINTS_X, OR_ATELIER_Y);

        texte(cb, font, 9, OR_TOTAL_POINTS_X, OR_TOTAL_Y, str(r.getTotalOral()));
    }

    private void ligneNote(PdfContentByte cb, BaseFont font, NoteEpreuve note, float xNote, float xPoints, float y) {
        if (note == null) return;
        texte(cb, font, 9, xNote, y, note.getNote() == null ? null : String.valueOf(note.getNote()));
        texte(cb, font, 9, xPoints, y, note.getPointsObtenus() == null ? null : String.valueOf(note.getPointsObtenus()));
    }

    private void ecrireDecision(PdfContentByte cb, BaseFont font, ReleveF1DeuxiemePartie r) {
        DecisionJury decision = r.getDecision();
        if (decision == DecisionJury.ADMIS) {
            coche(cb, COCHE_ADMIS_X, COCHE_ADMIS_Y);
        } else if (decision == DecisionJury.AJOURNE) {
            coche(cb, COCHE_AJOURNE_X, COCHE_AJOURNE_Y);
        } else if (decision == DecisionJury.DEUXIEME_SESSION) {
            coche(cb, COCHE_2EME_SESSION_X, COCHE_2EME_SESSION_Y);
        }

        texte(cb, font, 9, LIEU_DELIVRANCE_X, LIEU_DELIVRANCE_Y, r.getLieuDelivrance());
        texte(cb, font, 9, DATE_DELIVRANCE_X, DATE_DELIVRANCE_Y,
                r.getDateDelivrance() == null ? null : r.getDateDelivrance().format(DATE_FR));
        texte(cb, font, 9, PRESIDENT_JURY_X, PRESIDENT_JURY_Y, r.getPresidentJury());
    }

    private String str(Integer v) {
        return v == null ? null : String.valueOf(v);
    }
}
