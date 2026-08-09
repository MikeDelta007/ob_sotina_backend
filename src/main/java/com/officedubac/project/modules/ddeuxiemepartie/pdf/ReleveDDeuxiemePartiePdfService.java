package com.officedubac.project.modules.ddeuxiemepartie.pdf;

import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.officedubac.project.modules.ddeuxiemepartie.model.*;
import com.officedubac.project.modules.ddeuxiemepartie.model.Enums.DecisionJury;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.officedubac.project.modules.ddeuxiemepartie.pdf.ReleveDDeuxiemePartieCoordinates.*;

/**
 * Génère le PDF du relevé D-2emePartie en superposant les données saisies sur le
 * gabarit officiel (fond de page).
 */
@Service
public class ReleveDDeuxiemePartiePdfService {

    private static final String TEMPLATE_PATH = "templates/releve-D-2emePartie-template.pdf";
    private static final String POLICE_PATH = "fonts/Verdana.ttf";

    private static final DateTimeFormatter DATE_JOUR_MOIS = DateTimeFormatter.ofPattern("dd/MM");
    private static final DateTimeFormatter DATE_ANNEE_2_CHIFFRES = DateTimeFormatter.ofPattern("yy");
    private static final DateTimeFormatter DATE_NAISSANCE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final float TAILLE_IDENTITE = 9f;
    private static final float TAILLE_GRILLE = 7f;
    private static final float TAILLE_TOTAUX = 9f;
    private static final float TAILLE_TOTAL_GEN = 10f;
    private static final float TAILLE_DECISION = 9f;
    private static final float TAILLE_PIED_PAGE = 9f;

    public byte[] genererPdf(ReleveDDeuxiemePartie releve) {
        try (InputStream templateStream = new ClassPathResource(TEMPLATE_PATH).getInputStream();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PdfReader reader = new PdfReader(templateStream);
            PdfStamper stamper = new PdfStamper(reader, out);

            BaseFont font = chargerPolice();
            PdfContentByte cb = stamper.getOverContent(1);

            ecrireEnTete(cb, font, releve);
            ecrireCandidat(cb, font, releve);
            ecrireEpreuvesEcrites(cb, font, releve);
            ecrireEpreuvesOrales(cb, font, releve);
            ecrireTotalGeneral(cb, font, releve);
            ecrireEducationPhysique(cb, font, releve);
            ecrireDecision(cb, font, releve);
            ecrirePiedDePage(cb, font, releve);

            stamper.close();
            reader.close();
            return out.toByteArray();
        } catch (IOException | com.lowagie.text.DocumentException e) {
            throw new IllegalStateException("Erreur lors de la génération du relevé D-2emePartie en PDF: " + e.getMessage(), e);
        }
    }

