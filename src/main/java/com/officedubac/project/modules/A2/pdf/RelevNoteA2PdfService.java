package com.officedubac.project.modules.A2.pdf;

import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.officedubac.project.modules.A2.model.Enums.DecisionJury;
import com.officedubac.project.modules.A2.model.Enums.Mention;
import com.officedubac.project.modules.A2.model.Enums.TypeSession;
import com.officedubac.project.modules.A2.model.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

import static com.officedubac.project.modules.A2.pdf.RelevNoteA2Coordinates.*;

@Service
public class RelevNoteA2PdfService {

    private static final String TEMPLATE_PATH = "templates/releve-A2-template.pdf";
    private static final String POLICE_PATH = "fonts/Verdana.ttf";

    private static final DateTimeFormatter DATE_JOUR_MOIS = DateTimeFormatter.ofPattern("dd/MM");
    private static final DateTimeFormatter DATE_ANNEE_2_CHIFFRES = DateTimeFormatter.ofPattern("yy");

    // Tailles de police par contexte (adaptées à l'espace disponible dans chaque cellule)
    private static final float TAILLE_ENTETE     = 9f;  // N° table, jury, année
    private static final float TAILLE_IDENTITE   = 9f;  // nom, naissance, étab., options, N/F
    private static final float TAILLE_GRILLE     = 7f;  // notes/points 1er et 2eme groupe
    private static final float TAILLE_CONTROLE   = 7f;  // grille EPR. ORALES de CONTROLE
    private static final float TAILLE_TOTAUX     = 8f;  // 1er/2e TOTAL, TOTAL PROVISOIRE/DEFINITIF
    private static final float TAILLE_FACULT_EP  = 7f;  // facultatives + éducation physique
    private static final float TAILLE_DECISION   = 8f;  // mention
    private static final float TAILLE_PIED_PAGE  = 8f;  // lieu/date/président du jury

    public byte[] genererPdf(RelevNoteA2 releve) {
        try (InputStream templateStream = new ClassPathResource(TEMPLATE_PATH).getInputStream();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PdfReader reader = new PdfReader(templateStream);
            PdfStamper stamper = new PdfStamper(reader, out);

            BaseFont font = chargerPolice();
            PdfContentByte cb = stamper.getOverContent(1);

            ecrireSession(cb, releve);
            ecrireEnTete(cb, font, releve);
            ecrireCandidat(cb, font, releve);
            ecrirePremierGroupe(cb, font, releve);
            ecrireDeuxiemeGroupe(cb, font, releve);
            ecrireEpreuvesControle(cb, font, releve);
            ecrireEpreuvesFacultativesEtEducPhysique(cb, font, releve);
            ecrireTotaux(cb, font, releve);
            ecrireDecisions(cb, font, releve);

            stamper.close();
            reader.close();
            return out.toByteArray();
        } catch (IOException | com.lowagie.text.DocumentException e) {
            throw new IllegalStateException("Erreur lors de la génération du relevé A1 en PDF: " + e.getMessage(), e);
        }
    }

    // ---------------------------------------------------------------
    // Police
    // ---------------------------------------------------------------

