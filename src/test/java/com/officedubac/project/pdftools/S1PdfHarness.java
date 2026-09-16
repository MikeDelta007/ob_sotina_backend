package com.officedubac.project.pdftools;

import com.officedubac.project.modules.S1.model.*;
import com.officedubac.project.modules.S1.model.Enums.*;
import com.officedubac.project.modules.S1.pdf.RelevNoteS1PdfService;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;

public class S1PdfHarness {

    @Test
    void scenario1erGroupeSeulement() throws Exception {
        RelevNoteS1 r = base();
        r.setDecisionPremierGroupe(DecisionJury.ADMIS);
        r.setMentionPremierGroupe(Mention.PASSABLE);
        r.setDateDeliberationPremierGroupe(LocalDate.of(2026, 8, 11));
        r.setDecisionDeuxiemeGroupe(null);
        genererEtEcrire(r, "target/s1-scenario1.pdf");
    }

    @Test
    void scenario2emeGroupe() throws Exception {
        RelevNoteS1 r = base();
        r.setDecisionPremierGroupe(DecisionJury.AUTORISE_SECOND_GROUPE);
        r.setMentionPremierGroupe(null);
        r.setDateDeliberationPremierGroupe(null);
        r.setDecisionDeuxiemeGroupe(DecisionJury.ADMIS);
        r.setMentionDeuxiemeGroupe(Mention.PASSABLE);
        r.setDateDeliberationDeuxiemeGroupe(LocalDate.of(2026, 8, 11));
        genererEtEcrire(r, "target/s1-scenario2.pdf");
    }

    private RelevNoteS1 base() {
        RelevNoteS1 r = new RelevNoteS1();
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
                note(MatieresS1.FRANCAIS.getCode(), 12, 36),
                note(MatieresS1.PHILO.getCode(), 12, 24),
                note(MatieresS1.MATH.getCode(), 12, 96),
                note(MatieresS1.SC_PHYS.getCode(), 12, 96),
                note(MatieresS1.HIST_GEO.getCode(), 12, 24),
                note(MatieresS1.SC_NAT.getCode(), 12, 24),
                note(MatieresS1.ANGLAIS.getCode(), 12, 24)
        ));
        r.setTotalPremierGroupe(324);
        r.setReportPremierTotal(324);
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

        r.setTotalProvisoire(330);
        r.setTotalDefinitif(332);
        r.setLieuDeliberation("DAKAR");
        return r;
    }

    private void genererEtEcrire(RelevNoteS1 r, String out) throws Exception {
        RelevNoteS1PdfService service = new RelevNoteS1PdfService();
        byte[] pdf = service.genererPdf(r);
        try (FileOutputStream fos = new FileOutputStream(out)) {
            fos.write(pdf);
        }
    }

    private static NoteEpreuve note(String code, int note, int points) {
        NoteEpreuve n = new NoteEpreuve(code, note);
        n.setPointsObtenus(points);
        return n;
    }
}
