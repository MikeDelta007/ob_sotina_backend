package com.officedubac.project.pdftools;

import com.officedubac.project.modules.B.model.*;
import com.officedubac.project.modules.B.model.Enums.*;
import com.officedubac.project.modules.B.pdf.RelevNoteBPdfService;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;

public class BPdfHarness {

    @Test
    void genererEchantillon() throws Exception {
        RelevNoteB r = new RelevNoteB();
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

        List<Matiere> g1 = MatieresB.PREMIER_GROUPE;
        java.util.List<NoteEpreuve> notes = new java.util.ArrayList<>();
        int total1 = 0;
        for (Matiere m : g1) {
            int note = 12;
            int pts = note * m.getCoefficient();
            notes.add(note(m.getCode(), note, pts));
            total1 += pts;
        }
        r.setNotesPremierGroupe(notes);
        r.setTotalPremierGroupe(total1);
        r.setReportPremierTotal(total1);
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
        r.setEpreuvesFacultatives(List.of(fLangue));

        EducationPhysique ep = new EducationPhysique();
        ep.setNote(12);
        ep.setPointsPositifs(2);
        ep.setPointsNegatifs(0);
        r.setEducationPhysique(ep);

        r.setTotalDefinitif(total1 + 6 + 2);

        r.setDecisionPremierGroupe(DecisionJury.ADMIS);
        r.setMentionPremierGroupe(Mention.PASSABLE);
        r.setLieuDeliberation("DAKAR");
        r.setDateDeliberationPremierGroupe(LocalDate.of(2026, 8, 11));
        r.setDecisionDeuxiemeGroupe(null);

        RelevNoteBPdfService service = new RelevNoteBPdfService();
        byte[] pdf = service.genererPdf(r);
        try (FileOutputStream fos = new FileOutputStream("target/b-echantillon-g1.pdf")) {
            fos.write(pdf);
        }

        r.setDecisionPremierGroupe(DecisionJury.AUTORISE_SECOND_GROUPE);
        r.setMentionPremierGroupe(null);
        r.setDateDeliberationPremierGroupe(null);
        r.setDecisionDeuxiemeGroupe(DecisionJury.ADMIS);
        r.setMentionDeuxiemeGroupe(Mention.PASSABLE);
        r.setDateDeliberationDeuxiemeGroupe(LocalDate.of(2026, 8, 11));

        byte[] pdf2 = service.genererPdf(r);
        try (FileOutputStream fos = new FileOutputStream("target/b-echantillon-g2.pdf")) {
            fos.write(pdf2);
        }
    }

    private static NoteEpreuve note(String code, int note, int points) {
        NoteEpreuve n = new NoteEpreuve(code, note);
        n.setPointsObtenus(points);
        return n;
    }
}