    /**
     * Charge Verdana (embarquée) depuis resources/fonts/Verdana.ttf. Si le
     * fichier n'est pas fourni, replie silencieusement sur Helvetica (police
     * standard PDF, aucun souci de licence) pour ne jamais casser la
     * génération — voir le README pour ajouter la vraie police Verdana.
     */
    private BaseFont chargerPolice() {
        try (InputStream in = new ClassPathResource(POLICE_PATH).getInputStream()) {
            byte[] ttf = in.readAllBytes();
            return BaseFont.createFont("Verdana.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED, true, ttf, null);
        } catch (Exception e) {
            try {
                return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            } catch (Exception ex) {
                throw new IllegalStateException("Impossible de charger une police pour le PDF A1", ex);
            }
        }
    }

    // ---------------------------------------------------------------
    // Primitives de dessin
    // ---------------------------------------------------------------

    private void texte(PdfContentByte cb, BaseFont font, float size, float x, float y, String valeur) {
        if (valeur == null || valeur.isBlank()) return;
        cb.beginText();
        cb.setFontAndSize(font, size);
        cb.setTextMatrix(x, y);
        cb.showText(valeur);
        cb.endText();
    }

    /** Écrit le texte centré horizontalement sur centerX (utilisé pour les notes/points en cellule). */
    private void texteCentre(PdfContentByte cb, BaseFont font, float size, float centerX, float y, String valeur) {
        if (valeur == null || valeur.isBlank()) return;
        float largeur = font.getWidthPoint(valeur, size);
        texte(cb, font, size, centerX - largeur / 2f, y, valeur);
    }

    /** Coche (croix) dans une case à cocher, centrée sur (cx, cy). */
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

    /** Barre horizontalement le texte non retenu (ex : session NORMALE / DE REMPLACEMENT). */
    private void barrer(PdfContentByte cb, float x0, float x1, float y) {
        cb.saveState();
        cb.setLineWidth(1f);
        cb.moveTo(x0, y);
        cb.lineTo(x1, y);
        cb.stroke();
        cb.restoreState();
    }

    private String str(Integer v) {
        return v == null ? null : String.valueOf(v);
    }

    // ---------------------------------------------------------------
    // Sections
    // ---------------------------------------------------------------

    private void ecrireSession(PdfContentByte cb, RelevNoteA2 r) {
        TypeSession session = r.getSession();
        if (session == TypeSession.NORMALE) {
            barrer(cb, SESSION_REMPLACEMENT_X0, SESSION_REMPLACEMENT_X1, SESSION_REMPLACEMENT_Y);
        } else if (session == TypeSession.REMPLACEMENT) {
            barrer(cb, SESSION_NORMALE_X0, SESSION_NORMALE_X1, SESSION_NORMALE_Y);
        }
        // session == null : on ne barre rien (aucune indication saisie)
    }

    private void ecrireEnTete(PdfContentByte cb, BaseFont font, RelevNoteA2 r) {
        Candidat c = r.getCandidat();
        texte(cb, font, TAILLE_ENTETE, NUMERO_TABLE_X, NUMERO_TABLE_Y, c == null ? null : c.getNumeroTable());
        texte(cb, font, TAILLE_ENTETE, JURY_NUMERO_X, JURY_NUMERO_Y, r.getJuryNumero());
        texte(cb, font, TAILLE_ENTETE, ANNEE_X, ANNEE_Y, r.getAnnee() == null ? null : String.valueOf(r.getAnnee()));
    }

    private void ecrireCandidat(PdfContentByte cb, BaseFont font, RelevNoteA2 r) {
        Candidat c = r.getCandidat();
        if (c == null) return;
        texte(cb, font, TAILLE_IDENTITE, NOM_PRENOM_X, NOM_PRENOM_Y, c.getNomPrenom());
        texte(cb, font, TAILLE_IDENTITE, DATE_NAISSANCE_X, DATE_NAISSANCE_Y,
                c.getDateNaissance() == null ? null : c.getDateNaissance().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        texte(cb, font, TAILLE_IDENTITE, LIEU_NAISSANCE_X, LIEU_NAISSANCE_Y, c.getLieuNaissance());
        texte(cb, font, TAILLE_IDENTITE, ETABLISSEMENT_X, ETABLISSEMENT_Y, c.getEtablissement());
        texte(cb, font, TAILLE_IDENTITE, INDICATIF_X, INDICATIF_Y, c.getIndicatif());
        texte(cb, font, TAILLE_IDENTITE, OPTIONS_X, OPTIONS_Y, c.getOptions());
        texte(cb, font, TAILLE_IDENTITE, NATIONALITE_X, NATIONALITE_Y, c.getNationalite());
        texte(cb, font, TAILLE_IDENTITE, NOMBRE_DE_FOIS_X, NOMBRE_DE_FOIS_Y, c.getNombreDeFois());
    }

    private void ecrirePremierGroupe(PdfContentByte cb, BaseFont font, RelevNoteA2 r) {
        Map<String, NoteEpreuve> parCode = r.getNotesPremierGroupe().stream()
                .collect(Collectors.toMap(NoteEpreuve::getMatiereCode, n -> n));

        ligneNote(cb, font, parCode.get(MatieresA2.FRANCAIS_ECRIT.getCode()), G1_FR_ECRIT_Y);
        ligneNote(cb, font, parCode.get(MatieresA2.FRANCAIS_ORAL.getCode()),  G1_FR_ORAL_Y);
        ligneNote(cb, font, parCode.get(MatieresA2.PHILOSOPHIE.getCode()),    G1_PHILO_Y);
        ligneNote(cb, font, parCode.get(MatieresA2.LATIN_GREC_1.getCode()),   G1_LATGREC_Y);
        ligneNote(cb, font, parCode.get(MatieresA2.HIST_GEO.getCode()),       G1_HISTGEO_Y);
        ligneNote(cb, font, parCode.get(MatieresA2.LANGUE_VIVANTE.getCode()), G1_LV_Y);

        texteCentre(cb, font, TAILLE_TOTAUX, G1_POINTS_CENTER_X, G1_TOTAL_Y, String.valueOf(r.getTotalPremierGroupe()));
        texteCentre(cb, font, TAILLE_TOTAUX, G1_POINTS_CENTER_X, G2_TOTAL_Y, String.valueOf(r.getTotalPremierGroupe()));
    }

    private void ligneNote(PdfContentByte cb, BaseFont font, NoteEpreuve note, float y) {
        if (note == null) return;
        texteCentre(cb, font, TAILLE_GRILLE, G1_NOTE_CENTER_X, y, note.getNote() == null ? null : String.valueOf(note.getNote()));
        texteCentre(cb, font, TAILLE_GRILLE, G1_POINTS_CENTER_X, y, note.getPointsObtenus() == null ? null : String.valueOf(note.getPointsObtenus()));
    }

    private void ecrireDeuxiemeGroupe(PdfContentByte cb, BaseFont font, RelevNoteA2 r) {
        texteCentre(cb, font, TAILLE_TOTAUX, G2_POINTS_CENTER_X, REPORT_1ER_TOTAL_Y, String.valueOf(r.getReportPremierTotal()));

        Map<String, NoteEpreuve> parCode = r.getNotesDeuxiemeGroupe().stream()
                .collect(Collectors.toMap(NoteEpreuve::getMatiereCode, n -> n));

        ligneNote2(cb, font, parCode.get(MatieresA2.GREC_LATIN_2.getCode()),  G2_GRECLATIN_Y);
        ligneNote2(cb, font, parCode.get(MatieresA2.MATHEMATIQUES.getCode()), G2_MATHS_Y);
    }

    private void ligneNote2(PdfContentByte cb, BaseFont font, NoteEpreuve note, float y) {
        if (note == null) return;
        texteCentre(cb, font, TAILLE_GRILLE, G2_NOTE_CENTER_X, y, note.getNote() == null ? null : String.valueOf(note.getNote()));
        texteCentre(cb, font, TAILLE_GRILLE, G2_POINTS_CENTER_X, y, note.getPointsObtenus() == null ? null : String.valueOf(note.getPointsObtenus()));
    }

    private void ecrireEpreuvesControle(PdfContentByte cb, BaseFont font, RelevNoteA2 r) {
        float y = CTRL_FIRST_ROW_Y;
        for (EpreuveOraleControle ctrl : r.getEpreuvesOralesControle()) {
            texte(cb, font, TAILLE_CONTROLE, CTRL_MATIERE_X, y, ctrl.getMatiereChoisie());
            texteCentre(cb, font, TAILLE_CONTROLE, CTRL_RAPPEL_CENTER_X, y, str(ctrl.getRappelPointsObtenus1erGroupe()));
            texteCentre(cb, font, TAILLE_CONTROLE, CTRL_NOUVELLE_CENTER_X, y, str(ctrl.getNouvelleNoteSur20()));
            texteCentre(cb, font, TAILLE_CONTROLE, CTRL_POINTS_CENTER_X, y, str(ctrl.getPointsObtenusEpreuveControle()));
            texteCentre(cb, font, TAILLE_CONTROLE, CTRL_DIFF_CENTER_X, y, str(ctrl.getDifferenceEnPlus()));
            y -= CTRL_ROW_HEIGHT;
        }
    }

    private void ecrireEpreuvesFacultativesEtEducPhysique(PdfContentByte cb, BaseFont font, RelevNoteA2 r) {
        // ---- Droite : alimentent le TOTAL DEFINITIF ----
        for (EpreuveFacultative f : r.getEpreuvesFacultatives()) {
            if (f.getType() == TypeFacultative.LANGUE) {
                texte(cb, font, TAILLE_FACULT_EP, FAC_LANGUE_X, FAC_LANGUE_Y, str(f.getPointsAuDessusMoyenne()));
                texte(cb, font, TAILLE_FACULT_EP, FAC_LANGUE_GAUCHE_X, FAC_LANGUE_GAUCHE_Y, str(f.getPointsAuDessusMoyenne()));
            } else {
                texte(cb, font, TAILLE_FACULT_EP, FAC_ARTS_X, FAC_ARTS_Y, str(f.getPointsAuDessusMoyenne()));
                texte(cb, font, TAILLE_FACULT_EP, FAC_ARTS_GAUCHE_X, FAC_ARTS_GAUCHE_Y, str(f.getPointsAuDessusMoyenne()));
            }
        }

        EducationPhysique ep = r.getEducationPhysique();
        if (ep != null) {
            texte(cb, font, TAILLE_FACULT_EP, EP_NOTE_X, EP_NOTE_Y, str(ep.getNote()));
            texte(cb, font, TAILLE_FACULT_EP, EP_POS_X, EP_POS_Y, str(ep.getPointsPositifs()));
            texte(cb, font, TAILLE_FACULT_EP, EP_NEG_X, EP_NEG_Y, str(ep.getPointsNegatifs()));
            // Gauche : la case "Education Physique" du bloc facultatif reprend le bonus (alimente le 2ème TOTAL)
            texte(cb, font, TAILLE_FACULT_EP, FAC_EDUCPHYS_GAUCHE_X, FAC_EDUCPHYS_GAUCHE_Y, str(ep.getPointsPositifs()));
        }
    }

    private void ecrireTotaux(PdfContentByte cb, BaseFont font, RelevNoteA2 r) {
        texteCentre(cb, font, TAILLE_TOTAUX, TOTAL_PROVISOIRE_X, TOTAL_PROVISOIRE_Y, str(r.getTotalProvisoire()));
        texteCentre(cb, font, TAILLE_TOTAUX, G2_POINTS_CENTER_X, TOTAL_DEFINITIF_Y, str(r.getTotalDefinitif()));
    }

    private void ecrireDecisions(PdfContentByte cb, BaseFont font, RelevNoteA2 r) {
        // ---- 1er groupe ----
        DecisionJury d1 = r.getDecisionPremierGroupe();
        if (d1 == DecisionJury.ADMIS) {
            coche(cb, DEC1_COCHE_ADMIS_CX, DEC1_COCHE_ADMIS_CY);
            texte(cb, font, TAILLE_DECISION, DEC1_MENTION_X, DEC1_MENTION_Y, libelleMention(r.getMentionPremierGroupe()));
        } else if (d1 == DecisionJury.AUTORISE_SECOND_GROUPE) {
            coche(cb, DEC1_COCHE_AUTORISE_CX, DEC1_COCHE_AUTORISE_CY);
        } else if (d1 == DecisionJury.AJOURNE) {
            coche(cb, DEC1_COCHE_AJOURNE_CX, DEC1_COCHE_AJOURNE_CY);
        }
        ecrirePiedDePage1erGroupe(cb, font, r);

        // ---- 2eme groupe ----
        DecisionJury d2 = r.getDecisionDeuxiemeGroupe();
        if (d2 == DecisionJury.ADMIS) {
            coche(cb, DEC2_COCHE_ADMIS_CX, DEC2_COCHE_ADMIS_CY);
            texte(cb, font, TAILLE_DECISION, DEC2_MENTION_X, DEC2_MENTION_Y, libelleMention(r.getMentionDeuxiemeGroupe()));
        } else if (d2 == DecisionJury.AJOURNE) {
            coche(cb, DEC2_COCHE_AJOURNE_CX, DEC2_COCHE_AJOURNE_CY);
        }
        ecrirePiedDePage2emeGroupe(cb, font, r);
    }

    private void ecrirePiedDePage1erGroupe(PdfContentByte cb, BaseFont font, RelevNoteA2 r) {
        texte(cb, font, TAILLE_PIED_PAGE, DEC1_LIEU_X, DEC1_LIEU_Y, r.getLieuDelivrance());
        if (r.getDateDelivrance() != null) {
            texte(cb, font, TAILLE_PIED_PAGE, DEC1_JOUR_MOIS_X, DEC1_JOUR_MOIS_Y, r.getDateDelivrance().format(DATE_JOUR_MOIS));
            texte(cb, font, TAILLE_PIED_PAGE, DEC1_ANNEE2_X, DEC1_ANNEE2_Y, r.getDateDelivrance().format(DATE_ANNEE_2_CHIFFRES));
        }
        // Le nom du Président du Jury n'est pas imprimé ici : voir RelevNoteA1Coordinates
    }

    private void ecrirePiedDePage2emeGroupe(PdfContentByte cb, BaseFont font, RelevNoteA2 r) {
        texte(cb, font, TAILLE_PIED_PAGE, DEC2_LIEU_X, DEC2_LIEU_Y, r.getLieuDelivrance());
        if (r.getDateDelivrance() != null) {
            texte(cb, font, TAILLE_PIED_PAGE, DEC2_JOUR_MOIS_X, DEC2_JOUR_MOIS_Y, r.getDateDelivrance().format(DATE_JOUR_MOIS));
            texte(cb, font, TAILLE_PIED_PAGE, DEC2_ANNEE2_X, DEC2_ANNEE2_Y, r.getDateDelivrance().format(DATE_ANNEE_2_CHIFFRES));
        }
        // Le nom du Président du Jury n'est pas imprimé ici : voir RelevNoteA1Coordinates
    }

    private String libelleMention(Mention m) {
        if (m == null) return null;
        return switch (m) {
            case PASSABLE -> "PASSABLE";
            case ASSEZ_BIEN -> "ASSEZ BIEN";
            case BIEN -> "BIEN";
            case TRES_BIEN -> "TRES BIEN";
            case AUCUNE -> null;
        };
    }
}
