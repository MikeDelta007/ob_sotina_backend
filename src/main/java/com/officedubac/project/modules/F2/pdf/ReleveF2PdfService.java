package com.officedubac.project.modules.F2.pdf;

import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.officedubac.project.modules.F2.model.*;
import com.officedubac.project.modules.F2.model.Enums.DecisionJury;
import com.officedubac.project.modules.F2.model.Enums.Mention;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;

import static com.officedubac.project.modules.F2.pdf.ReleveF2Coordinates.*;

/**
 * Génère le PDF du relevé F2 en superposant les données saisies sur une
 * copie du "CERTIFICAT PROCES-VERBAL D'EXAMEN" scanné (fond de page), aux
 * coordonnées mesurées dans {@link ReleveF2Coordinates}.
 *
 * Particularité par rapport aux autres modules : ce formulaire n'a pas de
 * cases ADMIS/AUTORISE/AJOURNE imprimées à cocher — la décision et la
 * mention sont écrites en toutes lettres sur des lignes libres
 * ("l'avons déclaré .......... avec mention ..........").
 */
@Service
public class ReleveF2PdfService {

    private static final String TEMPLATE_PATH = "templates/releve-F2-template.pdf";
    private static final DateTimeFormatter DATE_FR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] genererPdf(ReleveF2 releve) {
        try (InputStream templateStream = new ClassPathResource(TEMPLATE_PATH).getInputStream();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PdfReader reader = new PdfReader(templateStream);
            PdfStamper stamper = new PdfStamper(reader, out);

            BaseFont font = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            PdfContentByte cb = stamper.getOverContent(1);

            ecrireEnTete(cb, font, releve);
            ecrireCandidat(cb, font, releve);
            ecrireAnticipees(cb, font, releve);
            ecrirePremierGroupe(cb, font, releve);
            ecrireDominantes(cb, font, releve);
            ecrireEpreuvesDeControle(cb, font, releve);
            ecrireEducationPhysique(cb, font, releve);
            ecrireEpreuvesFacultatives(cb, font, releve);
            ecrireTotalDefinitif(cb, font, releve);
            ecrireDecisions(cb, font, releve);

            stamper.close();
            reader.close();
            return out.toByteArray();
        } catch (IOException | com.lowagie.text.DocumentException e) {
            throw new IllegalStateException("Erreur lors de la génération du relevé F2 en PDF: " + e.getMessage(), e);
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

    private void ecrireEnTete(PdfContentByte cb, BaseFont font, ReleveF2 r) {
        texte(cb, font, 9, JURY_NUMERO_X, JURY_NUMERO_Y, r.getJuryNumero());
    }

    private void ecrireCandidat(PdfContentByte cb, BaseFont font, ReleveF2 r) {
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

    private void ecrireAnticipees(PdfContentByte cb, BaseFont font, ReleveF2 r) {
        Candidat c = r.getCandidat();
        if (c == null) return;
        texte(cb, font, 8, ANTICIPEES_SUBIES_X, ANTICIPEES_SUBIES_Y, c.getAnticipeesSubies());
        texte(cb, font, 8, ANTICIPEES_CENTRE_X, ANTICIPEES_CENTRE_Y, c.getAnticipeesCentre());
        texte(cb, font, 8, ANTICIPEES_ANNEE_X, ANTICIPEES_ANNEE_Y, c.getAnticipeesAnnee());
        texte(cb, font, 8, ANTICIPEES_LIEU_X, ANTICIPEES_LIEU_Y, c.getAnticipeesLieu());
        texte(cb, font, 8, ANTICIPEES_E_X, ANTICIPEES_E_Y, str(c.getAnticipeesNoteEcrit()));
        texte(cb, font, 8, ANTICIPEES_O_X, ANTICIPEES_O_Y, str(c.getAnticipeesNoteOral()));
    }

    private void ecrirePremierGroupe(PdfContentByte cb, BaseFont font, ReleveF2 r) {
        var parCode = r.getNotesPremierGroupe().stream()
                .collect(java.util.stream.Collectors.toMap(NoteEpreuve::getMatiereCode, n -> n));

        ligneNote(cb, font, parCode.get(MatieresF2.FRANCAIS_ECRIT.getCode()), G1_FR_ECRIT_Y);
        ligneNote(cb, font, parCode.get(MatieresF2.FRANCAIS_ORAL.getCode()),  G1_FR_ORAL_Y);
        ligneNote(cb, font, parCode.get(MatieresF2.MATHEMATIQUES.getCode()), G1_MATH_Y);
        ligneNote(cb, font, parCode.get(MatieresF2.ELECTROTECHNIQUE.getCode()), G1_ELECTROTECHNI_Y);
        ligneNote(cb, font, parCode.get(MatieresF2.CONSTR_ELEC_MECA.getCode()), G1_CONSTRELECMECA_Y);
        ligneNote(cb, font, parCode.get(MatieresF2.SHEMA_AUTOMATISMES.getCode()), G1_SHEMAAUTOM_Y);
        ligneNote(cb, font, parCode.get(MatieresF2.ETUDE_EQUIPEMENT.getCode()), G1_ETUDEEQUIP_Y);
        ligneNote(cb, font, parCode.get(MatieresF2.ANGLAIS.getCode()),       G1_ANGLAIS_Y);
        ligneNote(cb, font, parCode.get(MatieresF2.CABLAGE_TECHNO.getCode()), G1_CABLAGE_Y);
        ligneNote(cb, font, parCode.get(MatieresF2.ESSAIS_MESURES.getCode()), G1_ESSAIS_Y);

        texte(cb, font, 9, G1_TOTAL_POINTS_X, G1_TOTAL_Y, String.valueOf(r.getTotalPremierGroupe()));
        texte(cb, font, 9, G2_TOTAL_POINTS_X, G2_TOTAL_Y, String.valueOf(r.getTotalPremierGroupe()));
        texte(cb, font, 9, REPORT_1ER_TOTAL_X, REPORT_1ER_TOTAL_Y, String.valueOf(r.getReportPremierTotal()));
    }

    private void ligneNote(PdfContentByte cb, BaseFont font, NoteEpreuve note, float y) {
        if (note == null) return;
        texte(cb, font, 9, G1_NOTE_X, y, note.getNote() == null ? null : String.valueOf(note.getNote()));
        texte(cb, font, 9, G1_POINTS_X, y, note.getPointsObtenus() == null ? null : String.valueOf(note.getPointsObtenus()));
    }

    private void ecrireDominantes(PdfContentByte cb, BaseFont font, ReleveF2 r) {
        Dominantes d = r.getDominantes();
        if (d == null) return;
        texte(cb, font, 8, DOM_ECRIT1_X, DOM_ECRIT1_Y, d.getDominanteEcrit1());
        texte(cb, font, 8, DOM_ECRIT2_X, DOM_ECRIT2_Y, d.getDominanteEcrit2());
        texte(cb, font, 8, DOM_ORAL_X, DOM_ORAL_Y, d.getDominanteOral());
    }

    private void ecrireEpreuvesDeControle(PdfContentByte cb, BaseFont font, ReleveF2 r) {
        float y = CTRL_FIRST_ROW_Y;
        for (EpreuveDeControle ctrl : r.getEpreuvesDeControle()) {
            String libelle = ctrl.getMatiereCode() == null ? null : MatieresF2.findByCode(ctrl.getMatiereCode()).getLibelle();
            texte(cb, font, 8, CTRL_MATIERE_X, y, libelle);
            texte(cb, font, 8, CTRL_RAPPEL_X, y, str(ctrl.getRappelPointsObtenus1erGroupe()));
            texte(cb, font, 8, CTRL_NOUVELLE_X, y, str(ctrl.getNouvelleNoteSur20()));
            texte(cb, font, 8, CTRL_POINTS_X, y, str(ctrl.getPointsAuControle()));
            texte(cb, font, 8, CTRL_DIFF_X, y, str(ctrl.getDifferenceEnPlus()));
            y -= CTRL_ROW_HEIGHT;
        }
    }

    private void ecrireEducationPhysique(PdfContentByte cb, BaseFont font, ReleveF2 r) {
        EducationPhysique ep = r.getEducationPhysique();
        if (ep == null) return;
        if (Boolean.TRUE.equals(ep.getInapteOuControleAssidu())) {
            texte(cb, font, 8, EP_NOTE_X, EP_NOTE_Y, "Inapte/C.ASS.");
        } else {
            texte(cb, font, 9, EP_NOTE_X, EP_NOTE_Y, str(ep.getNote()));
        }
        texte(cb, font, 9, EP_POS_X, EP_POS_Y, str(ep.getPointsPositifs()));
        texte(cb, font, 9, EP_NEG_X, EP_NEG_Y, str(ep.getPointsNegatifs()));
    }

    private void ecrireEpreuvesFacultatives(PdfContentByte cb, BaseFont font, ReleveF2 r) {
        for (EpreuveFacultative f : r.getEpreuvesFacultatives()) {
            boolean langue = f.getType() == Enums.TypeFacultative.LANGUE;
            if (langue) {
                texte(cb, font, 9, FAC_G_LANGUE_X, FAC_G_LANGUE_Y, str(f.getPointsAuDessusMoyenne()));
                texte(cb, font, 9, FAC_D_LANGUE_X, FAC_D_LANGUE_Y, str(f.getPointsAuDessusMoyenne()));
            } else {
                texte(cb, font, 9, FAC_G_ARTS_X, FAC_G_ARTS_Y, str(f.getPointsAuDessusMoyenne()));
                texte(cb, font, 9, FAC_D_ARTS_X, FAC_D_ARTS_Y, str(f.getPointsAuDessusMoyenne()));
            }
        }
    }

    private void ecrireTotalDefinitif(PdfContentByte cb, BaseFont font, ReleveF2 r) {
        texte(cb, font, 10, TOTAL_DEFINITIF_X, TOTAL_DEFINITIF_Y, str(r.getTotalDefinitif()));
    }

    private void ecrireDecisions(PdfContentByte cb, BaseFont font, ReleveF2 r) {
        texte(cb, font, 9, DEC1_DECLARATION_X, DEC1_DECLARATION_Y, libelleDecision(r.getDecisionPremierGroupe()));
        texte(cb, font, 9, DEC1_MENTION_X, DEC1_MENTION_Y, libelleMention(r.getMentionPremierGroupe()));
        texte(cb, font, 9, DEC1_LIEU_X, DEC1_LIEU_Y, r.getLieuDelivrance());
        texte(cb, font, 9, DEC1_DATE_X, DEC1_DATE_Y, r.getDateDelivrance() == null ? null : r.getDateDelivrance().format(DATE_FR));
        texte(cb, font, 9, DEC1_PRESIDENT_X, DEC1_PRESIDENT_Y, r.getPresidentJury());

        texte(cb, font, 9, DEC2_DECLARATION_X, DEC2_DECLARATION_Y, libelleDecision(r.getDecisionDeuxiemeGroupe()));
        texte(cb, font, 9, DEC2_MENTION_X, DEC2_MENTION_Y, libelleMention(r.getMentionDeuxiemeGroupe()));
        texte(cb, font, 9, DEC2_LIEU_X, DEC2_LIEU_Y, r.getLieuDelivrance());
        texte(cb, font, 9, DEC2_DATE_X, DEC2_DATE_Y, r.getDateDelivrance() == null ? null : r.getDateDelivrance().format(DATE_FR));
        texte(cb, font, 9, DEC2_PRESIDENT_X, DEC2_PRESIDENT_Y, r.getPresidentJury());
    }

    private String libelleDecision(DecisionJury d) {
        if (d == null) return null;
        return switch (d) {
            case ADMIS -> "ADMIS";
            case AUTORISE_SECOND_GROUPE -> "AUTORISE A SUBIR LE 2EME GROUPE D'EPREUVES";
            case AJOURNE -> "AJOURNE";
        };
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
