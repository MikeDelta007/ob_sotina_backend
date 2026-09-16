package com.officedubac.project.pdftools;

import com.officedubac.project.modules.F7.model.*;
import com.officedubac.project.modules.F7.model.Enums.*;
import com.officedubac.project.modules.F7.pdf.RelevNoteF7PdfService;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;

public class F7PdfHarness {

    private RelevNoteF7 base() {
        RelevNoteF7 r = new RelevNoteF7();
        r.setSession(TypeSession.NORMALE);
        r.setJuryNumero("2");
        r.setAnnee(2026);

        Candidat c = new Candidat();
        c.setNomPrenom("MANSOUR DIOUF");
        c.setDateNaissance(LocalDate.of(2000, 7, 27));
        c.setLieuNaissance("DAKAR");
        c.setEtablissement("LBY");
        c.setIndicatif("12");
        c.setOptions("F7");
        c.setNumeroTable("255");
        c.setNationalite("SEN");
        c.setNombreDeFois("1");
        r.setCandidat(c);

        List<Matiere> mats = MatieresF7.PREMIER_GROUPE;
        java.util.ArrayList<NoteEpreuve> notes = new java.util.ArrayList<>();
        int total = 0;
        for (Matiere m : mats) {
            int note = 11;
            int pts = note * m.getCoefficient();
            notes.add(note(m.getCode(), note, pts));
            total += pts;
        }
        r.setNotesPremierGroupe(notes);
        r.setTotalPremierGroupe(total);
        r.setReportPremierTotal(total);
        r.setNotesDeuxiemeGroupe(List.of());

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

        r.setLieuDeliberation("DAKAR");
        return r;
    }

    @Test
    void scenario1erGroupeSeul() throws Exception {
        RelevNoteF7 r = base();
        r.setDecisionPremierGroupe(DecisionJury.ADMIS);
        r.setMentionPremierGroupe(Mention.PASSABLE);
        r.setDateDeliberationPremierGroupe(LocalDate.of(2026, 8, 11));
        r.setTotalDefinitif(r.getTotalPremierGroupe() + 2);

        genererEtEcrire(r, "target/f7-1ergroupe.pdf");
    }

    @Test
    void scenario2emeGroupe() throws Exception {
        RelevNoteF7 r = base();
        r.setDecisionPremierGroupe(DecisionJury.AUTORISE_SECOND_GROUPE);

        EpreuveOraleControle ctrl = new EpreuveOraleControle();
        ctrl.setMatiereChoisie("Biologie");
        ctrl.setCoefficient(4);
        ctrl.setRappelPointsObtenus1erGroupe(44);
        ctrl.setNouvelleNoteSur20(14);
        ctrl.setPointsObtenusEpreuveControle(56);
        ctrl.setDifferenceEnPlus(12);
        r.setEpreuvesOralesControle(List.of(ctrl));

        r.setDecisionDeuxiemeGroupe(DecisionJury.ADMIS);
        r.setMentionDeuxiemeGroupe(Mention.PASSABLE);
        r.setDateDeliberationDeuxiemeGroupe(LocalDate.of(2026, 8, 11));
        r.setTotalDefinitif(r.getTotalPremierGroupe() + 12 + 2);

        genererEtEcrire(r, "target/f7-2emegroupe.pdf");
    }

    private void genererEtEcrire(RelevNoteF7 r, String path) throws Exception {
        RelevNoteF7PdfService service = new RelevNoteF7PdfService();
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
