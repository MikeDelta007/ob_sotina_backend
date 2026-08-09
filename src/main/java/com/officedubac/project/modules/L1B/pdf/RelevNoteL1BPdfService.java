package com.officedubac.project.modules.L1B.pdf;

import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.officedubac.project.modules.L1B.model.*;
import com.officedubac.project.modules.L1B.model.Enums.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.officedubac.project.modules.L1B.pdf.RelevNoteL1BCoordinates.*;

/**
 * Génère le PDF du relevé L1B en superposant les données saisies sur le
 * gabarit officiel (fond de page), aux coordonnées mesurées dans {@link RelevNoteL1BCoordinates}.
 */
@Service
public class RelevNoteL1BPdfService {

    private static final String TEMPLATE_PATH = "templates/releve-L1B-template.pdf";
    private static final String POLICE_PATH = "fonts/Verdana.ttf";

    private static final DateTimeFormatter DATE_JOUR_MOIS = DateTimeFormatter.ofPattern("dd/MM");
    private static final DateTimeFormatter DATE_ANNEE_2_CHIFFRES = DateTimeFormatter.ofPattern("yy");
    private static final DateTimeFormatter DATE_NAISSANCE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final float TAILLE_ENTETE = 9f;
    private static final float TAILLE_IDENTITE = 9f;
    private static final float TAILLE_GRILLE = 7f;
    private static final float TAILLE_CONTROLE = 7f;
    private static final float TAILLE_TOTAUX = 8f;
    private static final float TAILLE_FACULT_EP = 7f;
    private static final float TAILLE_DECISION = 8f;
    private static final float TAILLE_PIED_PAGE = 8f;

