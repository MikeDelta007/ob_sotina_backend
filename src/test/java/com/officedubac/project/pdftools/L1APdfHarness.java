package com.officedubac.project.pdftools;

import com.officedubac.project.modules.L1A.model.*;
import com.officedubac.project.modules.L1A.model.Enums.*;
import com.officedubac.project.modules.L1A.pdf.RelevNoteL1APdfService;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;

public class L1APdfHarness {

    @Test
    void scenario1erGroupeSeulement() throws Exception {
        RelevNoteL1A r = base();
        r.setDecisionPremierGroupe(DecisionJury.ADMIS);
        r.setMentionPremierGroupe(Mention.PASSABLE);
        r.setDateDeliberationPremierGroupe(LocalDate.of(2026, 8, 11));
        r.setDecisionDeuxiemeGroupe(null);
        genererEtEcrire(r, "target/l1a-scenario1.pdf");
    }

    @Test
    void scenario2emeGroupe() throws Exception {
        RelevNoteL1A r = base();
        r.setDecisionPremierGroupe(DecisionJury.AUTORISE_SECOND_GROUPE);
        r.setMentionPremierGroupe(null);
        r.setDateDeliberationPremierGroupe(null);
        r.setDecisionDeuxiemeGroupe(DecisionJury.ADMIS);
        r.setMentionDeuxiemeGroupe(Mention.PASSABLE);
        r.setDateDeliberationDeuxiemeGroupe(LocalDate.of(2026, 8, 11));
        genererEtEcrire(r, "target/l1a-scenario2.pdf");
    }

    private RelevNoteL1A base() {
        RelevNoteL1A r = new RelevNoteL1A();
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
                note(MatieresL1A.FRANCAIS.getCode(), 12, 72),
                note(MatieresL1A.PHILO.getCode(), 12, 72),
                note(MatieresL1A.HIST_GEO.getCode(), 12, 24),
                note(MatieresL1A.LV1.getCode(), 12, 24),
                note(MatieresL1A.MATH.getCode(), 12, 24),
                note(MatieresL1A.GREC.getCode(), 12, 60),
                note(MatieresL1A.LATIN_ARABE.getCode(), 12, 60)
        ));
        r.setTotalPremierGroupe(336);
        r.setReportPremierTotal(336);
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

        r.setTotalProvisoire(342);
        r.setTotalDefinitif(344);
        r.setLieuDeliberation("DAKAR");
        return r;
    }

    private void genererEtEcrire(RelevNoteL1A r, String out) throws Exception {
        RelevNoteL1APdfService service = new RelevNoteL1APdfService();
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
