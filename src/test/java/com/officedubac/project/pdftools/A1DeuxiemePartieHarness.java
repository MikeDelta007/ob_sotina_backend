package com.officedubac.project.pdftools;

import com.officedubac.project.modules.a1deuxiemepartie.model.*;
import com.officedubac.project.modules.a1deuxiemepartie.model.Enums.DecisionJury;
import com.officedubac.project.modules.a1deuxiemepartie.pdf.ReleveA1DeuxiemePartiePdfService;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;

public class A1DeuxiemePartieHarness {

    @Test
    void genererEchantillon() throws Exception {
        ReleveA1DeuxiemePartie r = new ReleveA1DeuxiemePartie();
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
                note(MatieresA1DeuxiemePartie.PHILOSOPHIE.getCode(), 12, 60),
                note(MatieresA1DeuxiemePartie.LATIN_ARABE.getCode(), 12, 24),
                note(MatieresA1DeuxiemePartie.GREC.getCode(), 12, 24),
                note(MatieresA1DeuxiemePartie.LANGUE_VIVANTE.getCode(), 12, 24)
        ));
        r.setTotalEcrit(132);

        r.setNotesOrales(List.of(
                note(MatieresA1DeuxiemePartie.LATIN_GREC_ARABE_ORAL.getCode(), 12, 24),
                note(MatieresA1DeuxiemePartie.HIST_GEO.getCode(), 12, 24),
                note(MatieresA1DeuxiemePartie.MATHEMATIQUES.getCode(), 12, 24)
        ));
        r.setTotalOral(72);
        r.setTotalGeneral(204);

        r.setDecision(DecisionJury.ADMIS);
        r.setLieuDeliberation("DAKAR");
        r.setDateDeliberation(LocalDate.of(2026, 8, 19));

        ReleveA1DeuxiemePartiePdfService service = new ReleveA1DeuxiemePartiePdfService();
        byte[] pdf = service.genererPdf(r);
        try (FileOutputStream fos = new FileOutputStream(System.getProperty("pdf.out",
                "target/a1-2eme-echantillon.pdf"))) {
            fos.write(pdf);
        }
    }

    private static NoteEpreuve note(String code, int note, int points) {
        NoteEpreuve n = new NoteEpreuve(code, note);
        n.setPointsObtenus(points);
        return n;
    }
}
