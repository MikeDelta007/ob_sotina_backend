package com.officedubac.project.pdftools;

import com.officedubac.project.modules.a2deuxiemepartie.model.*;
import com.officedubac.project.modules.a2deuxiemepartie.model.Enums.DecisionJury;
import com.officedubac.project.modules.a2deuxiemepartie.pdf.ReleveA2DeuxiemePartiePdfService;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;

public class A2DeuxiemePartieHarness {

    @Test
    void genererEchantillon() throws Exception {
        ReleveA2DeuxiemePartie r = new ReleveA2DeuxiemePartie();
        r.setJuryNumero("2");

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

        r.setNotesEcrites(List.of(
                note(MatieresA2DeuxiemePartie.PHILOSOPHIE.getCode(), 12, 60),
                note(MatieresA2DeuxiemePartie.LV1_ECRIT.getCode(), 12, 24),
                note(MatieresA2DeuxiemePartie.HIST_GEO.getCode(), 12, 24),
                note(MatieresA2DeuxiemePartie.LATIN_ARABE.getCode(), 12, 24)
        ));
        r.setTotalEcrit(132);

        r.setNotesOrales(List.of(
                note(MatieresA2DeuxiemePartie.LV1_ORAL.getCode(), 12, 24),
                note(MatieresA2DeuxiemePartie.LV2_ORAL.getCode(), 12, 24),
                note(MatieresA2DeuxiemePartie.MATHEMATIQUES.getCode(), 12, 24)
        ));
        r.setTotalOral(72);
        r.setTotalGeneral(204);

        r.setDecision(DecisionJury.ADMIS);
        r.setLieuDeliberation("DAKAR");
        r.setDateDeliberation(LocalDate.of(2026, 8, 19));

        ReleveA2DeuxiemePartiePdfService service = new ReleveA2DeuxiemePartiePdfService();
        byte[] pdf = service.genererPdf(r);
        try (FileOutputStream fos = new FileOutputStream(System.getProperty("pdf.out",
                "target/a2-2eme-echantillon.pdf"))) {
            fos.write(pdf);
        }
    }

    private static NoteEpreuve note(String code, int note, int points) {
        NoteEpreuve n = new NoteEpreuve(code, note);
        n.setPointsObtenus(points);
        return n;
    }
}
