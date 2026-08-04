package com.officedubac.project.modules.A4.pdf;

import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.officedubac.project.modules.A4.model.*;
import com.officedubac.project.modules.A4.model.Enums.DecisionJury;
import com.officedubac.project.modules.A4.model.Enums.Mention;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

import static com.officedubac.project.modules.A4.pdf.ReleveA4Coordinates.*;

@Service
public class ReleveA4PdfService {

    private static final String TEMPLATE_PATH = "templates/releve-A4-template.pdf";
    private static final DateTimeFormatter DATE_FR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] genererPdf(ReleveA4 releve) {
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
            ecrireBonusGauche(cb, font, releve);
            ecrireEducationPhysiqueEtFacultatives(cb, font, releve);
            ecrireTotaux(cb, font, releve);
            ecrireDecisions(cb, font, releve);

            stamper.close();
            reader.close();
            return out.toByteArray();
        } catch (IOException | com.lowagie.text.DocumentException e) {
            throw new IllegalStateException("Erreur lors de la génération du relevé A4 en PDF: " + e.getMessage(), e);
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

    private void ecrireEnTete(PdfContentByte cb, BaseFont font, ReleveA4 r) {
        texte(cb, font, 9, JURY_NUMERO_X, JURY_NUMERO_Y, r.getJuryNumero());
        texte(cb, font, 9, CENTRE_X, CENTRE_Y, r.getCentre());
        texte(cb, font, 9, SESSION_X, SESSION_Y, r.getSession());
    }

    private void ecrireCandidat(PdfContentByte cb, BaseFont font, ReleveA4 r) {
        Candidat c = r.getCandidat();
        if (c == null) return;
        texte(cb, font, 9, LIGNE1_X, LIGNE1_Y, c.getLigne1IdentiteEtablissement());
        texte(cb, font, 9, LIGNE2_X, LIGNE2_Y, c.getLigne2Naissance());
        texte(cb, font, 9, LIGNE3_X, LIGNE3_Y, c.getLigne3SerieOptions());
        texte(cb, font, 9, LIGNE4_X, LIGNE4_Y, c.getLigne4Eaf());
    }

    private void ecrirePremierGroupe(PdfContentByte cb, BaseFont font, ReleveA4 r) {
        Map<String, NoteEpreuve> parCode = r.getNotesPremierGroupe().stream()
                .collect(Collectors.toMap(NoteEpreuve::getMatiereCode, n -> n));

        ligneNote(cb, font, parCode.get(MatieresA4.FRANCAIS_ECRIT.getCode()), G1_FR_ECRIT_Y);
        ligneNote(cb, font, parCode.get(MatieresA4.FRANCAIS_ORAL.getCode()),  G1_FR_ORAL_Y);
        ligneNote(cb, font, parCode.get(MatieresA4.PHILOSOPHIE.getCode()),    G1_PHILO_Y);
        ligneNote(cb, font, parCode.get(MatieresA4.LV1_ECRIT.getCode()),      G1_LV1_Y);
        ligneNote(cb, font, parCode.get(MatieresA4.HIST_GEO.getCode()),       G1_HISTGEO_Y);
        ligneNote(cb, font, parCode.get(MatieresA4.MATH_ORAL.getCode()),      G1_MATH_Y);

        texte(cb, font, 9, G1_TOTAL_POINTS_X, G1_TOTAL_Y, String.valueOf(r.getTotalPremierGroupe()));
        texte(cb, font, 9, G2_TOTAL_POINTS_X, G2_TOTAL_Y, String.valueOf(r.getTotalPremierGroupe()));
    }

    private void ligneNote(PdfContentByte cb, BaseFont font, NoteEpreuve note, float y) {
        if (note == null) return;
        texte(cb, font, 9, G1_NOTE_X, y, note.getNote() == null ? null : String.valueOf(note.getNote()));
        texte(cb, font, 9, G1_POINTS_X, y, note.getPointsObtenus() == null ? null : String.valueOf(note.getPointsObtenus()));
    }

    private void ecrireDeuxiemeGroupe(PdfContentByte cb, BaseFont font, ReleveA4 r) {
        texte(cb, font, 9, REPORT_1ER_TOTAL_X, REPORT_1ER_TOTAL_Y, String.valueOf(r.getReportPremierTotal()));

        Map<String, NoteEpreuve> parCode = r.getNotesDeuxiemeGroupe().stream()
                .collect(Collectors.toMap(NoteEpreuve::getMatiereCode, n -> n));
        NoteEpreuve lv2 = parCode.get(MatieresA4.LV2.getCode());
        if (lv2 != null) {
            texte(cb, font, 9, LV2_NOTE_X, LV2_NOTE_Y, lv2.getNote() == null ? null : String.valueOf(lv2.getNote()));
            texte(cb, font, 9, LV2_POINTS_X, LV2_POINTS_Y, lv2.getPointsObtenus() == null ? null : String.valueOf(lv2.getPointsObtenus()));
        }
    }

    private void ecrireEpreuvesControle(PdfContentByte cb, BaseFont font, ReleveA4 r) {
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

    private void ecrireBonusGauche(PdfContentByte cb, BaseFont font, ReleveA4 r) {
        EducationPhysique ep = r.getEducationPhysique();
        texte(cb, font, 9, BONUS_EDUCPHYS_LEFT_X, BONUS_EDUCPHYS_LEFT_Y, ep == null ? null : str(ep.getPointsPositifs()));

        for (EpreuveFacultative f : r.getEpreuvesFacultatives()) {
            if (f.getType() == Enums.TypeFacultative.LANGUE) {
                texte(cb, font, 9, BONUS_LANGUE_LEFT_X, BONUS_LANGUE_LEFT_Y, str(f.getPointsAuDessusMoyenne()));
            } else {
                texte(cb, font, 9, BONUS_ARTS_LEFT_X, BONUS_ARTS_LEFT_Y, str(f.getPointsAuDessusMoyenne()));
            }
        }
    }

    private void ecrireEducationPhysiqueEtFacultatives(PdfContentByte cb, BaseFont font, ReleveA4 r) {
        EducationPhysique ep = r.getEducationPhysique();
        if (ep != null) {
            texte(cb, font, 9, EP_NOTE_X, EP_NOTE_Y, str(ep.getNote()));
            texte(cb, font, 9, EP_POS_X, EP_POS_Y, str(ep.getPointsPositifs()));
            texte(cb, font, 9, EP_NEG_X, EP_NEG_Y, str(ep.getPointsNegatifs()));
        }
        for (EpreuveFacultative f : r.getEpreuvesFacultatives()) {
            if (f.getType() == Enums.TypeFacultative.LANGUE) {
                texte(cb, font, 9, FAC_LANGUE_X, FAC_LANGUE_Y, str(f.getPointsAuDessusMoyenne()));
            } else {
                texte(cb, font, 9, FAC_ARTS_X, FAC_ARTS_Y, str(f.getPointsAuDessusMoyenne()));
            }
        }
    }

    private void ecrireTotaux(PdfContentByte cb, BaseFont font, ReleveA4 r) {
        texte(cb, font, 9, TOTAL_PROVISOIRE_X, TOTAL_PROVISOIRE_Y, str(r.getTotalProvisoire()));
        texte(cb, font, 10, TOTAL_DEFINITIF_X, TOTAL_DEFINITIF_Y, str(r.getTotalDefinitif()));
    }

    private void ecrireDecisions(PdfContentByte cb, BaseFont font, ReleveA4 r) {
        DecisionJury d1 = r.getDecisionPremierGroupe();
        if (d1 == DecisionJury.ADMIS) {
            coche(cb, DEC1_COCHE_ADMIS_X, DEC1_COCHE_ADMIS_Y);
            texte(cb, font, 9, DEC1_MENTION_X, DEC1_MENTION_Y, libelleMention(r.getMentionPremierGroupe()));
        } else if (d1 == DecisionJury.AUTORISE_SECOND_GROUPE) {
            coche(cb, DEC1_COCHE_AUTORISE_X, DEC1_COCHE_AUTORISE_Y);
        } else if (d1 == DecisionJury.AJOURNE) {
            coche(cb, DEC1_COCHE_AJOURNE_X, DEC1_COCHE_AJOURNE_Y);
        }
        texte(cb, font, 9, DEC1_DATE_X, DEC1_DATE_Y, r.getDateDelivrance() == null ? null : r.getDateDelivrance().format(DATE_FR));
        texte(cb, font, 9, DEC1_PRESIDENT_X, DEC1_PRESIDENT_Y, r.getPresidentJury());

        DecisionJury d2 = r.getDecisionDeuxiemeGroupe();
        if (d2 == DecisionJury.ADMIS) {
            coche(cb, DEC2_COCHE_ADMIS_X, DEC2_COCHE_ADMIS_Y);
            texte(cb, font, 9, DEC2_MENTION_X, DEC2_MENTION_Y, libelleMention(r.getMentionDeuxiemeGroupe()));
        } else if (d2 == DecisionJury.AJOURNE) {
            coche(cb, DEC2_COCHE_AJOURNE_X, DEC2_COCHE_AJOURNE_Y);
        }
        texte(cb, font, 9, DEC2_DATE_X, DEC2_DATE_Y, r.getDateDelivrance() == null ? null : r.getDateDelivrance().format(DATE_FR));
        texte(cb, font, 9, DEC2_PRESIDENT_X, DEC2_PRESIDENT_Y, r.getPresidentJury());
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

    private String str(Integer v) {
        return v == null ? null : String.valueOf(v);
    }
}
