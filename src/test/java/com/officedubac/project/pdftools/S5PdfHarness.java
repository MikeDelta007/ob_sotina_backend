package com.officedubac.project.pdftools;

import com.officedubac.project.modules.S5.model.*;
import com.officedubac.project.modules.S5.model.Enums.*;
import com.officedubac.project.modules.S5.pdf.RelevNoteS5PdfService;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class S5PdfHarness {

    private RelevNoteS5 base() {
        RelevNoteS5 r = new RelevNoteS5();
        r.setSession(TypeSession.NORMALE);
        r.setJuryNumero("2");
        r.setAnnee(2026);

        Candidat c = new Candidat();
        c.setNomPrenom("MANSOUR DIOUF");
        c.setDateNaissance(LocalDate.of(2000, 7, 27));
        c.setLieuNaissance("DAKAR");
        c.setEtablissement("LBY");
        c.setIndicatif("12");
        c.setOptions("SP");
        c.setNumeroTable("255");
        c.setNationalite("SEN");
        c.setNombreDeFois("1");
        r.setCandidat(c);

        List<NoteEpreuve> g1 = new ArrayList<>();
        int total = 0;
        for (Matiere m : MatieresS5.PREMIER_GROUPE) {
            int note = 12;
            int points = note * m.getCoefficient();
            g1.add(note(m.getCode(), note, points));
            total += points;
        }
        r.setNotesPremierGroupe(g1);
        r.setTotalPremierGroupe(total);
        r.setReportPremierTotal(total);
        r.setNotesDeuxiemeGroupe(List.of());

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

        r.setTotalProvisoire(total + 6);
        r.setTotalDefinitif(total + 6 + 2);

        r.setLieuDeliberation("DAKAR");
        return r;
    }

    @Test
    void scenarioPremierGroupeSeul() throws Exception {
        RelevNoteS5 r = base();
        r.setDecisionPremierGroupe(DecisionJury.ADMIS);
        r.setMentionPremierGroupe(Mention.PASSABLE);
        r.setDateDeliberationPremierGroupe(LocalDate.of(2026, 8, 11));
        r.setDecisionDeuxiemeGroupe(null);
        write(r, "target/s5-echantillon-g1.pdf");
    }

    @Test
    void scenarioDeuxiemeGroupe() throws Exception {
        RelevNoteS5 r = base();
        r.setDecisionPremierGroupe(DecisionJury.AUTORISE_SECOND_GROUPE);
        r.setDateDeliberationPremierGroupe(null);
        r.setDecisionDeuxiemeGroupe(DecisionJury.ADMIS);
        r.setMentionDeuxiemeGroupe(Mention.PASSABLE);
        r.setDateDeliberationDeuxiemeGroupe(LocalDate.of(2026, 8, 11));
        write(r, "target/s5-echantillon-g2.pdf");
    }

    private void write(RelevNoteS5 r, String path) throws Exception {
        RelevNoteS5PdfService service = new RelevNoteS5PdfService();
        byte[] pdf = service.genererPdf(r);
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(pdf);
        }
    }

    private static NoteEpreuve note(String code, int note, int points) {
        NoteEpreuve n = new NoteEpreuve(code, note);
        n.setPointsObtenus(points);
        return n;
    }
}