    private BaseFont chargerPolice() {
        try (InputStream in = new ClassPathResource(POLICE_PATH).getInputStream()) {
            byte[] ttf = in.readAllBytes();
            return BaseFont.createFont("Verdana.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED, true, ttf, null);
        } catch (Exception e) {
            try {
                return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            } catch (Exception ex) {
                throw new IllegalStateException("Impossible de charger une police pour le PDF D-2emePartie", ex);
            }
        }
    }

    private void texte(PdfContentByte cb, BaseFont font, float size, float x, float y, String valeur) {
        if (valeur == null || valeur.isBlank()) return;
        cb.beginText();
        cb.setFontAndSize(font, size);
        cb.setTextMatrix(x, y);
        cb.showText(valeur);
        cb.endText();
    }

    private void texteCentre(PdfContentByte cb, BaseFont font, float size, float centerX, float y, String valeur) {
        if (valeur == null || valeur.isBlank()) return;
        float largeur = font.getWidthPoint(valeur, size);
        texte(cb, font, size, centerX - largeur / 2f, y, valeur);
    }

    private String str(Integer v) {
        return v == null ? null : String.valueOf(v);
    }

    private void ecrireEnTete(PdfContentByte cb, BaseFont font, ReleveDDeuxiemePartie r) {
        texte(cb, font, TAILLE_IDENTITE, JURY_NUMERO_X, JURY_NUMERO_Y, r.getJuryNumero());
        Candidat c = r.getCandidat();
        texte(cb, font, TAILLE_IDENTITE, NUMERO_TABLE_X, NUMERO_TABLE_Y, c == null ? null : c.getNumeroTable());
    }

    private void ecrireCandidat(PdfContentByte cb, BaseFont font, ReleveDDeuxiemePartie r) {
        Candidat c = r.getCandidat();
        if (c == null) return;
        texte(cb, font, TAILLE_IDENTITE, NOM_PRENOM_X, NOM_PRENOM_Y, c.getNomPrenom());
        texte(cb, font, TAILLE_IDENTITE, DATE_NAISSANCE_X, DATE_NAISSANCE_Y,
                c.getDateNaissance() == null ? null : c.getDateNaissance().format(DATE_NAISSANCE_FORMAT));
        texte(cb, font, TAILLE_IDENTITE, LIEU_NAISSANCE_X, LIEU_NAISSANCE_Y, c.getLieuNaissance());
    }

    private void ecrireEpreuvesEcrites(PdfContentByte cb, BaseFont font, ReleveDDeuxiemePartie r) {
        Map<String, NoteEpreuve> parCode = r.getNotesEcrites().stream()
                .collect(Collectors.toMap(NoteEpreuve::getMatiereCode, n -> n, (a, b) -> a));
        List<Matiere> matieres = MatieresDDeuxiemePartie.EPREUVES_ECRITES;
        for (int i = 0; i < matieres.size(); i++) {
            ligneNote(cb, font, parCode.get(matieres.get(i).getCode()), EC_NOTE_CENTER_X, EC_POINTS_CENTER_X, EC_ROW_Y[i]);
        }
        texteCentre(cb, font, TAILLE_TOTAUX, EC_POINTS_CENTER_X, EC_TOTAL_Y, str(r.getTotalEcrit()));
    }

    private void ecrireEpreuvesOrales(PdfContentByte cb, BaseFont font, ReleveDDeuxiemePartie r) {
        Map<String, NoteEpreuve> parCode = r.getNotesOrales().stream()
                .collect(Collectors.toMap(NoteEpreuve::getMatiereCode, n -> n, (a, b) -> a));
        List<Matiere> matieres = MatieresDDeuxiemePartie.EPREUVES_ORALES;
        for (int i = 0; i < matieres.size(); i++) {
            ligneNote(cb, font, parCode.get(matieres.get(i).getCode()), OR_NOTE_CENTER_X, OR_POINTS_CENTER_X, OR_ROW_Y[i]);
        }
        texteCentre(cb, font, TAILLE_TOTAUX, OR_POINTS_CENTER_X, OR_TOTAL_Y, str(r.getTotalOral()));
    }

    private void ligneNote(PdfContentByte cb, BaseFont font, NoteEpreuve note, float xNote, float xPoints, float y) {
        if (note == null) return;
        texteCentre(cb, font, TAILLE_GRILLE, xNote, y, note.getNote() == null ? null : String.valueOf(note.getNote()));
        texteCentre(cb, font, TAILLE_GRILLE, xPoints, y, note.getPointsObtenus() == null ? null : String.valueOf(note.getPointsObtenus()));
    }

    private void ecrireTotalGeneral(PdfContentByte cb, BaseFont font, ReleveDDeuxiemePartie r) {
        texte(cb, font, TAILLE_TOTAL_GEN, TOTAL_GENERAL_X, TOTAL_GENERAL_Y, str(r.getTotalGeneral()));
    }

    private void ecrireEducationPhysique(PdfContentByte cb, BaseFont font, ReleveDDeuxiemePartie r) {
        EducationPhysique ep = r.getEducationPhysique();
        if (ep == null) return;
        texte(cb, font, TAILLE_GRILLE, EP_NOTE_X, EP_NOTE_Y, str(ep.getNote()));
        texte(cb, font, TAILLE_GRILLE, EP_POS_X, EP_POS_Y, str(ep.getPointsPositifs()));
        texte(cb, font, TAILLE_GRILLE, EP_NEG_X, EP_NEG_Y, str(ep.getPointsNegatifs()));
    }

    private void ecrireDecision(PdfContentByte cb, BaseFont font, ReleveDDeuxiemePartie r) {
        DecisionJury d = r.getDecision();
        if (d == null) return;
        texte(cb, font, TAILLE_DECISION, DEC_TEXTE_X, DEC_TEXTE_Y, d == DecisionJury.ADMIS ? "ADMIS" : "AJOURNE");
    }

    private void ecrirePiedDePage(PdfContentByte cb, BaseFont font, ReleveDDeuxiemePartie r) {
        texte(cb, font, TAILLE_PIED_PAGE, LIEU_DELIVRANCE_X, PIED_Y, r.getLieuDelivrance());
        if (r.getDateDelivrance() != null) {
            texte(cb, font, TAILLE_PIED_PAGE, JOUR_MOIS_X, PIED_Y, r.getDateDelivrance().format(DATE_JOUR_MOIS));
            texte(cb, font, TAILLE_PIED_PAGE, ANNEE2_X, PIED_Y, r.getDateDelivrance().format(DATE_ANNEE_2_CHIFFRES));
        }
    }
}
