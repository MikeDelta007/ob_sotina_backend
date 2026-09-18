package com.officedubac.project.pdftools;

import com.officedubac.project.modules.T2.model.*;
import com.officedubac.project.modules.T2.model.Enums.*;
import com.officedubac.project.modules.T2.pdf.RelevNoteT2PdfService;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class T2PdfHarness {

    private RelevNoteT2 base() {
        RelevNoteT2 r = new RelevNoteT2();
        r.setSession(TypeSession.NORMALE);
        r.setJuryNumero("2");
        r.setAnnee(2026);

        Candidat c = new Candidat();
        c.setNomPrenom("MANSOUR DIOUF");
        c.setDateNaissance(LocalDate.of(2000, 7, 27));
        c.setLieuNaissance("DAKAR");
        c.setEtablissement("LBY");
        c.setIndicatif("12");
        c.setOptions("T2");
        c.setNumeroTable("255");
        c.setNationalite("SEN");
        c.setNombreDeFois("1");
        r.setCandidat(c);

        List<NoteEpreuve> g1 = new ArrayList<>();
        for (Matiere m : MatieresT2.PREMIER_GROUPE) {
            g1.add(note(m.getCode(), 12, 12 * m.getCoefficient()));
        }
        r.setNotesPremierGroupe(g1);
        r.setTotalPremierGroupe(12 * 32);
        r.setReportPremierTotal(12 * 32);
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

        r.setTotalProvisoire(400);
        r.setTotalDefinitif(402);
        r.setLieuDeliberation("DAKAR");
        return r;
    }

    @Test
    void scenarioPremierGroupeSeulement() throws Exception {
        RelevNoteT2 r = base();
        r.setDecisionPremierGroupe(DecisionJury.ADMIS);
        r.setMentionPremierGroupe(Mention.PASSABLE);
        r.setDateDeliberationPremierGroupe(LocalDate.of(2026, 8, 11));
        r.setDecisionDeuxiemeGroupe(null);

        genererEtSauver(r, "target/t2-echantillon-groupe1.pdf");
    }

    @Test
    void scenarioDeuxiemeGroupe() throws Exception {
        RelevNoteT2 r = base();
        r.setDecisionPremierGroupe(DecisionJury.AUTORISE_SECOND_GROUPE);
        r.setMentionPremierGroupe(null);
        r.setDateDeliberationPremierGroupe(null);

        EpreuveOraleControle ctrl = new EpreuveOraleControle();
        ctrl.setMatiereChoisie("Electrotechnique");
        ctrl.setCoefficient(6);
        ctrl.setRappelPointsObtenus1erGroupe(72);
        ctrl.setNouvelleNoteSur20(14);
        ctrl.setPointsObtenusEpreuveControle(84);
        ctrl.setDifferenceEnPlus(12);
        r.setEpreuvesOralesControle(List.of(ctrl));

        r.setDecisionDeuxiemeGroupe(DecisionJury.ADMIS);
        r.setMentionDeuxiemeGroupe(Mention.PASSABLE);
        r.setDateDeliberationDeuxiemeGroupe(LocalDate.of(2026, 8, 19));

        genererEtSauver(r, "target/t2-echantillon-groupe2.pdf");
    }

    private void genererEtSauver(RelevNoteT2 r, String path) throws Exception {
        RelevNoteT2PdfService service = new RelevNoteT2PdfService();
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
