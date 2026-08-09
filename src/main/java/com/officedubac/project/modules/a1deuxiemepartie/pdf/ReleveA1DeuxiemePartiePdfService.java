package com.officedubac.project.modules.a1deuxiemepartie.pdf;

import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.officedubac.project.modules.a1deuxiemepartie.model.*;
import com.officedubac.project.modules.a1deuxiemepartie.model.Enums.DecisionJury;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

import static com.officedubac.project.modules.a1deuxiemepartie.pdf.ReleveA1DeuxiemePartieCoordinates.*;

/**
 * Génère le PDF du relevé A1 2ème Partie en superposant les données saisies
 * sur le nouveau gabarit vectoriel (fond de page).
 *
 * Police : DejaVu Sans embarquée (substitut libre à Verdana — voir README),
 * repli automatique sur Helvetica si le fichier de police est absent.
 */
@Service
public class ReleveA1DeuxiemePartiePdfService {

    private static final String TEMPLATE_PATH = "templates/releve-A1-2emePartie-template.pdf";
    private static final String POLICE_PATH = "fonts/DejaVuSans.ttf";

    private static final DateTimeFormatter DATE_JOUR_MOIS = DateTimeFormatter.ofPattern("dd/MM");
    private static final DateTimeFormatter DATE_ANNEE_2_CHIFFRES = DateTimeFormatter.ofPattern("yy");
    private static final DateTimeFormatter DATE_NAISSANCE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final float TAILLE_ENTETE    = 9f;
    private static final float TAILLE_IDENTITE  = 9f;
    private static final float TAILLE_GRILLE    = 7f;
    private static final float TAILLE_TOTAUX    = 9f;
    private static final float TAILLE_TOTAL_GEN = 10f;
    private static final float TAILLE_PIED_PAGE = 9f;