    public byte[] genererPdf(RelevNoteL1B releve) {
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
            throw new IllegalStateException("Erreur lors de la génération du relevé L1B en PDF: " + e.getMessage(), e);
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
                throw new IllegalStateException("Impossible de charger une police pour le PDF L1B", ex);
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

    private void ecrireSession(PdfContentByte cb, RelevNoteL1B r) {
        TypeSession session = r.getSession();
        if (session == TypeSession.NORMALE) {
            barrer(cb, SESSION_REMPLACEMENT_X0, SESSION_REMPLACEMENT_X1, SESSION_REMPLACEMENT_Y);
        } else if (session == TypeSession.REMPLACEMENT) {
            barrer(cb, SESSION_NORMALE_X0, SESSION_NORMALE_X1, SESSION_NORMALE_Y);
        }
    }

    private void ecrireEnTete(PdfContentByte cb, BaseFont font, RelevNoteL1B r) {
        Candidat c = r.getCandidat();
        texte(cb, font, TAILLE_ENTETE, NUMERO_TABLE_X, NUMERO_TABLE_Y, c == null ? null : c.getNumeroTable());
        texte(cb, font, TAILLE_ENTETE, JURY_NUMERO_X, JURY_NUMERO_Y, r.getJuryNumero());
        texte(cb, font, TAILLE_ENTETE, ANNEE_X, ANNEE_Y, r.getAnnee() == null ? null : String.valueOf(r.getAnnee()));
    }

    private void ecrireCandidat(PdfContentByte cb, BaseFont font, RelevNoteL1B r) {
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

    private void ecrirePremierGroupe(PdfContentByte cb, BaseFont font, RelevNoteL1B r) {
        Map<String, NoteEpreuve> parCode = r.getNotesPremierGroupe().stream()
                .collect(Collectors.toMap(NoteEpreuve::getMatiereCode, n -> n, (a, b) -> a));
        List<Matiere> matieres = MatieresL1B.PREMIER_GROUPE;
        for (int i = 0; i < matieres.size(); i++) {
            ligneNote(cb, font, parCode.get(matieres.get(i).getCode()), G1_NOTE_CENTER_X, G1_POINTS_CENTER_X, G1_ROW_Y[i]);
        }
        texteCentre(cb, font, TAILLE_TOTAUX, G1_POINTS_CENTER_X, G1_TOTAL_Y, str(r.getTotalPremierGroupe()));
        texteCentre(cb, font, TAILLE_TOTAUX, G1_POINTS_CENTER_X, G2_TOTAL_Y, str(r.getTotalPremierGroupe()));
    }

    private void ligneNote(PdfContentByte cb, BaseFont font, NoteEpreuve note, float xNote, float xPoints, float y) {
        if (note == null) return;
        texteCentre(cb, font, TAILLE_GRILLE, xNote, y, note.getNote() == null ? null : String.valueOf(note.getNote()));
        texteCentre(cb, font, TAILLE_GRILLE, xPoints, y, note.getPointsObtenus() == null ? null : String.valueOf(note.getPointsObtenus()));
    }

    private void ecrireDeuxiemeGroupe(PdfContentByte cb, BaseFont font, RelevNoteL1B r) {
        texteCentre(cb, font, TAILLE_TOTAUX, G2_POINTS_CENTER_X, REPORT_1ER_TOTAL_Y, str(r.getReportPremierTotal()));
        Map<String, NoteEpreuve> parCode = r.getNotesDeuxiemeGroupe().stream()
                .collect(Collectors.toMap(NoteEpreuve::getMatiereCode, n -> n, (a, b) -> a));
        List<Matiere> matieres = MatieresL1B.DEUXIEME_GROUPE;
        for (int i = 0; i < matieres.size(); i++) {
            ligneNote(cb, font, parCode.get(matieres.get(i).getCode()), G2_NOTE_CENTER_X, G2_POINTS_CENTER_X, G2_ROW_Y[i]);
        }
    }

    private void ecrireEpreuvesControle(PdfContentByte cb, BaseFont font, RelevNoteL1B r) {
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

    private void ecrireEpreuvesFacultativesEtEducPhysique(PdfContentByte cb, BaseFont font, RelevNoteL1B r) {
        for (EpreuveFacultative fac : r.getEpreuvesFacultatives()) {
            if (fac.getType() == TypeFacultative.LANGUE) {
                texte(cb, font, TAILLE_FACULT_EP, FAC_LANGUE_X, FAC_LANGUE_Y, str(fac.getPointsAuDessusMoyenne()));
                texte(cb, font, TAILLE_FACULT_EP, FAC_LANGUE_GAUCHE_X, FAC_LANGUE_GAUCHE_Y, str(fac.getPointsAuDessusMoyenne()));
            } else {
                texte(cb, font, TAILLE_FACULT_EP, FAC_ARTS_X, FAC_ARTS_Y, str(fac.getPointsAuDessusMoyenne()));
                texte(cb, font, TAILLE_FACULT_EP, FAC_ARTS_GAUCHE_X, FAC_ARTS_GAUCHE_Y, str(fac.getPointsAuDessusMoyenne()));
            }
        }

        EducationPhysique ep = r.getEducationPhysique();
        if (ep != null) {
            texte(cb, font, TAILLE_FACULT_EP, EP_NOTE_X, EP_NOTE_Y, str(ep.getNote()));
            texte(cb, font, TAILLE_FACULT_EP, EP_POS_X, EP_POS_Y, str(ep.getPointsPositifs()));
            texte(cb, font, TAILLE_FACULT_EP, EP_NEG_X, EP_NEG_Y, str(ep.getPointsNegatifs()));
            texte(cb, font, TAILLE_FACULT_EP, FAC_EDUCPHYS_GAUCHE_X, FAC_EDUCPHYS_GAUCHE_Y, str(ep.getPointsPositifs()));
        }
    }

    private void ecrireTotaux(PdfContentByte cb, BaseFont font, RelevNoteL1B r) {
        texteCentre(cb, font, TAILLE_TOTAUX, G2_POINTS_CENTER_X, TOTAL_DEFINITIF_Y, str(r.getTotalDefinitif()));
    }

    private void ecrireDecisions(PdfContentByte cb, BaseFont font, RelevNoteL1B r) {
        DecisionJury d1 = r.getDecisionPremierGroupe();
        texte(cb, font, TAILLE_DECISION, DEC1_TEXTE_X, DEC1_TEXTE_Y, libelleDecision1(d1, r.getMentionPremierGroupe()));
        ecrirePiedDePage(cb, font, r, DEC1_LIEU_X, DEC1_JOUR_MOIS_X, DEC1_ANNEE2_X, DEC1_PIED_Y);

        DecisionJury d2 = r.getDecisionDeuxiemeGroupe();
        texte(cb, font, TAILLE_DECISION, DEC2_TEXTE_X, DEC2_TEXTE_Y, libelleDecision2(d2, r.getMentionDeuxiemeGroupe()));
        ecrirePiedDePage(cb, font, r, DEC2_LIEU_X, DEC2_JOUR_MOIS_X, DEC2_ANNEE2_X, DEC2_PIED_Y);
    }

    private void ecrirePiedDePage(PdfContentByte cb, BaseFont font, RelevNoteL1B r, float lieuX, float jourMoisX, float annee2X, float y) {
        texte(cb, font, TAILLE_PIED_PAGE, lieuX, y, r.getLieuDelivrance());
        if (r.getDateDelivrance() != null) {
            texte(cb, font, TAILLE_PIED_PAGE, jourMoisX, y, r.getDateDelivrance().format(DATE_JOUR_MOIS));
            texte(cb, font, TAILLE_PIED_PAGE, annee2X, y, r.getDateDelivrance().format(DATE_ANNEE_2_CHIFFRES));
        }
    }

    private String libelleDecision1(DecisionJury d, Mention mention) {
        if (d == null) return null;
        return switch (d) {
            case ADMIS -> "ADMIS" + (mention == null || mention == Mention.AUCUNE ? "" : " - Mention " + libelleMention(mention));
            case AUTORISE_SECOND_GROUPE -> "AUTORISE A SUBIR LE 2EME GROUPE D'EPREUVES";
            case AJOURNE -> "AJOURNE";
        };
    }

    private String libelleDecision2(DecisionJury d, Mention mention) {
        if (d == null) return null;
        return switch (d) {
            case ADMIS -> "ADMIS" + (mention == null || mention == Mention.AUCUNE ? "" : " - Mention " + libelleMention(mention));
            case AJOURNE -> "AJOURNE";
            case AUTORISE_SECOND_GROUPE -> null;
        };
    }

    private String libelleMention(Mention m) {
        return switch (m) {
            case PASSABLE -> "PASSABLE";
            case ASSEZ_BIEN -> "ASSEZ BIEN";
            case BIEN -> "BIEN";
            case TRES_BIEN -> "TRES BIEN";
            case AUCUNE -> null;
        };
    }
}
