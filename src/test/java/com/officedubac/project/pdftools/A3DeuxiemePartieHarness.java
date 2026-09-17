package com.officedubac.project.pdftools;

import com.officedubac.project.modules.a3deuxiemepartie.model.*;
import com.officedubac.project.modules.a3deuxiemepartie.model.Enums.DecisionJury;
import com.officedubac.project.modules.a3deuxiemepartie.pdf.ReleveA3DeuxiemePartiePdfService;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;

public class A3DeuxiemePartieHarness {

    @Test
    void genererEchantillon() throws Exception {
        ReleveA3DeuxiemePartie r = new ReleveA3DeuxiemePartie();
        r.setJuryNumero("2");

        Candidat c = new Candidat();
        c.setNomPrenom("MANSOUR DIOUF");
        c.setDateNaissance(LocalDate.of(2000, 7, 27));
        c.setLieuNaissance("DAKAR");
        c.setNumeroTable("255");
        r.setCandidat(c);

        r.setNotesEcrites(List.of(
                note(MatieresA3DeuxiemePartie.PHILO.getCode(), 12, 48),
                note(MatieresA3DeuxiemePartie.LV1_ECRIT.getCode(), 12, 36),
                note(MatieresA3DeuxiemePartie.HIST_GEO.getCode(), 12, 36),
                note(MatieresA3DeuxiemePartie.LV2.getCode(), 12, 24)
        ));
        r.setTotalEcrit(144);

        r.setNotesOrales(List.of(
                note(MatieresA3DeuxiemePartie.LV1_ORAL.getCode(), 12, 24),
                note(MatieresA3DeuxiemePartie.MATH.getCode(), 12, 24)
        ));
        r.setTotalOral(48);
        r.setTotalGeneral(192);

        r.setDecision(DecisionJury.ADMIS);
        r.setLieuDeliberation("DAKAR");
        r.setDateDeliberation(LocalDate.of(2026, 8, 19));

        ReleveA3DeuxiemePartiePdfService service = new ReleveA3DeuxiemePartiePdfService();
        byte[] pdf = service.genererPdf(r);
        try (FileOutputStream fos = new FileOutputStream(System.getProperty("pdf.out",
                "target/a3-2eme-echantillon.pdf"))) {
            fos.write(pdf);
        }
    }

    private static NoteEpreuve note(String code, int note, int points) {
        NoteEpreuve n = new NoteEpreuve(code, note);
        n.setPointsObtenus(points);
        return n;
    }
}