    public byte[] genererPdf(ReleveA1DeuxiemePartie releve) {
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
            ecrireDecision(cb, releve);
            ecrirePiedDePage(cb, font, releve);

            stamper.close();
            reader.close();
            return out.toByteArray();
        } catch (IOException | com.lowagie.text.DocumentException e) {
            throw new IllegalStateException("Erreur lors de la génération du relevé A1 2ème Partie en PDF: " + e.getMessage(), e);
        }
    }

    // ---------------------------------------------------------------
    // Police
    // ---------------------------------------------------------------

    private BaseFont chargerPolice() {
        try (InputStream in = new ClassPathResource(POLICE_PATH).getInputStream()) {
            byte[] ttf = in.readAllBytes();
            return BaseFont.createFont("DejaVuSans.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED, true, ttf, null);
        } catch (Exception e) {
            try {
                return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            } catch (Exception ex) {
                throw new IllegalStateException("Impossible de charger une police pour le PDF A1 2ème Partie", ex);
            }
        }
    }

    // ---------------------------------------------------------------
    // Primitives
    // ---------------------------------------------------------------

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

    private void coche(PdfContentByte cb, float cx, float cy) {
        float demi = 5f;
        cb.saveState();
        cb.setLineWidth(1.2f);
        cb.moveTo(cx - demi, cy - demi);
        cb.lineTo(cx + demi, cy + demi);
        cb.moveTo(cx - demi, cy + demi);
        cb.lineTo(cx + demi, cy - demi);
        cb.stroke();
        cb.restoreState();
    }

    private String str(Integer v) {
        return v == null ? null : String.valueOf(v);
    }

    // ---------------------------------------------------------------
    // Sections
    // ---------------------------------------------------------------

    private void ecrireEnTete(PdfContentByte cb, BaseFont font, ReleveA1DeuxiemePartie r) {
        texte(cb, font, TAILLE_ENTETE, JURY_NUMERO_X, JURY_NUMERO_Y, r.getJuryNumero());
        Candidat c = r.getCandidat();
        texte(cb, font, TAILLE_ENTETE, NUMERO_TABLE_X, NUMERO_TABLE_Y, c == null ? null : c.getNumeroTable());
    }

    private void ecrireCandidat(PdfContentByte cb, BaseFont font, ReleveA1DeuxiemePartie r) {
        Candidat c = r.getCandidat();
        if (c == null) return;
        texte(cb, font, TAILLE_IDENTITE, NOM_PRENOM_X, NOM_PRENOM_Y, c.getNomPrenom());
        texte(cb, font, TAILLE_IDENTITE, DATE_NAISSANCE_X, DATE_NAISSANCE_Y,
                c.getDateNaissance() == null ? null : c.getDateNaissance().format(DATE_NAISSANCE_FORMAT));
        texte(cb, font, TAILLE_IDENTITE, LIEU_NAISSANCE_X, LIEU_NAISSANCE_Y, c.getLieuNaissance());
        texte(cb, font, TAILLE_IDENTITE, ETABLISSEMENT_X, ETABLISSEMENT_Y, c.getEtablissement());
        texte(cb, font, TAILLE_IDENTITE, INDICATIF_X, INDICATIF_Y, c.getIndicatif());
        texte(cb, font, TAILLE_IDENTITE, OPTIONS_X, OPTIONS_Y, c.getOptions());
        texte(cb, font, TAILLE_IDENTITE, NATIONALITE_X, NATIONALITE_Y, c.getNationalite());
        texte(cb, font, TAILLE_IDENTITE, NOMBRE_DE_FOIS_X, NOMBRE_DE_FOIS_Y, c.getNombreDeFois());
    }

    private void ecrireEpreuvesEcrites(PdfContentByte cb, BaseFont font, ReleveA1DeuxiemePartie r) {
        Map<String, NoteEpreuve> parCode = r.getNotesEcrites().stream()
                .collect(Collectors.toMap(NoteEpreuve::getMatiereCode, n -> n));

        ligneNote(cb, font, parCode.get(MatieresA1DeuxiemePartie.PHILOSOPHIE.getCode()), EC_NOTE_CENTER_X, EC_POINTS_CENTER_X, EC_PHILO_Y);
        ligneNote(cb, font, parCode.get(MatieresA1DeuxiemePartie.LATIN_ARABE.getCode()), EC_NOTE_CENTER_X, EC_POINTS_CENTER_X, EC_LATAR_Y);
        ligneNote(cb, font, parCode.get(MatieresA1DeuxiemePartie.GREC.getCode()),        EC_NOTE_CENTER_X, EC_POINTS_CENTER_X, EC_GREC_Y);
        ligneNote(cb, font, parCode.get(MatieresA1DeuxiemePartie.LANGUE_VIVANTE.getCode()), EC_NOTE_CENTER_X, EC_POINTS_CENTER_X, EC_LV_Y);

        texteCentre(cb, font, TAILLE_TOTAUX, EC_POINTS_CENTER_X, EC_TOTAL_Y, str(r.getTotalEcrit()));
    }

    private void ecrireEpreuvesOrales(PdfContentByte cb, BaseFont font, ReleveA1DeuxiemePartie r) {
        Map<String, NoteEpreuve> parCode = r.getNotesOrales().stream()
                .collect(Collectors.toMap(NoteEpreuve::getMatiereCode, n -> n));

        ligneNote(cb, font, parCode.get(MatieresA1DeuxiemePartie.LATIN_GREC_ARABE_ORAL.getCode()), OR_NOTE_CENTER_X, OR_POINTS_CENTER_X, OR_LATGRECARABE_Y);
        ligneNote(cb, font, parCode.get(MatieresA1DeuxiemePartie.HIST_GEO.getCode()),               OR_NOTE_CENTER_X, OR_POINTS_CENTER_X, OR_HISTGEO_Y);
        ligneNote(cb, font, parCode.get(MatieresA1DeuxiemePartie.MATHEMATIQUES.getCode()),          OR_NOTE_CENTER_X, OR_POINTS_CENTER_X, OR_MATH_Y);

        texteCentre(cb, font, TAILLE_TOTAUX, OR_POINTS_CENTER_X, OR_TOTAL_Y, str(r.getTotalOral()));
    }

    private void ligneNote(PdfContentByte cb, BaseFont font, NoteEpreuve note, float xNoteCenter, float xPointsCenter, float y) {
        if (note == null) return;
        texteCentre(cb, font, TAILLE_GRILLE, xNoteCenter, y, note.getNote() == null ? null : String.valueOf(note.getNote()));
        texteCentre(cb, font, TAILLE_GRILLE, xPointsCenter, y, note.getPointsObtenus() == null ? null : String.valueOf(note.getPointsObtenus()));
    }

    private void ecrireTotalGeneral(PdfContentByte cb, BaseFont font, ReleveA1DeuxiemePartie r) {
        texte(cb, font, TAILLE_TOTAL_GEN, TOTAL_GENERAL_X, TOTAL_GENERAL_Y, str(r.getTotalGeneral()));
    }

    private void ecrireDecision(PdfContentByte cb, ReleveA1DeuxiemePartie r) {
        DecisionJury decision = r.getDecision();
        if (decision == DecisionJury.ADMIS) {
            coche(cb, COCHE_ADMIS_CX, COCHE_ADMIS_CY);
        } else if (decision == DecisionJury.AJOURNE) {
            coche(cb, COCHE_AJOURNE_CX, COCHE_AJOURNE_CY);
        }
    }

    private void ecrirePiedDePage(PdfContentByte cb, BaseFont font, ReleveA1DeuxiemePartie r) {
        texte(cb, font, TAILLE_PIED_PAGE, LIEU_DELIVRANCE_X, LIEU_DELIVRANCE_Y, r.getLieuDelivrance());
        if (r.getDateDelivrance() != null) {
            texte(cb, font, TAILLE_PIED_PAGE, JOUR_MOIS_X, JOUR_MOIS_Y, r.getDateDelivrance().format(DATE_JOUR_MOIS));
            texte(cb, font, TAILLE_PIED_PAGE, ANNEE_2_CHIFFRES_X, ANNEE_2_CHIFFRES_Y, r.getDateDelivrance().format(DATE_ANNEE_2_CHIFFRES));
        }
        // Le nom du Président du Jury n'est pas imprimé ici (voir ReleveA1DeuxiemePartieCoordinates)
    }
}
