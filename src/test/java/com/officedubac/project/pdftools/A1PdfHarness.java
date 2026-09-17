package com.officedubac.project.pdftools;

import com.officedubac.project.modules.A1.model.*;
import com.officedubac.project.modules.A1.model.Enums.*;
import com.officedubac.project.modules.A1.pdf.RelevNoteA1PdfService;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;

public class A1PdfHarness {

    @Test
    void genererEchantillon() throws Exception {
        RelevNoteA1 r = new RelevNoteA1();
        r.setSession(TypeSession.NORMALE);
        r.setJuryNumero("2");
        r.setAnnee(2026);

        Candidat c = new Candidat();
        c.setNomPrenom("MANSOUR DIOUF");
        c.setDateNaissance(LocalDate.of(2000, 7, 27));
        c.setLieuNaissance("DAKAR");
        c.setEtablissement("LBY");
        c.setIndicatif("12");
        c.setOptions("ES-OP-AN");
        c.setNumeroTable("255");
        c.setNationalite("SEN");
        c.setNombreDeFois("1");
        r.setCandidat(c);

        r.setNotesPremierGroupe(List.of(
                note(MatieresA1.FRANCAIS_ECRIT.getCode(), 11, 33),
                note(MatieresA1.FRANCAIS_ORAL.getCode(), 11, 11),
                note(MatieresA1.PHILOSOPHIE.getCode(), 11, 44),
                note(MatieresA1.LATIN_GREC_1.getCode(), 11, 33),
                note(MatieresA1.HIST_GEO.getCode(), 11, 33),
                note(MatieresA1.LANGUE_VIVANTE.getCode(), 11, 22)
        ));
        r.setTotalPremierGroupe(176);
        r.setReportPremierTotal(176);

        r.setNotesDeuxiemeGroupe(List.of(
                note(MatieresA1.GREC_LATIN_2.getCode(), 12, 24),
                note(MatieresA1.MATHEMATIQUES.getCode(), 13, 26)
        ));

        EpreuveOraleControle ctrl = new EpreuveOraleControle();
        ctrl.setMatiereChoisie("Anglais");
        ctrl.setCoefficient(2);
        ctrl.setRappelPointsObtenus1erGroupe(22);
        ctrl.setNouvelleNoteSur20(14);
        ctrl.setPointsObtenusEpreuveControle(28);
        ctrl.setDifferenceEnPlus(6);
        r.setEpreuvesOralesControle(List.of(ctrl));

        EpreuveFacultative fLangue = new EpreuveFacultative();
        fLangue.setType(TypeFacultative.LANGUE);
        fLangue.setNote(15);
        fLangue.setPointsAuDessusMoyenne(5);

        EpreuveFacultative fArts = new EpreuveFacultative();
        fArts.setType(TypeFacultative.DESSIN);
        fArts.setNote(14);
        fArts.setPointsAuDessusMoyenne(4);

        r.setEpreuvesFacultatives(List.of(fLangue, fArts));

        EducationPhysique ep = new EducationPhysique();
        ep.setNote(12);
        ep.setPointsPositifs(2);
        ep.setPointsNegatifs(0);
        r.setEducationPhysique(ep);

        r.setTotalProvisoire(226);
        r.setTotalDefinitif(228);

        r.setDecisionPremierGroupe(DecisionJury.AUTORISE_SECOND_GROUPE);
        r.setMentionPremierGroupe(null);
        r.setLieuDeliberation("DAKAR");
        r.setDateDeliberationPremierGroupe(null);

        r.setDecisionDeuxiemeGroupe(DecisionJury.ADMIS);
        r.setMentionDeuxiemeGroupe(Mention.PASSABLE);
        r.setDateDeliberationDeuxiemeGroupe(LocalDate.of(2026, 8, 11));

        RelevNoteA1PdfService service = new RelevNoteA1PdfService();
        byte[] pdf = service.genererPdf(r);
        try (FileOutputStream fos = new FileOutputStream(System.getProperty("pdf.out",
                "target/a1-echantillon.pdf"))) {
            fos.write(pdf);
        }
    }

    private static NoteEpreuve note(String code, int note, int points) {
        NoteEpreuve n = new NoteEpreuve(code, note);
        n.setPointsObtenus(points);
        return n;
    }
}
