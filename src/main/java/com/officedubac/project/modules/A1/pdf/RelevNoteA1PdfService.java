package com.officedubac.project.modules.A1.pdf;

import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.officedubac.project.modules.A1.model.*;
import com.officedubac.project.modules.A1.model.Enums.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

import static com.officedubac.project.modules.A1.pdf.RelevNoteA1Coordinates.*;

@Service
public class RelevNoteA1PdfService {

    private static final String TEMPLATE_PATH = "templates/releve-A1-template.pdf";
    private static final DateTimeFormatter DATE_FR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] genererPdf(RelevNoteA1 releve) {
        try (InputStream templateStream = new ClassPathResource(TEMPLATE_PATH).getInputStream();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PdfReader reader = new PdfReader(templateStream);
            PdfStamper stamper = new PdfStamper(reader, out);

            BaseFont font = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            PdfContentByte cb = stamper.getOverContent(1);

            ecrireEnTete(cb, font, releve);
            ecrireCandidat(cb, font, releve);
            ecrirePremierGroupe(cb, font, releve);
            ecrireDeuxiemeGroupe(cb, font, releve);
            ecrireEpreuvesControle(cb, font, releve);
            ecrireEpreuvesFacultatives(cb, font, releve);
            ecrireTotaux(cb, font, releve);
            ecrireDecisions(cb, font, releve);

            stamper.close();
            reader.close();
            return out.toByteArray();
        } catch (IOException | com.lowagie.text.DocumentException e) {
            throw new IllegalStateException("Erreur lors de la génération du relevé A1 en PDF", e);
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
        // petite croix "X" en guise de coche
        cb.moveTo(x, y);
        cb.lineTo(x + 6, y + 8);
        cb.moveTo(x, y + 8);
        cb.lineTo(x + 6, y);
        cb.stroke();
        cb.restoreState();
    }

    private void ecrireEnTete(PdfContentByte cb, BaseFont font, RelevNoteA1 r) {
        texte(cb, font, 9, JURY_NUMERO_X, JURY_NUMERO_Y, r.getJuryNumero());
        texte(cb, font, 9, ANNEE_X, ANNEE_Y, r.getAnnee() == null ? null : String.valueOf(r.getAnnee()));
    }

    private void ecrireCandidat(PdfContentByte cb, BaseFont font, RelevNoteA1 r) {
        Candidat c = r.getCandidat();
        if (c == null) return;
        texte(cb, font, 10, NOM_PRENOM_X, NOM_PRENOM_Y, c.getNomPrenom());
        texte(cb, font, 9, DATE_NAISSANCE_X, DATE_NAISSANCE_Y,
                c.getDateNaissance() == null ? null : c.getDateNaissance().format(DATE_FR));
        texte(cb, font, 9, LIEU_NAISSANCE_X, LIEU_NAISSANCE_Y, c.getLieuNaissance());
        texte(cb, font, 9, ETABLISSEMENT_X, ETABLISSEMENT_Y, c.getEtablissement());
        texte(cb, font, 9, INDICATIF_X, INDICATIF_Y, c.getIndicatif());
        texte(cb, font, 9, OPTIONS_X, OPTIONS_Y, c.getOptions());
        texte(cb, font, 9, N_X, N_Y, c.getN());
        texte(cb, font, 9, F_X, F_Y, c.getF());
    }

    private void ecrirePremierGroupe(PdfContentByte cb, BaseFont font, RelevNoteA1 r) {
        Map<String, NoteEpreuve> parCode = r.getNotesPremierGroupe().stream()
                .collect(Collectors.toMap(NoteEpreuve::getMatiereCode, n -> n));

        ligneNote(cb, font, parCode.get(MatieresA1.FRANCAIS_ECRIT.getCode()), G1_NOTE_X, G1_POINTS_X, G1_FR_ECRIT_Y);
        ligneNote(cb, font, parCode.get(MatieresA1.FRANCAIS_ORAL.getCode()),  G1_NOTE_X, G1_POINTS_X, G1_FR_ORAL_Y);
        ligneNote(cb, font, parCode.get(MatieresA1.PHILOSOPHIE.getCode()),    G1_NOTE_X, G1_POINTS_X, G1_PHILO_Y);
        ligneNote(cb, font, parCode.get(MatieresA1.LATIN_GREC_1.getCode()),   G1_NOTE_X, G1_POINTS_X, G1_LATGREC_Y);
        ligneNote(cb, font, parCode.get(MatieresA1.HIST_GEO.getCode()),       G1_NOTE_X, G1_POINTS_X, G1_HISTGEO_Y);
        ligneNote(cb, font, parCode.get(MatieresA1.LANGUE_VIVANTE.getCode()), G1_NOTE_X, G1_POINTS_X, G1_LV_Y);

        texte(cb, font, 9, G1_TOTAL_POINTS_X, G1_TOTAL_Y, String.valueOf(r.getTotalPremierGroupe()));
        texte(cb, font, 9, G2_TOTAL_POINTS_X, G2_TOTAL_Y, String.valueOf(r.getTotalPremierGroupe()));
    }

    private void ecrireDeuxiemeGroupe(PdfContentByte cb, BaseFont font, RelevNoteA1 r) {
        texte(cb, font, 9, REPORT_1ER_TOTAL_X, REPORT_1ER_TOTAL_Y, String.valueOf(r.getReportPremierTotal()));

        Map<String, NoteEpreuve> parCode = r.getNotesDeuxiemeGroupe().stream()
                .collect(Collectors.toMap(NoteEpreuve::getMatiereCode, n -> n));

        ligneNote(cb, font, parCode.get(MatieresA1.GREC_LATIN_2.getCode()),  G2_NOTE_X, G2_POINTS_X, G2_GRECLATIN_Y);
        ligneNote(cb, font, parCode.get(MatieresA1.MATHEMATIQUES.getCode()), G2_NOTE_X, G2_POINTS_X, G2_MATHS_Y);
    }

    private void ligneNote(PdfContentByte cb, BaseFont font, NoteEpreuve note, float xNote, float xPoints, float y) {
        if (note == null) return;
        texte(cb, font, 9, xNote, y, note.getNote() == null ? null : String.valueOf(note.getNote()));
        texte(cb, font, 9, xPoints, y, note.getPointsObtenus() == null ? null : String.valueOf(note.getPointsObtenus()));
    }

    private void ecrireEpreuvesControle(PdfContentByte cb, BaseFont font, RelevNoteA1 r) {
        float y = CTRL_FIRST_ROW_Y;
        for (EpreuveOraleControle ctrl : r.getEpreuvesOralesControle()) {
            texte(cb, font, 8, CTRL_MATIERE_X, y, ctrl.getMatiereChoisie());
            texte(cb, font, 8, CTRL_RAPPEL_X, y, str(ctrl.getRappelPointsObtenus1erGroupe()));
            texte(cb, font, 8, CTRL_NOUVELLE_X, y, str(ctrl.getNouvelleNoteSur20()));
            texte(cb, font, 8, CTRL_POINTS_X, y, str(ctrl.getPointsObtenusEpreuveControle()));
            texte(cb, font, 8, CTRL_DIFF_X, y, str(ctrl.getDifferenceEnPlus()));
            y -= CTRL_ROW_HEIGHT;
        }
    }

    private void ecrireEpreuvesFacultatives(PdfContentByte cb, BaseFont font, RelevNoteA1 r) {
        for (EpreuveFacultative f : r.getEpreuvesFacultatives()) {
            if (f.getType() == TypeFacultative.LANGUE) {
                texte(cb, font, 9, FAC_LANGUE_X, FAC_LANGUE_Y, str(f.getPointsAuDessusMoyenne()));
            } else {
                texte(cb, font, 9, FAC_ARTS_X, FAC_ARTS_Y, str(f.getPointsAuDessusMoyenne()));
            }
        }
    }

    private void ecrireTotaux(PdfContentByte cb, BaseFont font, RelevNoteA1 r) {
        texte(cb, font, 9, TOTAL_PROVISOIRE_X, TOTAL_PROVISOIRE_Y, str(r.getTotalProvisoire()));

        EducationPhysique ep = r.getEducationPhysique();
        if (ep != null) {
            texte(cb, font, 9, EP_NOTE_X, EP_NOTE_Y, str(ep.getNote()));
            texte(cb, font, 9, EP_POS_X, EP_POS_Y, str(ep.getPointsPositifs()));
            texte(cb, font, 9, EP_NEG_X, EP_NEG_Y, str(ep.getPointsNegatifs()));
        }

        texte(cb, font, 10, TOTAL_DEFINITIF_X, TOTAL_DEFINITIF_Y, str(r.getTotalDefinitif()));
    }

    private void ecrireDecisions(PdfContentByte cb, BaseFont font, RelevNoteA1 r) {
        // ---- 1er groupe ----
        DecisionJury d1 = r.getDecisionPremierGroupe();
        if (d1 == DecisionJury.ADMIS) {
            coche(cb, DEC1_COCHE_ADMIS_X, DEC1_COCHE_ADMIS_Y);
            texte(cb, font, 9, DEC1_MENTION_X, DEC1_MENTION_Y, libelleMention(r.getMentionPremierGroupe()));
        } else if (d1 == DecisionJury.AUTORISE_SECOND_GROUPE) {
            coche(cb, DEC1_COCHE_AUTORISE_X, DEC1_COCHE_AUTORISE_Y);
        } else if (d1 == DecisionJury.AJOURNE) {
            coche(cb, DEC1_COCHE_AJOURNE_X, DEC1_COCHE_AJOURNE_Y);
        }
        texte(cb, font, 9, DEC1_LIEU_X, DEC1_LIEU_Y, r.getLieuDelivrance());
        texte(cb, font, 9, DEC1_DATE_X, DEC1_DATE_Y, r.getDateDelivrance() == null ? null : r.getDateDelivrance().format(DATE_FR));
        texte(cb, font, 9, DEC1_PRESIDENT_X, DEC1_PRESIDENT_Y, r.getPresidentJury());

        // ---- 2eme groupe ----
        DecisionJury d2 = r.getDecisionDeuxiemeGroupe();
        if (d2 == DecisionJury.ADMIS) {
            coche(cb, DEC2_COCHE_ADMIS_X, DEC2_COCHE_ADMIS_Y);
            texte(cb, font, 9, DEC2_MENTION_X, DEC2_MENTION_Y, libelleMention(r.getMentionDeuxiemeGroupe()));
        } else if (d2 == DecisionJury.AJOURNE) {
            coche(cb, DEC2_COCHE_AJOURNE_X, DEC2_COCHE_AJOURNE_Y);
        }
        texte(cb, font, 9, DEC2_LIEU_X, DEC2_LIEU_Y, r.getLieuDelivrance());
        texte(cb, font, 9, DEC2_DATE_X, DEC2_DATE_Y, r.getDateDelivrance() == null ? null : r.getDateDelivrance().format(DATE_FR));
        texte(cb, font, 9, DEC2_PRESIDENT_X, DEC2_PRESIDENT_Y, r.getPresidentJury());
    }

    private String libelleMention(Enums.Mention m) {
        if (m == null) return null;
        return switch (m) {
            case PASSABLE -> "PASSABLE";
            case ASSEZ_BIEN -> "ASSEZ BIEN";
            case BIEN -> "BIEN";
            case TRES_BIEN -> "TRES BIEN";
            case AUCUNE -> null;
        };
    }

    private String str(Integer v) {
        return v == null ? null : String.valueOf(v);
    }
}
