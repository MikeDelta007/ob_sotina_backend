package com.officedubac.project;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Insère un relevé d'exemple par série dans la vraie base MongoDB configurée
 * (application.properties), pour vérification manuelle du rendu PDF via Postman
 * (GET http://localhost:8080/ob{mapping}/{id}/pdf).
 *
 * Lancer avec : mvn -o test -Dtest=SeedAllSeriesTest
 * Les identifiants et URLs sont affichés dans la console (System.out).
 */
@SpringBootTest
public class SeedAllSeriesTest {

    @Autowired private com.officedubac.project.modules.A3.service.RelevNoteA3Service A3Service;
    @Autowired private com.officedubac.project.modules.B.service.RelevNoteBService BService;
    @Autowired private com.officedubac.project.modules.D.service.RelevNoteDService DService;
    @Autowired private com.officedubac.project.modules.E.service.RelevNoteEService EService;
    @Autowired private com.officedubac.project.modules.F1.service.RelevNoteF1Service F1Service;
    @Autowired private com.officedubac.project.modules.F7.service.RelevNoteF7Service F7Service;
    @Autowired private com.officedubac.project.modules.G.service.RelevNoteGService GService;
    @Autowired private com.officedubac.project.modules.G1.service.RelevNoteG1Service G1Service;
    @Autowired private com.officedubac.project.modules.G2.service.RelevNoteG2Service G2Service;
    @Autowired private com.officedubac.project.modules.Lprime1.service.RelevNoteLPrime1Service Lprime1Service;
    @Autowired private com.officedubac.project.modules.L1A.service.RelevNoteL1AService L1AService;
    @Autowired private com.officedubac.project.modules.L1B.service.RelevNoteL1BService L1BService;
    @Autowired private com.officedubac.project.modules.L2.service.RelevNoteL2Service L2Service;
    @Autowired private com.officedubac.project.modules.S1.service.RelevNoteS1Service S1Service;
    @Autowired private com.officedubac.project.modules.S2.service.RelevNoteS2Service S2Service;
    @Autowired private com.officedubac.project.modules.S3.service.RelevNoteS3Service S3Service;
    @Autowired private com.officedubac.project.modules.S4.service.RelevNoteS4Service S4Service;
    @Autowired private com.officedubac.project.modules.S5.service.RelevNoteS5Service S5Service;
    @Autowired private com.officedubac.project.modules.T1.service.RelevNoteT1Service T1Service;
    @Autowired private com.officedubac.project.modules.T2.service.RelevNoteT2Service T2Service;
    @Autowired private com.officedubac.project.modules.A4.service.RelevNoteA4Service A4Service;
    @Autowired private com.officedubac.project.modules.A1.service.RelevNoteA1Service A1Service;
    @Autowired private com.officedubac.project.modules.A2.service.RelevNoteA2Service A2Service;
    @Autowired private com.officedubac.project.modules.a3deuxiemepartie.service.ReleveA3DeuxiemePartieService a3deuxiemepartieService;
    @Autowired private com.officedubac.project.modules.c2emepartie.service.ReleveC2emePartieService c2emepartieService;
    @Autowired private com.officedubac.project.modules.ddeuxiemepartie.service.ReleveDDeuxiemePartieService ddeuxiemepartieService;
    @Autowired private com.officedubac.project.modules.f1deuxiemepartie.service.ReleveF1DeuxiemePartieService f1deuxiemepartieService;
    @Autowired private com.officedubac.project.modules.a1deuxiemepartie.service.ReleveA1DeuxiemePartieService a1deuxiemepartieService;
    @Autowired private com.officedubac.project.modules.a2deuxiemepartie.service.ReleveA2DeuxiemePartieService a2deuxiemepartieService;

    private static final String BASE_URL = "http://localhost:8080/ob";

    private void afficher(String serie, String mapping, String id) {
        System.out.println(serie + " => " + BASE_URL + mapping + "/" + id + "/pdf");
    }

    @Test
    void seedToutesLesSeries() throws Exception {

        {
            com.officedubac.project.modules.A3.dto.RelevNoteA3SaisieRequest req = new com.officedubac.project.modules.A3.dto.RelevNoteA3SaisieRequest();
            req.setSession(com.officedubac.project.modules.A3.model.Enums.TypeSession.NORMALE);
            req.setJuryNumero("11");
            req.setAnnee(2026);
            req.setNomPrenom("CANDIDAT TEST 1");
            req.setDateNaissance(LocalDate.of(2005, 1, 15));
            req.setLieuNaissance("DAKAR");
            req.setEtablissement("LYCEE TEST");
            req.setIndicatif("LT");
            req.setOptions("A3");
            req.setNumeroTable("100001");
            req.setNationalite("SEN");
            req.setNombreDeFois("1");
        Map<String, Integer> g1 = new HashMap<>();
        g1.put("FR_ECRIT", 12);
        g1.put("FR_ORAL", 14);
        g1.put("PHILO", 9);
        g1.put("HIST_GEO", 16);
        g1.put("LV1_ECRIT", 11);
        g1.put("MATH", 13);
        g1.put("LV2_ECRIT", 15);
        g1.put("LV1_ORAL", 10);
            req.setNotesPremierGroupe(g1);
            Map<String, Integer> g2 = new HashMap<>();
            req.setNotesDeuxiemeGroupe(g2);

            com.officedubac.project.modules.A3.dto.RelevNoteA3SaisieRequest.EpreuveOraleControleSaisie ctrl = new com.officedubac.project.modules.A3.dto.RelevNoteA3SaisieRequest.EpreuveOraleControleSaisie();
            ctrl.setMatiereChoisie("MATIERE DE CONTROLE");
            ctrl.setCoefficient(2);
            ctrl.setRappelPointsObtenus1erGroupe(20);
            ctrl.setNouvelleNoteSur20(12);
            req.setEpreuvesOralesControle(List.of(ctrl));

            com.officedubac.project.modules.A3.dto.RelevNoteA3SaisieRequest.EpreuveFacultativeSaisie fac = new com.officedubac.project.modules.A3.dto.RelevNoteA3SaisieRequest.EpreuveFacultativeSaisie();
            fac.setType("LANGUE");
            fac.setNote(14);
            req.setEpreuvesFacultatives(List.of(fac));

            com.officedubac.project.modules.A3.dto.RelevNoteA3SaisieRequest.EducationPhysiqueSaisie ep = new com.officedubac.project.modules.A3.dto.RelevNoteA3SaisieRequest.EducationPhysiqueSaisie();
            ep.setNote(13);
            req.setEducationPhysique(ep);

            req.setLieuDelivrance("DAKAR");
            req.setDateDelivrance(LocalDate.of(2026, 7, 20));
            req.setPresidentJury("Professeur TEST");

            com.officedubac.project.modules.A3.model.RelevNoteA3 releve = A3Service.creer(req);
            afficher("A3", "/api/v1/releves-a3", releve.getId());
        }

        {
            com.officedubac.project.modules.B.dto.RelevNoteBSaisieRequest req = new com.officedubac.project.modules.B.dto.RelevNoteBSaisieRequest();
            req.setSession(com.officedubac.project.modules.B.model.Enums.TypeSession.NORMALE);
            req.setJuryNumero("12");
            req.setAnnee(2026);
            req.setNomPrenom("CANDIDAT TEST 2");
            req.setDateNaissance(LocalDate.of(2005, 1, 15));
            req.setLieuNaissance("DAKAR");
            req.setEtablissement("LYCEE TEST");
            req.setIndicatif("LT");
            req.setOptions("B");
            req.setNumeroTable("100002");
            req.setNationalite("SEN");
            req.setNombreDeFois("1");
        Map<String, Integer> g1 = new HashMap<>();
        g1.put("FR_ECRIT", 12);
        g1.put("FR_ORAL", 14);
        g1.put("PHILO", 9);
        g1.put("HIST_GEO", 16);
        g1.put("SC_ECO_SOC", 11);
        g1.put("MATH", 13);
        g1.put("LV1", 15);
        g1.put("LV2", 10);
            req.setNotesPremierGroupe(g1);
            Map<String, Integer> g2 = new HashMap<>();
            req.setNotesDeuxiemeGroupe(g2);

            com.officedubac.project.modules.B.dto.RelevNoteBSaisieRequest.EpreuveOraleControleSaisie ctrl = new com.officedubac.project.modules.B.dto.RelevNoteBSaisieRequest.EpreuveOraleControleSaisie();
            ctrl.setMatiereChoisie("MATIERE DE CONTROLE");
            ctrl.setCoefficient(2);
            ctrl.setRappelPointsObtenus1erGroupe(20);
            ctrl.setNouvelleNoteSur20(12);
            req.setEpreuvesOralesControle(List.of(ctrl));

            com.officedubac.project.modules.B.dto.RelevNoteBSaisieRequest.EpreuveFacultativeSaisie fac = new com.officedubac.project.modules.B.dto.RelevNoteBSaisieRequest.EpreuveFacultativeSaisie();
            fac.setType("LANGUE");
            fac.setNote(14);
            req.setEpreuvesFacultatives(List.of(fac));

            com.officedubac.project.modules.B.dto.RelevNoteBSaisieRequest.EducationPhysiqueSaisie ep = new com.officedubac.project.modules.B.dto.RelevNoteBSaisieRequest.EducationPhysiqueSaisie();
            ep.setNote(13);
            req.setEducationPhysique(ep);

            req.setLieuDelivrance("DAKAR");
            req.setDateDelivrance(LocalDate.of(2026, 7, 20));
            req.setPresidentJury("Professeur TEST");

            com.officedubac.project.modules.B.model.RelevNoteB releve = BService.creer(req);
            afficher("B", "/api/v1/releves-b", releve.getId());
        }

        {
            com.officedubac.project.modules.D.dto.RelevNoteDSaisieRequest req = new com.officedubac.project.modules.D.dto.RelevNoteDSaisieRequest();
            req.setSession(com.officedubac.project.modules.D.model.Enums.TypeSession.NORMALE);
            req.setJuryNumero("13");
            req.setAnnee(2026);
            req.setNomPrenom("CANDIDAT TEST 3");
            req.setDateNaissance(LocalDate.of(2005, 1, 15));
            req.setLieuNaissance("DAKAR");
            req.setEtablissement("LYCEE TEST");
            req.setIndicatif("LT");
            req.setOptions("D");
            req.setNumeroTable("100003");
            req.setNationalite("SEN");
            req.setNombreDeFois("1");
        Map<String, Integer> g1 = new HashMap<>();
        g1.put("FR_ECRIT", 12);
        g1.put("FR_ORAL", 14);
        g1.put("PHILO", 9);
        g1.put("MATH", 16);
        g1.put("SC_PHYS", 11);
        g1.put("SC_NAT", 13);
        g1.put("HIST_GEO", 15);
        g1.put("LV", 10);
            req.setNotesPremierGroupe(g1);
            Map<String, Integer> g2 = new HashMap<>();
            req.setNotesDeuxiemeGroupe(g2);

            com.officedubac.project.modules.D.dto.RelevNoteDSaisieRequest.EpreuveOraleControleSaisie ctrl = new com.officedubac.project.modules.D.dto.RelevNoteDSaisieRequest.EpreuveOraleControleSaisie();
            ctrl.setMatiereChoisie("MATIERE DE CONTROLE");
            ctrl.setCoefficient(2);
            ctrl.setRappelPointsObtenus1erGroupe(20);
            ctrl.setNouvelleNoteSur20(12);
            req.setEpreuvesOralesControle(List.of(ctrl));

            com.officedubac.project.modules.D.dto.RelevNoteDSaisieRequest.EpreuveFacultativeSaisie fac = new com.officedubac.project.modules.D.dto.RelevNoteDSaisieRequest.EpreuveFacultativeSaisie();
            fac.setType("LANGUE");
            fac.setNote(14);
            req.setEpreuvesFacultatives(List.of(fac));

            com.officedubac.project.modules.D.dto.RelevNoteDSaisieRequest.EducationPhysiqueSaisie ep = new com.officedubac.project.modules.D.dto.RelevNoteDSaisieRequest.EducationPhysiqueSaisie();
            ep.setNote(13);
            req.setEducationPhysique(ep);

            req.setLieuDelivrance("DAKAR");
            req.setDateDelivrance(LocalDate.of(2026, 7, 20));
            req.setPresidentJury("Professeur TEST");

            com.officedubac.project.modules.D.model.RelevNoteD releve = DService.creer(req);
            afficher("D", "/api/v1/releves-d", releve.getId());
        }

        {
            com.officedubac.project.modules.E.dto.RelevNoteESaisieRequest req = new com.officedubac.project.modules.E.dto.RelevNoteESaisieRequest();
            req.setSession(com.officedubac.project.modules.E.model.Enums.TypeSession.NORMALE);
            req.setJuryNumero("14");
            req.setAnnee(2026);
            req.setNomPrenom("CANDIDAT TEST 4");
            req.setDateNaissance(LocalDate.of(2005, 1, 15));
            req.setLieuNaissance("DAKAR");
            req.setEtablissement("LYCEE TEST");
            req.setIndicatif("LT");
            req.setOptions("E");
            req.setNumeroTable("100004");
            req.setNationalite("SEN");
            req.setNombreDeFois("1");
        Map<String, Integer> g1 = new HashMap<>();
        g1.put("FR_ECRIT", 12);
        g1.put("FR_ORAL", 14);
        g1.put("PHILO", 9);
        g1.put("MATH", 16);
        g1.put("SC_PHYS", 11);
        g1.put("CONS_MECA", 13);
        g1.put("AN_FAB_TAUT", 15);
        g1.put("TECH_PRATIQUE", 10);
        g1.put("LV", 17);
            req.setNotesPremierGroupe(g1);
            Map<String, Integer> g2 = new HashMap<>();
            req.setNotesDeuxiemeGroupe(g2);

            com.officedubac.project.modules.E.dto.RelevNoteESaisieRequest.EpreuveOraleControleSaisie ctrl = new com.officedubac.project.modules.E.dto.RelevNoteESaisieRequest.EpreuveOraleControleSaisie();
            ctrl.setMatiereChoisie("MATIERE DE CONTROLE");
            ctrl.setCoefficient(2);
            ctrl.setRappelPointsObtenus1erGroupe(20);
            ctrl.setNouvelleNoteSur20(12);
            req.setEpreuvesOralesControle(List.of(ctrl));

            com.officedubac.project.modules.E.dto.RelevNoteESaisieRequest.EpreuveFacultativeSaisie fac = new com.officedubac.project.modules.E.dto.RelevNoteESaisieRequest.EpreuveFacultativeSaisie();
            fac.setType("LANGUE");
            fac.setNote(14);
            req.setEpreuvesFacultatives(List.of(fac));

            com.officedubac.project.modules.E.dto.RelevNoteESaisieRequest.EducationPhysiqueSaisie ep = new com.officedubac.project.modules.E.dto.RelevNoteESaisieRequest.EducationPhysiqueSaisie();
            ep.setNote(13);
            req.setEducationPhysique(ep);

            req.setLieuDelivrance("DAKAR");
            req.setDateDelivrance(LocalDate.of(2026, 7, 20));
            req.setPresidentJury("Professeur TEST");

            com.officedubac.project.modules.E.model.RelevNoteE releve = EService.creer(req);
            afficher("E", "/api/v1/releves-e", releve.getId());
        }

        {
            com.officedubac.project.modules.F1.dto.RelevNoteF1SaisieRequest req = new com.officedubac.project.modules.F1.dto.RelevNoteF1SaisieRequest();
            req.setSession(com.officedubac.project.modules.F1.model.Enums.TypeSession.NORMALE);
            req.setJuryNumero("15");
            req.setAnnee(2026);
            req.setNomPrenom("CANDIDAT TEST 5");
            req.setDateNaissance(LocalDate.of(2005, 1, 15));
            req.setLieuNaissance("DAKAR");
            req.setEtablissement("LYCEE TEST");
            req.setIndicatif("LT");
            req.setOptions("F1");
            req.setNumeroTable("100005");
            req.setNationalite("SEN");
            req.setNombreDeFois("1");
        Map<String, Integer> g1 = new HashMap<>();
        g1.put("FR_ECRIT", 12);
        g1.put("FR_ORAL", 14);
        g1.put("MATH", 9);
        g1.put("MECANIQUE", 16);
        g1.put("CONST_MECA", 11);
        g1.put("ANALYSE_FAB", 13);
        g1.put("ELEC_METAL", 15);
        g1.put("TECHNO_AUTOM", 10);
        g1.put("ANGLAIS", 17);
        g1.put("EPR_PRATIQUE", 8);
            req.setNotesPremierGroupe(g1);
            Map<String, Integer> g2 = new HashMap<>();
            req.setNotesDeuxiemeGroupe(g2);

            com.officedubac.project.modules.F1.dto.RelevNoteF1SaisieRequest.EpreuveOraleControleSaisie ctrl = new com.officedubac.project.modules.F1.dto.RelevNoteF1SaisieRequest.EpreuveOraleControleSaisie();
            ctrl.setMatiereChoisie("MATIERE DE CONTROLE");
            ctrl.setCoefficient(2);
            ctrl.setRappelPointsObtenus1erGroupe(20);
            ctrl.setNouvelleNoteSur20(12);
            req.setEpreuvesOralesControle(List.of(ctrl));

            com.officedubac.project.modules.F1.dto.RelevNoteF1SaisieRequest.EpreuveFacultativeSaisie fac = new com.officedubac.project.modules.F1.dto.RelevNoteF1SaisieRequest.EpreuveFacultativeSaisie();
            fac.setType("LANGUE");
            fac.setNote(14);
            req.setEpreuvesFacultatives(List.of(fac));

            com.officedubac.project.modules.F1.dto.RelevNoteF1SaisieRequest.EducationPhysiqueSaisie ep = new com.officedubac.project.modules.F1.dto.RelevNoteF1SaisieRequest.EducationPhysiqueSaisie();
            ep.setNote(13);
            req.setEducationPhysique(ep);

            req.setLieuDelivrance("DAKAR");
            req.setDateDelivrance(LocalDate.of(2026, 7, 20));
            req.setPresidentJury("Professeur TEST");

            com.officedubac.project.modules.F1.model.RelevNoteF1 releve = F1Service.creer(req);
            afficher("F1", "/api/v1/releves-f1", releve.getId());
        }

        {
            com.officedubac.project.modules.F7.dto.RelevNoteF7SaisieRequest req = new com.officedubac.project.modules.F7.dto.RelevNoteF7SaisieRequest();
            req.setSession(com.officedubac.project.modules.F7.model.Enums.TypeSession.NORMALE);
            req.setJuryNumero("16");
            req.setAnnee(2026);
            req.setNomPrenom("CANDIDAT TEST 6");
            req.setDateNaissance(LocalDate.of(2005, 1, 15));
            req.setLieuNaissance("DAKAR");
            req.setEtablissement("LYCEE TEST");
            req.setIndicatif("LT");
            req.setOptions("F7");
            req.setNumeroTable("100006");
            req.setNationalite("SEN");
            req.setNombreDeFois("1");
        Map<String, Integer> g1 = new HashMap<>();
        g1.put("FR_ECRIT", 12);
        g1.put("FR_ORAL", 14);
        g1.put("MATH", 9);
        g1.put("BIOLOGIE", 16);
        g1.put("BIOCHIMIE", 11);
        g1.put("MICROBIO", 13);
        g1.put("PHYSIOLOGIE", 15);
        g1.put("LV", 10);
        g1.put("TP_BIOLOGIE", 17);
        g1.put("TP_BIOCHIMIE", 8);
            req.setNotesPremierGroupe(g1);
            Map<String, Integer> g2 = new HashMap<>();
            req.setNotesDeuxiemeGroupe(g2);

            com.officedubac.project.modules.F7.dto.RelevNoteF7SaisieRequest.EpreuveOraleControleSaisie ctrl = new com.officedubac.project.modules.F7.dto.RelevNoteF7SaisieRequest.EpreuveOraleControleSaisie();
            ctrl.setMatiereChoisie("MATIERE DE CONTROLE");
            ctrl.setCoefficient(2);
            ctrl.setRappelPointsObtenus1erGroupe(20);
            ctrl.setNouvelleNoteSur20(12);
            req.setEpreuvesOralesControle(List.of(ctrl));

            com.officedubac.project.modules.F7.dto.RelevNoteF7SaisieRequest.EpreuveFacultativeSaisie fac = new com.officedubac.project.modules.F7.dto.RelevNoteF7SaisieRequest.EpreuveFacultativeSaisie();
            fac.setType("LANGUE");
            fac.setNote(14);
            req.setEpreuvesFacultatives(List.of(fac));

            com.officedubac.project.modules.F7.dto.RelevNoteF7SaisieRequest.EducationPhysiqueSaisie ep = new com.officedubac.project.modules.F7.dto.RelevNoteF7SaisieRequest.EducationPhysiqueSaisie();
            ep.setNote(13);
            req.setEducationPhysique(ep);

            req.setLieuDelivrance("DAKAR");
            req.setDateDelivrance(LocalDate.of(2026, 7, 20));
            req.setPresidentJury("Professeur TEST");

            com.officedubac.project.modules.F7.model.RelevNoteF7 releve = F7Service.creer(req);
            afficher("F7", "/api/v1/releves-f7", releve.getId());
        }

        {
            com.officedubac.project.modules.G.dto.RelevNoteGSaisieRequest req = new com.officedubac.project.modules.G.dto.RelevNoteGSaisieRequest();
            req.setSession(com.officedubac.project.modules.G.model.Enums.TypeSession.NORMALE);
            req.setJuryNumero("17");
            req.setAnnee(2026);
            req.setNomPrenom("CANDIDAT TEST 7");
            req.setDateNaissance(LocalDate.of(2005, 1, 15));
            req.setLieuNaissance("DAKAR");
            req.setEtablissement("LYCEE TEST");
            req.setIndicatif("LT");
            req.setOptions("G");
            req.setNumeroTable("100007");
            req.setNationalite("SEN");
            req.setNombreDeFois("1");
        Map<String, Integer> g1 = new HashMap<>();
        g1.put("TECH_EXPR", 12);
        g1.put("ANGLAIS", 14);
        g1.put("PHILO", 9);
        g1.put("MATH", 16);
        g1.put("ECONOMIE_GEN", 11);
        g1.put("ETUDE_CAS", 13);
        g1.put("CONN_MONDE", 15);
        g1.put("CORRESPONDANCE", 10);
        g1.put("TRAITEMENT_INFO", 17);
            req.setNotesPremierGroupe(g1);
            Map<String, Integer> g2 = new HashMap<>();
            req.setNotesDeuxiemeGroupe(g2);

            com.officedubac.project.modules.G.dto.RelevNoteGSaisieRequest.EpreuveOraleControleSaisie ctrl = new com.officedubac.project.modules.G.dto.RelevNoteGSaisieRequest.EpreuveOraleControleSaisie();
            ctrl.setMatiereChoisie("MATIERE DE CONTROLE");
            ctrl.setCoefficient(2);
            ctrl.setRappelPointsObtenus1erGroupe(20);
            ctrl.setNouvelleNoteSur20(12);
            req.setEpreuvesOralesControle(List.of(ctrl));

            com.officedubac.project.modules.G.dto.RelevNoteGSaisieRequest.EpreuveFacultativeSaisie fac = new com.officedubac.project.modules.G.dto.RelevNoteGSaisieRequest.EpreuveFacultativeSaisie();
            fac.setType("LANGUE");
            fac.setNote(14);
            req.setEpreuvesFacultatives(List.of(fac));

            com.officedubac.project.modules.G.dto.RelevNoteGSaisieRequest.EducationPhysiqueSaisie ep = new com.officedubac.project.modules.G.dto.RelevNoteGSaisieRequest.EducationPhysiqueSaisie();
            ep.setNote(13);
            req.setEducationPhysique(ep);

            req.setLieuDelivrance("DAKAR");
            req.setDateDelivrance(LocalDate.of(2026, 7, 20));
            req.setPresidentJury("Professeur TEST");

            com.officedubac.project.modules.G.model.RelevNoteG releve = GService.creer(req);
            afficher("G", "/api/v1/releves-g", releve.getId());
        }

        {
            com.officedubac.project.modules.G1.dto.RelevNoteG1SaisieRequest req = new com.officedubac.project.modules.G1.dto.RelevNoteG1SaisieRequest();
            req.setSession(com.officedubac.project.modules.G1.model.Enums.TypeSession.NORMALE);
            req.setJuryNumero("18");
            req.setAnnee(2026);
            req.setNomPrenom("CANDIDAT TEST 8");
            req.setDateNaissance(LocalDate.of(2005, 1, 15));
            req.setLieuNaissance("DAKAR");
            req.setEtablissement("LYCEE TEST");
            req.setIndicatif("LT");
            req.setOptions("G1");
            req.setNumeroTable("100008");
            req.setNationalite("SEN");
            req.setNombreDeFois("1");
        Map<String, Integer> g1 = new HashMap<>();
        g1.put("FR_ECRIT", 12);
        g1.put("FR_ORAL", 14);
        g1.put("PHILO", 9);
        g1.put("ECONOMIE", 16);
        g1.put("LV1", 11);
        g1.put("ETUDE_CAS", 13);
        g1.put("CONN_MONDE", 15);
        g1.put("ORGAN_ADMIN", 10);
        g1.put("LV2", 17);
            req.setNotesPremierGroupe(g1);
            Map<String, Integer> g2 = new HashMap<>();
            req.setNotesDeuxiemeGroupe(g2);

            com.officedubac.project.modules.G1.dto.RelevNoteG1SaisieRequest.EpreuveOraleControleSaisie ctrl = new com.officedubac.project.modules.G1.dto.RelevNoteG1SaisieRequest.EpreuveOraleControleSaisie();
            ctrl.setMatiereChoisie("MATIERE DE CONTROLE");
            ctrl.setCoefficient(2);
            ctrl.setRappelPointsObtenus1erGroupe(20);
            ctrl.setNouvelleNoteSur20(12);
            req.setEpreuvesOralesControle(List.of(ctrl));

            com.officedubac.project.modules.G1.dto.RelevNoteG1SaisieRequest.EpreuveFacultativeSaisie fac = new com.officedubac.project.modules.G1.dto.RelevNoteG1SaisieRequest.EpreuveFacultativeSaisie();
            fac.setType("LANGUE");
            fac.setNote(14);
            req.setEpreuvesFacultatives(List.of(fac));

            com.officedubac.project.modules.G1.dto.RelevNoteG1SaisieRequest.EducationPhysiqueSaisie ep = new com.officedubac.project.modules.G1.dto.RelevNoteG1SaisieRequest.EducationPhysiqueSaisie();
            ep.setNote(13);
            req.setEducationPhysique(ep);

            req.setLieuDelivrance("DAKAR");
            req.setDateDelivrance(LocalDate.of(2026, 7, 20));
            req.setPresidentJury("Professeur TEST");

            com.officedubac.project.modules.G1.model.RelevNoteG1 releve = G1Service.creer(req);
            afficher("G1", "/api/v1/releves-g1", releve.getId());
        }

        {
            com.officedubac.project.modules.G2.dto.RelevNoteG2SaisieRequest req = new com.officedubac.project.modules.G2.dto.RelevNoteG2SaisieRequest();
            req.setSession(com.officedubac.project.modules.G2.model.Enums.TypeSession.NORMALE);
            req.setJuryNumero("19");
            req.setAnnee(2026);
            req.setNomPrenom("CANDIDAT TEST 9");
            req.setDateNaissance(LocalDate.of(2005, 1, 15));
            req.setLieuNaissance("DAKAR");
            req.setEtablissement("LYCEE TEST");
            req.setIndicatif("LT");
            req.setOptions("G2");
            req.setNumeroTable("100009");
            req.setNationalite("SEN");
            req.setNombreDeFois("1");
        Map<String, Integer> g1 = new HashMap<>();
        g1.put("FR_ECRIT", 12);
        g1.put("FR_ORAL", 14);
        g1.put("PHILO", 9);
        g1.put("MATH", 16);
        g1.put("ECONOMIE", 11);
        g1.put("ETUDE_CAS", 13);
        g1.put("CONN_MONDE", 15);
        g1.put("CORRESPON_DACTYLO", 10);
        g1.put("LV1", 17);
        g1.put("LV2", 8);
            req.setNotesPremierGroupe(g1);
            Map<String, Integer> g2 = new HashMap<>();
            req.setNotesDeuxiemeGroupe(g2);

            com.officedubac.project.modules.G2.dto.RelevNoteG2SaisieRequest.EpreuveOraleControleSaisie ctrl = new com.officedubac.project.modules.G2.dto.RelevNoteG2SaisieRequest.EpreuveOraleControleSaisie();
            ctrl.setMatiereChoisie("MATIERE DE CONTROLE");
            ctrl.setCoefficient(2);
            ctrl.setRappelPointsObtenus1erGroupe(20);
            ctrl.setNouvelleNoteSur20(12);
            req.setEpreuvesOralesControle(List.of(ctrl));

            com.officedubac.project.modules.G2.dto.RelevNoteG2SaisieRequest.EpreuveFacultativeSaisie fac = new com.officedubac.project.modules.G2.dto.RelevNoteG2SaisieRequest.EpreuveFacultativeSaisie();
            fac.setType("LANGUE");
            fac.setNote(14);
            req.setEpreuvesFacultatives(List.of(fac));

            com.officedubac.project.modules.G2.dto.RelevNoteG2SaisieRequest.EducationPhysiqueSaisie ep = new com.officedubac.project.modules.G2.dto.RelevNoteG2SaisieRequest.EducationPhysiqueSaisie();
            ep.setNote(13);
            req.setEducationPhysique(ep);

            req.setLieuDelivrance("DAKAR");
            req.setDateDelivrance(LocalDate.of(2026, 7, 20));
            req.setPresidentJury("Professeur TEST");

            com.officedubac.project.modules.G2.model.RelevNoteG2 releve = G2Service.creer(req);
            afficher("G2", "/api/v1/releves-g2", releve.getId());
        }

        {
            com.officedubac.project.modules.Lprime1.dto.RelevNoteLPrime1SaisieRequest req = new com.officedubac.project.modules.Lprime1.dto.RelevNoteLPrime1SaisieRequest();
            req.setSession(com.officedubac.project.modules.Lprime1.model.Enums.TypeSession.NORMALE);
            req.setJuryNumero("20");
            req.setAnnee(2026);
            req.setNomPrenom("CANDIDAT TEST 10");
            req.setDateNaissance(LocalDate.of(2005, 1, 15));
            req.setLieuNaissance("DAKAR");
            req.setEtablissement("LYCEE TEST");
            req.setIndicatif("LT");
            req.setOptions("Lprime1");
            req.setNumeroTable("100010");
            req.setNationalite("SEN");
            req.setNombreDeFois("1");
        Map<String, Integer> g1 = new HashMap<>();
        g1.put("FRANCAIS", 12);
        g1.put("PHILO", 14);
        g1.put("HIST_GEO", 9);
        g1.put("LV1_ECRIT", 16);
        g1.put("MATH", 11);
        g1.put("LV2", 13);
        g1.put("LV1_ORAL", 15);
            req.setNotesPremierGroupe(g1);
            Map<String, Integer> g2 = new HashMap<>();
            req.setNotesDeuxiemeGroupe(g2);

            com.officedubac.project.modules.Lprime1.dto.RelevNoteLPrime1SaisieRequest.EpreuveOraleControleSaisie ctrl = new com.officedubac.project.modules.Lprime1.dto.RelevNoteLPrime1SaisieRequest.EpreuveOraleControleSaisie();
            ctrl.setMatiereChoisie("MATIERE DE CONTROLE");
            ctrl.setCoefficient(2);
            ctrl.setRappelPointsObtenus1erGroupe(20);
            ctrl.setNouvelleNoteSur20(12);
            req.setEpreuvesOralesControle(List.of(ctrl));

            com.officedubac.project.modules.Lprime1.dto.RelevNoteLPrime1SaisieRequest.EpreuveFacultativeSaisie fac = new com.officedubac.project.modules.Lprime1.dto.RelevNoteLPrime1SaisieRequest.EpreuveFacultativeSaisie();
            fac.setType("LANGUE");
            fac.setNote(14);
            req.setEpreuvesFacultatives(List.of(fac));

            com.officedubac.project.modules.Lprime1.dto.RelevNoteLPrime1SaisieRequest.EducationPhysiqueSaisie ep = new com.officedubac.project.modules.Lprime1.dto.RelevNoteLPrime1SaisieRequest.EducationPhysiqueSaisie();
            ep.setNote(13);
            req.setEducationPhysique(ep);

            req.setLieuDelivrance("DAKAR");
            req.setDateDelivrance(LocalDate.of(2026, 7, 20));
            req.setPresidentJury("Professeur TEST");

            com.officedubac.project.modules.Lprime1.model.RelevNoteLPrime1 releve = Lprime1Service.creer(req);
            afficher("Lprime1", "/api/v1/releves-lprime1", releve.getId());
        }

        {
            com.officedubac.project.modules.L1A.dto.RelevNoteL1ASaisieRequest req = new com.officedubac.project.modules.L1A.dto.RelevNoteL1ASaisieRequest();
            req.setSession(com.officedubac.project.modules.L1A.model.Enums.TypeSession.NORMALE);
            req.setJuryNumero("21");
            req.setAnnee(2026);
            req.setNomPrenom("CANDIDAT TEST 11");
            req.setDateNaissance(LocalDate.of(2005, 1, 15));
            req.setLieuNaissance("DAKAR");
            req.setEtablissement("LYCEE TEST");
            req.setIndicatif("LT");
            req.setOptions("L1A");
            req.setNumeroTable("100011");
            req.setNationalite("SEN");
            req.setNombreDeFois("1");
        Map<String, Integer> g1 = new HashMap<>();
        g1.put("FRANCAIS", 12);
        g1.put("PHILO", 14);
        g1.put("HIST_GEO", 9);
        g1.put("LV1", 16);
        g1.put("MATH", 11);
        g1.put("GREC", 13);
        g1.put("LATIN_ARABE", 15);
            req.setNotesPremierGroupe(g1);
            Map<String, Integer> g2 = new HashMap<>();
            req.setNotesDeuxiemeGroupe(g2);

            com.officedubac.project.modules.L1A.dto.RelevNoteL1ASaisieRequest.EpreuveOraleControleSaisie ctrl = new com.officedubac.project.modules.L1A.dto.RelevNoteL1ASaisieRequest.EpreuveOraleControleSaisie();
            ctrl.setMatiereChoisie("MATIERE DE CONTROLE");
            ctrl.setCoefficient(2);
            ctrl.setRappelPointsObtenus1erGroupe(20);
            ctrl.setNouvelleNoteSur20(12);
            req.setEpreuvesOralesControle(List.of(ctrl));

            com.officedubac.project.modules.L1A.dto.RelevNoteL1ASaisieRequest.EpreuveFacultativeSaisie fac = new com.officedubac.project.modules.L1A.dto.RelevNoteL1ASaisieRequest.EpreuveFacultativeSaisie();
            fac.setType("LANGUE");
            fac.setNote(14);
            req.setEpreuvesFacultatives(List.of(fac));

            com.officedubac.project.modules.L1A.dto.RelevNoteL1ASaisieRequest.EducationPhysiqueSaisie ep = new com.officedubac.project.modules.L1A.dto.RelevNoteL1ASaisieRequest.EducationPhysiqueSaisie();
            ep.setNote(13);
            req.setEducationPhysique(ep);

            req.setLieuDelivrance("DAKAR");
            req.setDateDelivrance(LocalDate.of(2026, 7, 20));
            req.setPresidentJury("Professeur TEST");

            com.officedubac.project.modules.L1A.model.RelevNoteL1A releve = L1AService.creer(req);
            afficher("L1A", "/api/v1/releves-l1a", releve.getId());
        }

        {
            com.officedubac.project.modules.L1B.dto.RelevNoteL1BSaisieRequest req = new com.officedubac.project.modules.L1B.dto.RelevNoteL1BSaisieRequest();
            req.setSession(com.officedubac.project.modules.L1B.model.Enums.TypeSession.NORMALE);
            req.setJuryNumero("22");
            req.setAnnee(2026);
            req.setNomPrenom("CANDIDAT TEST 12");
            req.setDateNaissance(LocalDate.of(2005, 1, 15));
            req.setLieuNaissance("DAKAR");
            req.setEtablissement("LYCEE TEST");
            req.setIndicatif("LT");
            req.setOptions("L1B");
            req.setNumeroTable("100012");
            req.setNationalite("SEN");
            req.setNombreDeFois("1");
        Map<String, Integer> g1 = new HashMap<>();
        g1.put("FRANCAIS", 12);
        g1.put("PHILO", 14);
        g1.put("HIST_GEO", 9);
        g1.put("LV1", 16);
        g1.put("MATH", 11);
        g1.put("LV2", 13);
        g1.put("LATIN_ARABE", 15);
            req.setNotesPremierGroupe(g1);
            Map<String, Integer> g2 = new HashMap<>();
            req.setNotesDeuxiemeGroupe(g2);

            com.officedubac.project.modules.L1B.dto.RelevNoteL1BSaisieRequest.EpreuveOraleControleSaisie ctrl = new com.officedubac.project.modules.L1B.dto.RelevNoteL1BSaisieRequest.EpreuveOraleControleSaisie();
            ctrl.setMatiereChoisie("MATIERE DE CONTROLE");
            ctrl.setCoefficient(2);
            ctrl.setRappelPointsObtenus1erGroupe(20);
            ctrl.setNouvelleNoteSur20(12);
            req.setEpreuvesOralesControle(List.of(ctrl));

            com.officedubac.project.modules.L1B.dto.RelevNoteL1BSaisieRequest.EpreuveFacultativeSaisie fac = new com.officedubac.project.modules.L1B.dto.RelevNoteL1BSaisieRequest.EpreuveFacultativeSaisie();
            fac.setType("LANGUE");
            fac.setNote(14);
            req.setEpreuvesFacultatives(List.of(fac));

            com.officedubac.project.modules.L1B.dto.RelevNoteL1BSaisieRequest.EducationPhysiqueSaisie ep = new com.officedubac.project.modules.L1B.dto.RelevNoteL1BSaisieRequest.EducationPhysiqueSaisie();
            ep.setNote(13);
            req.setEducationPhysique(ep);

            req.setLieuDelivrance("DAKAR");
            req.setDateDelivrance(LocalDate.of(2026, 7, 20));
            req.setPresidentJury("Professeur TEST");

            com.officedubac.project.modules.L1B.model.RelevNoteL1B releve = L1BService.creer(req);
            afficher("L1B", "/api/v1/releves-l1b", releve.getId());
        }

        {
            com.officedubac.project.modules.L2.dto.RelevNoteL2SaisieRequest req = new com.officedubac.project.modules.L2.dto.RelevNoteL2SaisieRequest();
            req.setSession(com.officedubac.project.modules.L2.model.Enums.TypeSession.NORMALE);
            req.setJuryNumero("23");
            req.setAnnee(2026);
            req.setNomPrenom("CANDIDAT TEST 13");
            req.setDateNaissance(LocalDate.of(2005, 1, 15));
            req.setLieuNaissance("DAKAR");
            req.setEtablissement("LYCEE TEST");
            req.setIndicatif("LT");
            req.setOptions("L2");
            req.setNumeroTable("100013");
            req.setNationalite("SEN");
            req.setNombreDeFois("1");
        Map<String, Integer> g1 = new HashMap<>();
        g1.put("FRANCAIS", 12);
        g1.put("PHILO", 14);
        g1.put("HIST_GEO", 9);
        g1.put("MATH", 16);
        g1.put("LV1", 11);
        g1.put("LV2_OU_ECO", 13);
        g1.put("SC_NATURE", 15);
            req.setNotesPremierGroupe(g1);
            Map<String, Integer> g2 = new HashMap<>();
            req.setNotesDeuxiemeGroupe(g2);

            com.officedubac.project.modules.L2.dto.RelevNoteL2SaisieRequest.EpreuveOraleControleSaisie ctrl = new com.officedubac.project.modules.L2.dto.RelevNoteL2SaisieRequest.EpreuveOraleControleSaisie();
            ctrl.setMatiereChoisie("MATIERE DE CONTROLE");
            ctrl.setCoefficient(2);
            ctrl.setRappelPointsObtenus1erGroupe(20);
            ctrl.setNouvelleNoteSur20(12);
            req.setEpreuvesOralesControle(List.of(ctrl));

            com.officedubac.project.modules.L2.dto.RelevNoteL2SaisieRequest.EpreuveFacultativeSaisie fac = new com.officedubac.project.modules.L2.dto.RelevNoteL2SaisieRequest.EpreuveFacultativeSaisie();
            fac.setType("LANGUE");
            fac.setNote(14);
            req.setEpreuvesFacultatives(List.of(fac));

            com.officedubac.project.modules.L2.dto.RelevNoteL2SaisieRequest.EducationPhysiqueSaisie ep = new com.officedubac.project.modules.L2.dto.RelevNoteL2SaisieRequest.EducationPhysiqueSaisie();
            ep.setNote(13);
            req.setEducationPhysique(ep);

            req.setLieuDelivrance("DAKAR");
            req.setDateDelivrance(LocalDate.of(2026, 7, 20));
            req.setPresidentJury("Professeur TEST");

            com.officedubac.project.modules.L2.model.RelevNoteL2 releve = L2Service.creer(req);
            afficher("L2", "/api/v1/releves-l2", releve.getId());
        }

        {
            com.officedubac.project.modules.S1.dto.RelevNoteS1SaisieRequest req = new com.officedubac.project.modules.S1.dto.RelevNoteS1SaisieRequest();
            req.setSession(com.officedubac.project.modules.S1.model.Enums.TypeSession.NORMALE);
            req.setJuryNumero("24");
            req.setAnnee(2026);
            req.setNomPrenom("CANDIDAT TEST 14");
            req.setDateNaissance(LocalDate.of(2005, 1, 15));
            req.setLieuNaissance("DAKAR");
            req.setEtablissement("LYCEE TEST");
            req.setIndicatif("LT");
            req.setOptions("S1");
            req.setNumeroTable("100014");
            req.setNationalite("SEN");
            req.setNombreDeFois("1");
        Map<String, Integer> g1 = new HashMap<>();
        g1.put("FRANCAIS", 12);
        g1.put("PHILO", 14);
        g1.put("MATH", 9);
        g1.put("SC_PHYS", 16);
        g1.put("HIST_GEO", 11);
        g1.put("SC_NAT", 13);
        g1.put("ANGLAIS", 15);
            req.setNotesPremierGroupe(g1);
            Map<String, Integer> g2 = new HashMap<>();
            req.setNotesDeuxiemeGroupe(g2);

            com.officedubac.project.modules.S1.dto.RelevNoteS1SaisieRequest.EpreuveOraleControleSaisie ctrl = new com.officedubac.project.modules.S1.dto.RelevNoteS1SaisieRequest.EpreuveOraleControleSaisie();
            ctrl.setMatiereChoisie("MATIERE DE CONTROLE");
            ctrl.setCoefficient(2);
            ctrl.setRappelPointsObtenus1erGroupe(20);
            ctrl.setNouvelleNoteSur20(12);
            req.setEpreuvesOralesControle(List.of(ctrl));

            com.officedubac.project.modules.S1.dto.RelevNoteS1SaisieRequest.EpreuveFacultativeSaisie fac = new com.officedubac.project.modules.S1.dto.RelevNoteS1SaisieRequest.EpreuveFacultativeSaisie();
            fac.setType("LANGUE");
            fac.setNote(14);
            req.setEpreuvesFacultatives(List.of(fac));

            com.officedubac.project.modules.S1.dto.RelevNoteS1SaisieRequest.EducationPhysiqueSaisie ep = new com.officedubac.project.modules.S1.dto.RelevNoteS1SaisieRequest.EducationPhysiqueSaisie();
            ep.setNote(13);
            req.setEducationPhysique(ep);

            req.setLieuDelivrance("DAKAR");
            req.setDateDelivrance(LocalDate.of(2026, 7, 20));
            req.setPresidentJury("Professeur TEST");

            com.officedubac.project.modules.S1.model.RelevNoteS1 releve = S1Service.creer(req);
            afficher("S1", "/api/v1/releves-s1", releve.getId());
        }

        {
            com.officedubac.project.modules.S2.dto.RelevNoteS2SaisieRequest req = new com.officedubac.project.modules.S2.dto.RelevNoteS2SaisieRequest();
            req.setSession(com.officedubac.project.modules.S2.model.Enums.TypeSession.NORMALE);
            req.setJuryNumero("25");
            req.setAnnee(2026);
            req.setNomPrenom("CANDIDAT TEST 15");
            req.setDateNaissance(LocalDate.of(2005, 1, 15));
            req.setLieuNaissance("DAKAR");
            req.setEtablissement("LYCEE TEST");
            req.setIndicatif("LT");
            req.setOptions("S2");
            req.setNumeroTable("100015");
            req.setNationalite("SEN");
            req.setNombreDeFois("1");
        Map<String, Integer> g1 = new HashMap<>();
        g1.put("FRANCAIS", 12);
        g1.put("PHILO", 14);
        g1.put("MATH", 9);
        g1.put("SC_PHYS", 16);
        g1.put("SC_NAT", 11);
        g1.put("HIST_GEO", 13);
        g1.put("ANGLAIS", 15);
            req.setNotesPremierGroupe(g1);
            Map<String, Integer> g2 = new HashMap<>();
            req.setNotesDeuxiemeGroupe(g2);

            com.officedubac.project.modules.S2.dto.RelevNoteS2SaisieRequest.EpreuveOraleControleSaisie ctrl = new com.officedubac.project.modules.S2.dto.RelevNoteS2SaisieRequest.EpreuveOraleControleSaisie();
            ctrl.setMatiereChoisie("MATIERE DE CONTROLE");
            ctrl.setCoefficient(2);
            ctrl.setRappelPointsObtenus1erGroupe(20);
            ctrl.setNouvelleNoteSur20(12);
            req.setEpreuvesOralesControle(List.of(ctrl));

            com.officedubac.project.modules.S2.dto.RelevNoteS2SaisieRequest.EpreuveFacultativeSaisie fac = new com.officedubac.project.modules.S2.dto.RelevNoteS2SaisieRequest.EpreuveFacultativeSaisie();
            fac.setType("LANGUE");
            fac.setNote(14);
            req.setEpreuvesFacultatives(List.of(fac));

            com.officedubac.project.modules.S2.dto.RelevNoteS2SaisieRequest.EducationPhysiqueSaisie ep = new com.officedubac.project.modules.S2.dto.RelevNoteS2SaisieRequest.EducationPhysiqueSaisie();
            ep.setNote(13);
            req.setEducationPhysique(ep);

            req.setLieuDelivrance("DAKAR");
            req.setDateDelivrance(LocalDate.of(2026, 7, 20));
            req.setPresidentJury("Professeur TEST");

            com.officedubac.project.modules.S2.model.RelevNoteS2 releve = S2Service.creer(req);
            afficher("S2", "/api/v1/releves-s2", releve.getId());
        }

        {
            com.officedubac.project.modules.S3.dto.RelevNoteS3SaisieRequest req = new com.officedubac.project.modules.S3.dto.RelevNoteS3SaisieRequest();
            req.setSession(com.officedubac.project.modules.S3.model.Enums.TypeSession.NORMALE);
            req.setJuryNumero("26");
            req.setAnnee(2026);
            req.setNomPrenom("CANDIDAT TEST 16");
            req.setDateNaissance(LocalDate.of(2005, 1, 15));
            req.setLieuNaissance("DAKAR");
            req.setEtablissement("LYCEE TEST");
            req.setIndicatif("LT");
            req.setOptions("S3");
            req.setNumeroTable("100016");
            req.setNationalite("SEN");
            req.setNombreDeFois("1");
        Map<String, Integer> g1 = new HashMap<>();
        g1.put("FRANCAIS", 12);
        g1.put("PHILO", 14);
        g1.put("MATH", 9);
        g1.put("SC_PHYS", 16);
        g1.put("CONST_MECA", 11);
        g1.put("ANAL_FAB_AUTO", 13);
        g1.put("ANGLAIS", 15);
        g1.put("EPR_PRATIQUE", 10);
            req.setNotesPremierGroupe(g1);
            Map<String, Integer> g2 = new HashMap<>();
            req.setNotesDeuxiemeGroupe(g2);

            com.officedubac.project.modules.S3.dto.RelevNoteS3SaisieRequest.EpreuveOraleControleSaisie ctrl = new com.officedubac.project.modules.S3.dto.RelevNoteS3SaisieRequest.EpreuveOraleControleSaisie();
            ctrl.setMatiereChoisie("MATIERE DE CONTROLE");
            ctrl.setCoefficient(2);
            ctrl.setRappelPointsObtenus1erGroupe(20);
            ctrl.setNouvelleNoteSur20(12);
            req.setEpreuvesOralesControle(List.of(ctrl));

            com.officedubac.project.modules.S3.dto.RelevNoteS3SaisieRequest.EpreuveFacultativeSaisie fac = new com.officedubac.project.modules.S3.dto.RelevNoteS3SaisieRequest.EpreuveFacultativeSaisie();
            fac.setType("LANGUE");
            fac.setNote(14);
            req.setEpreuvesFacultatives(List.of(fac));

            com.officedubac.project.modules.S3.dto.RelevNoteS3SaisieRequest.EducationPhysiqueSaisie ep = new com.officedubac.project.modules.S3.dto.RelevNoteS3SaisieRequest.EducationPhysiqueSaisie();
            ep.setNote(13);
            req.setEducationPhysique(ep);

            req.setLieuDelivrance("DAKAR");
            req.setDateDelivrance(LocalDate.of(2026, 7, 20));
            req.setPresidentJury("Professeur TEST");

            com.officedubac.project.modules.S3.model.RelevNoteS3 releve = S3Service.creer(req);
            afficher("S3", "/api/v1/releves-s3", releve.getId());
        }

        {
            com.officedubac.project.modules.S4.dto.RelevNoteS4SaisieRequest req = new com.officedubac.project.modules.S4.dto.RelevNoteS4SaisieRequest();
            req.setSession(com.officedubac.project.modules.S4.model.Enums.TypeSession.NORMALE);
            req.setJuryNumero("27");
            req.setAnnee(2026);
            req.setNomPrenom("CANDIDAT TEST 17");
            req.setDateNaissance(LocalDate.of(2005, 1, 15));
            req.setLieuNaissance("DAKAR");
            req.setEtablissement("LYCEE TEST");
            req.setIndicatif("LT");
            req.setOptions("S4");
            req.setNumeroTable("100017");
            req.setNationalite("SEN");
            req.setNombreDeFois("1");
        Map<String, Integer> g1 = new HashMap<>();
        g1.put("PHILO", 12);
        g1.put("MATH", 14);
        g1.put("SC_PHYS", 9);
        g1.put("SVT", 16);
        g1.put("FRANCAIS", 11);
        g1.put("HIST_GEO", 13);
        g1.put("ANGLAIS", 15);
        g1.put("ECOLOGIE", 10);
        g1.put("ZOOTECHNIQUE", 17);
        g1.put("PHYTOTECHNIQUE", 8);
            req.setNotesPremierGroupe(g1);
            Map<String, Integer> g2 = new HashMap<>();
            req.setNotesDeuxiemeGroupe(g2);

            com.officedubac.project.modules.S4.dto.RelevNoteS4SaisieRequest.EpreuveOraleControleSaisie ctrl = new com.officedubac.project.modules.S4.dto.RelevNoteS4SaisieRequest.EpreuveOraleControleSaisie();
            ctrl.setMatiereChoisie("MATIERE DE CONTROLE");
            ctrl.setCoefficient(2);
            ctrl.setRappelPointsObtenus1erGroupe(20);
            ctrl.setNouvelleNoteSur20(12);
            req.setEpreuvesOralesControle(List.of(ctrl));

            com.officedubac.project.modules.S4.dto.RelevNoteS4SaisieRequest.EpreuveFacultativeSaisie fac = new com.officedubac.project.modules.S4.dto.RelevNoteS4SaisieRequest.EpreuveFacultativeSaisie();
            fac.setType("LANGUE");
            fac.setNote(14);
            req.setEpreuvesFacultatives(List.of(fac));

            com.officedubac.project.modules.S4.dto.RelevNoteS4SaisieRequest.EducationPhysiqueSaisie ep = new com.officedubac.project.modules.S4.dto.RelevNoteS4SaisieRequest.EducationPhysiqueSaisie();
            ep.setNote(13);
            req.setEducationPhysique(ep);

            req.setLieuDelivrance("DAKAR");
            req.setDateDelivrance(LocalDate.of(2026, 7, 20));
            req.setPresidentJury("Professeur TEST");

            com.officedubac.project.modules.S4.model.RelevNoteS4 releve = S4Service.creer(req);
            afficher("S4", "/api/v1/releves-s4", releve.getId());
        }

        {
            com.officedubac.project.modules.S5.dto.RelevNoteS5SaisieRequest req = new com.officedubac.project.modules.S5.dto.RelevNoteS5SaisieRequest();
            req.setSession(com.officedubac.project.modules.S5.model.Enums.TypeSession.NORMALE);
            req.setJuryNumero("28");
            req.setAnnee(2026);
            req.setNomPrenom("CANDIDAT TEST 18");
            req.setDateNaissance(LocalDate.of(2005, 1, 15));
            req.setLieuNaissance("DAKAR");
            req.setEtablissement("LYCEE TEST");
            req.setIndicatif("LT");
            req.setOptions("S5");
            req.setNumeroTable("100018");
            req.setNationalite("SEN");
            req.setNombreDeFois("1");
        Map<String, Integer> g1 = new HashMap<>();
        g1.put("PHILO", 12);
        g1.put("MATH", 14);
        g1.put("SC_PHYS", 9);
        g1.put("SVT", 16);
        g1.put("FRANCAIS", 11);
        g1.put("HIST_GEO", 13);
        g1.put("ANGLAIS", 15);
        g1.put("TECH_TRANSF", 10);
        g1.put("MICROBIOLOGIE", 17);
        g1.put("BIOCHIMIE", 8);
            req.setNotesPremierGroupe(g1);
            Map<String, Integer> g2 = new HashMap<>();
            req.setNotesDeuxiemeGroupe(g2);

            com.officedubac.project.modules.S5.dto.RelevNoteS5SaisieRequest.EpreuveOraleControleSaisie ctrl = new com.officedubac.project.modules.S5.dto.RelevNoteS5SaisieRequest.EpreuveOraleControleSaisie();
            ctrl.setMatiereChoisie("MATIERE DE CONTROLE");
            ctrl.setCoefficient(2);
            ctrl.setRappelPointsObtenus1erGroupe(20);
            ctrl.setNouvelleNoteSur20(12);
            req.setEpreuvesOralesControle(List.of(ctrl));

            com.officedubac.project.modules.S5.dto.RelevNoteS5SaisieRequest.EpreuveFacultativeSaisie fac = new com.officedubac.project.modules.S5.dto.RelevNoteS5SaisieRequest.EpreuveFacultativeSaisie();
            fac.setType("LANGUE");
            fac.setNote(14);
            req.setEpreuvesFacultatives(List.of(fac));

            com.officedubac.project.modules.S5.dto.RelevNoteS5SaisieRequest.EducationPhysiqueSaisie ep = new com.officedubac.project.modules.S5.dto.RelevNoteS5SaisieRequest.EducationPhysiqueSaisie();
            ep.setNote(13);
            req.setEducationPhysique(ep);

            req.setLieuDelivrance("DAKAR");
            req.setDateDelivrance(LocalDate.of(2026, 7, 20));
            req.setPresidentJury("Professeur TEST");

            com.officedubac.project.modules.S5.model.RelevNoteS5 releve = S5Service.creer(req);
            afficher("S5", "/api/v1/releves-s5", releve.getId());
        }

        {
            com.officedubac.project.modules.T1.dto.RelevNoteT1SaisieRequest req = new com.officedubac.project.modules.T1.dto.RelevNoteT1SaisieRequest();
            req.setSession(com.officedubac.project.modules.T1.model.Enums.TypeSession.NORMALE);
            req.setJuryNumero("29");
            req.setAnnee(2026);
            req.setNomPrenom("CANDIDAT TEST 19");
            req.setDateNaissance(LocalDate.of(2005, 1, 15));
            req.setLieuNaissance("DAKAR");
            req.setEtablissement("LYCEE TEST");
            req.setIndicatif("LT");
            req.setOptions("T1");
            req.setNumeroTable("100019");
            req.setNationalite("SEN");
            req.setNombreDeFois("1");
        Map<String, Integer> g1 = new HashMap<>();
        g1.put("TECH_EXPR", 12);
        g1.put("MATH", 14);
        g1.put("MECANIQUE", 9);
        g1.put("CONST_MECA", 16);
        g1.put("ANAL_FAB_OUTIL", 11);
        g1.put("ELECTRICITE", 13);
        g1.put("METALLURGIE", 15);
        g1.put("SC_PHYS", 10);
        g1.put("ANGLAIS", 17);
        g1.put("TECHNO_AUTOM", 8);
        g1.put("EPR_PRATIQUE", 12);
            req.setNotesPremierGroupe(g1);
            Map<String, Integer> g2 = new HashMap<>();
            req.setNotesDeuxiemeGroupe(g2);

            com.officedubac.project.modules.T1.dto.RelevNoteT1SaisieRequest.EpreuveOraleControleSaisie ctrl = new com.officedubac.project.modules.T1.dto.RelevNoteT1SaisieRequest.EpreuveOraleControleSaisie();
            ctrl.setMatiereChoisie("MATIERE DE CONTROLE");
            ctrl.setCoefficient(2);
            ctrl.setRappelPointsObtenus1erGroupe(20);
            ctrl.setNouvelleNoteSur20(12);
            req.setEpreuvesOralesControle(List.of(ctrl));

            com.officedubac.project.modules.T1.dto.RelevNoteT1SaisieRequest.EpreuveFacultativeSaisie fac = new com.officedubac.project.modules.T1.dto.RelevNoteT1SaisieRequest.EpreuveFacultativeSaisie();
            fac.setType("LANGUE");
            fac.setNote(14);
            req.setEpreuvesFacultatives(List.of(fac));

            com.officedubac.project.modules.T1.dto.RelevNoteT1SaisieRequest.EducationPhysiqueSaisie ep = new com.officedubac.project.modules.T1.dto.RelevNoteT1SaisieRequest.EducationPhysiqueSaisie();
            ep.setNote(13);
            req.setEducationPhysique(ep);

            req.setLieuDelivrance("DAKAR");
            req.setDateDelivrance(LocalDate.of(2026, 7, 20));
            req.setPresidentJury("Professeur TEST");

            com.officedubac.project.modules.T1.model.RelevNoteT1 releve = T1Service.creer(req);
            afficher("T1", "/api/v1/releves-t1", releve.getId());
        }

        {
            com.officedubac.project.modules.T2.dto.RelevNoteT2SaisieRequest req = new com.officedubac.project.modules.T2.dto.RelevNoteT2SaisieRequest();
            req.setSession(com.officedubac.project.modules.T2.model.Enums.TypeSession.NORMALE);
            req.setJuryNumero("30");
            req.setAnnee(2026);
            req.setNomPrenom("CANDIDAT TEST 20");
            req.setDateNaissance(LocalDate.of(2005, 1, 15));
            req.setLieuNaissance("DAKAR");
            req.setEtablissement("LYCEE TEST");
            req.setIndicatif("LT");
            req.setOptions("T2");
            req.setNumeroTable("100020");
            req.setNationalite("SEN");
            req.setNombreDeFois("1");
        Map<String, Integer> g1 = new HashMap<>();
        g1.put("TECH_EXPR", 12);
        g1.put("MATH", 14);
        g1.put("ELECTROTECH", 9);
        g1.put("CONST_ELECTROMECA", 16);
        g1.put("SCHEMA_AUTOM", 11);
        g1.put("ANALYSE_SYST", 13);
        g1.put("SC_PHYS", 15);
        g1.put("ANGLAIS", 10);
        g1.put("CONST_ELEC", 17);
        g1.put("ESSAIS_MESURES", 8);
            req.setNotesPremierGroupe(g1);
            Map<String, Integer> g2 = new HashMap<>();
            req.setNotesDeuxiemeGroupe(g2);

            com.officedubac.project.modules.T2.dto.RelevNoteT2SaisieRequest.EpreuveOraleControleSaisie ctrl = new com.officedubac.project.modules.T2.dto.RelevNoteT2SaisieRequest.EpreuveOraleControleSaisie();
            ctrl.setMatiereChoisie("MATIERE DE CONTROLE");
            ctrl.setCoefficient(2);
            ctrl.setRappelPointsObtenus1erGroupe(20);
            ctrl.setNouvelleNoteSur20(12);
            req.setEpreuvesOralesControle(List.of(ctrl));

            com.officedubac.project.modules.T2.dto.RelevNoteT2SaisieRequest.EpreuveFacultativeSaisie fac = new com.officedubac.project.modules.T2.dto.RelevNoteT2SaisieRequest.EpreuveFacultativeSaisie();
            fac.setType("LANGUE");
            fac.setNote(14);
            req.setEpreuvesFacultatives(List.of(fac));

            com.officedubac.project.modules.T2.dto.RelevNoteT2SaisieRequest.EducationPhysiqueSaisie ep = new com.officedubac.project.modules.T2.dto.RelevNoteT2SaisieRequest.EducationPhysiqueSaisie();
            ep.setNote(13);
            req.setEducationPhysique(ep);

            req.setLieuDelivrance("DAKAR");
            req.setDateDelivrance(LocalDate.of(2026, 7, 20));
            req.setPresidentJury("Professeur TEST");

            com.officedubac.project.modules.T2.model.RelevNoteT2 releve = T2Service.creer(req);
            afficher("T2", "/api/v1/releves-t2", releve.getId());
        }

        {
            com.officedubac.project.modules.A4.dto.RelevNoteA4SaisieRequest req = new com.officedubac.project.modules.A4.dto.RelevNoteA4SaisieRequest();
            req.setSession(com.officedubac.project.modules.A4.model.Enums.TypeSession.NORMALE);
            req.setJuryNumero("31");
            req.setAnnee(2026);
            req.setNomPrenom("CANDIDAT TEST 21");
            req.setDateNaissance(LocalDate.of(2005, 1, 15));
            req.setLieuNaissance("DAKAR");
            req.setEtablissement("LYCEE TEST");
            req.setIndicatif("LT");
            req.setOptions("A4");
            req.setNumeroTable("100021");
            req.setNationalite("SEN");
            req.setNombreDeFois("1");
        Map<String, Integer> g1 = new HashMap<>();
        g1.put("FR_ECRIT", 12);
        g1.put("FR_ORAL", 14);
        g1.put("PHILO", 9);
        g1.put("LV1_ECRIT", 16);
        g1.put("HIST_GEO", 11);
        g1.put("MATH", 13);
            req.setNotesPremierGroupe(g1);
        Map<String, Integer> g2 = new HashMap<>();
        g2.put("LANGUE_VIVANTE", 12);
            req.setNotesDeuxiemeGroupe(g2);

            com.officedubac.project.modules.A4.dto.RelevNoteA4SaisieRequest.EpreuveOraleControleSaisie ctrl = new com.officedubac.project.modules.A4.dto.RelevNoteA4SaisieRequest.EpreuveOraleControleSaisie();
            ctrl.setMatiereChoisie("MATIERE DE CONTROLE");
            ctrl.setCoefficient(2);
            ctrl.setRappelPointsObtenus1erGroupe(20);
            ctrl.setNouvelleNoteSur20(12);
            req.setEpreuvesOralesControle(List.of(ctrl));

            com.officedubac.project.modules.A4.dto.RelevNoteA4SaisieRequest.EpreuveFacultativeSaisie fac = new com.officedubac.project.modules.A4.dto.RelevNoteA4SaisieRequest.EpreuveFacultativeSaisie();
            fac.setType("LANGUE");
            fac.setNote(14);
            req.setEpreuvesFacultatives(List.of(fac));

            com.officedubac.project.modules.A4.dto.RelevNoteA4SaisieRequest.EducationPhysiqueSaisie ep = new com.officedubac.project.modules.A4.dto.RelevNoteA4SaisieRequest.EducationPhysiqueSaisie();
            ep.setNote(13);
            req.setEducationPhysique(ep);

            req.setLieuDelivrance("DAKAR");
            req.setDateDelivrance(LocalDate.of(2026, 7, 20));
            req.setPresidentJury("Professeur TEST");

            com.officedubac.project.modules.A4.model.RelevNoteA4 releve = A4Service.creer(req);
            afficher("A4", "/api/v1/releves-a4", releve.getId());
        }

        {
            com.officedubac.project.modules.A1.dto.RelevNoteA1SaisieRequest req = new com.officedubac.project.modules.A1.dto.RelevNoteA1SaisieRequest();
            req.setSession(com.officedubac.project.modules.A1.model.Enums.TypeSession.NORMALE);
            req.setJuryNumero("32");
            req.setAnnee(2026);
            req.setNomPrenom("CANDIDAT TEST 22");
            req.setDateNaissance(LocalDate.of(2005, 1, 15));
            req.setLieuNaissance("DAKAR");
            req.setEtablissement("LYCEE TEST");
            req.setIndicatif("LT");
            req.setOptions("A1");
            req.setNumeroTable("100022");
            req.setNationalite("SEN");
            req.setNombreDeFois("1");
        Map<String, Integer> g1 = new HashMap<>();
        g1.put("FR_ECRIT", 12);
        g1.put("FR_ORAL", 14);
        g1.put("PHILO", 9);
        g1.put("LAT_GREC1", 16);
        g1.put("HIST_GEO", 11);
        g1.put("LV", 13);
            req.setNotesPremierGroupe(g1);
        Map<String, Integer> g2 = new HashMap<>();
        g2.put("LAT_GREC2", 12);
        g2.put("MATH", 14);
            req.setNotesDeuxiemeGroupe(g2);

            com.officedubac.project.modules.A1.dto.RelevNoteA1SaisieRequest.EpreuveOraleControleSaisie ctrl = new com.officedubac.project.modules.A1.dto.RelevNoteA1SaisieRequest.EpreuveOraleControleSaisie();
            ctrl.setMatiereChoisie("MATIERE DE CONTROLE");
            ctrl.setCoefficient(2);
            ctrl.setRappelPointsObtenus1erGroupe(20);
            ctrl.setNouvelleNoteSur20(12);
            req.setEpreuvesOralesControle(List.of(ctrl));

            com.officedubac.project.modules.A1.dto.RelevNoteA1SaisieRequest.EpreuveFacultativeSaisie fac = new com.officedubac.project.modules.A1.dto.RelevNoteA1SaisieRequest.EpreuveFacultativeSaisie();
            fac.setType("LANGUE");
            fac.setNote(14);
            req.setEpreuvesFacultatives(List.of(fac));

            com.officedubac.project.modules.A1.dto.RelevNoteA1SaisieRequest.EducationPhysiqueSaisie ep = new com.officedubac.project.modules.A1.dto.RelevNoteA1SaisieRequest.EducationPhysiqueSaisie();
            ep.setNote(13);
            req.setEducationPhysique(ep);

            req.setLieuDelivrance("DAKAR");
            req.setDateDelivrance(LocalDate.of(2026, 7, 20));
            req.setPresidentJury("Professeur TEST");

            com.officedubac.project.modules.A1.model.RelevNoteA1 releve = A1Service.creer(req);
            afficher("A1", "/api/v1/releves-a1", releve.getId());
        }

        {
            com.officedubac.project.modules.A2.dto.RelevNoteA2SaisieRequest req = new com.officedubac.project.modules.A2.dto.RelevNoteA2SaisieRequest();
            req.setSession(com.officedubac.project.modules.A2.model.Enums.TypeSession.NORMALE);
            req.setJuryNumero("33");
            req.setAnnee(2026);
            req.setNomPrenom("CANDIDAT TEST 23");
            req.setDateNaissance(LocalDate.of(2005, 1, 15));
            req.setLieuNaissance("DAKAR");
            req.setEtablissement("LYCEE TEST");
            req.setIndicatif("LT");
            req.setOptions("A2");
            req.setNumeroTable("100023");
            req.setNationalite("SEN");
            req.setNombreDeFois("1");
        Map<String, Integer> g1 = new HashMap<>();
        g1.put("FR_ECRIT", 12);
        g1.put("FR_ORAL", 14);
        g1.put("PHILO", 9);
        g1.put("LAT_GREC1", 16);
        g1.put("HIST_GEO", 11);
        g1.put("LV", 13);
            req.setNotesPremierGroupe(g1);
        Map<String, Integer> g2 = new HashMap<>();
        g2.put("LAT_GREC2", 12);
        g2.put("MATH", 14);
            req.setNotesDeuxiemeGroupe(g2);

            com.officedubac.project.modules.A2.dto.RelevNoteA2SaisieRequest.EpreuveOraleControleSaisie ctrl = new com.officedubac.project.modules.A2.dto.RelevNoteA2SaisieRequest.EpreuveOraleControleSaisie();
            ctrl.setMatiereChoisie("MATIERE DE CONTROLE");
            ctrl.setCoefficient(2);
            ctrl.setRappelPointsObtenus1erGroupe(20);
            ctrl.setNouvelleNoteSur20(12);
            req.setEpreuvesOralesControle(List.of(ctrl));

            com.officedubac.project.modules.A2.dto.RelevNoteA2SaisieRequest.EpreuveFacultativeSaisie fac = new com.officedubac.project.modules.A2.dto.RelevNoteA2SaisieRequest.EpreuveFacultativeSaisie();
            fac.setType("LANGUE");
            fac.setNote(14);
            req.setEpreuvesFacultatives(List.of(fac));

            com.officedubac.project.modules.A2.dto.RelevNoteA2SaisieRequest.EducationPhysiqueSaisie ep = new com.officedubac.project.modules.A2.dto.RelevNoteA2SaisieRequest.EducationPhysiqueSaisie();
            ep.setNote(13);
            req.setEducationPhysique(ep);

            req.setLieuDelivrance("DAKAR");
            req.setDateDelivrance(LocalDate.of(2026, 7, 20));
            req.setPresidentJury("Professeur TEST");

            com.officedubac.project.modules.A2.model.RelevNoteA2 releve = A2Service.creer(req);
            afficher("A2", "/api/v1/releves-a2", releve.getId());
        }

        {
            com.officedubac.project.modules.a3deuxiemepartie.dto.ReleveA3DeuxiemePartieSaisieRequest req = new com.officedubac.project.modules.a3deuxiemepartie.dto.ReleveA3DeuxiemePartieSaisieRequest();
            req.setJuryNumero("34");
            req.setNomPrenom("CANDIDAT TEST 24");
            req.setDateNaissance(LocalDate.of(2005, 1, 15));
            req.setLieuNaissance("DAKAR");
            req.setEtablissement("LYCEE TEST");
            req.setIndicatif("LT");
            req.setOptions("a3deuxiemepartie");
            req.setNumeroTable("100024");
            req.setNationalite("SEN");
            req.setNombreDeFois("1");
        Map<String, Integer> ec = new HashMap<>();
        ec.put("PHILO", 12);
        ec.put("LV1_ECRIT", 14);
        ec.put("HIST_GEO", 9);
        ec.put("LV2", 16);
            req.setNotesEcrites(ec);
        Map<String, Integer> or_ = new HashMap<>();
        or_.put("LV1_ORAL", 12);
        or_.put("MATH", 14);
            req.setNotesOrales(or_);

            req.setLieuDelivrance("DAKAR");
            req.setDateDelivrance(LocalDate.of(2026, 7, 20));
            req.setPresidentJury("Professeur TEST");

            com.officedubac.project.modules.a3deuxiemepartie.model.ReleveA3DeuxiemePartie releve = a3deuxiemepartieService.creer(req);
            afficher("a3deuxiemepartie", "/api/v1/releves-a3-2eme-partie", releve.getId());
        }

        {
            com.officedubac.project.modules.c2emepartie.dto.ReleveC2emePartieSaisieRequest req = new com.officedubac.project.modules.c2emepartie.dto.ReleveC2emePartieSaisieRequest();
            req.setJuryNumero("35");
            req.setNomPrenom("CANDIDAT TEST 25");
            req.setDateNaissance(LocalDate.of(2005, 1, 15));
            req.setLieuNaissance("DAKAR");
            req.setEtablissement("LYCEE TEST");
            req.setIndicatif("LT");
            req.setOptions("c2emepartie");
            req.setNumeroTable("100025");
            req.setNationalite("SEN");
            req.setNombreDeFois("1");
        Map<String, Integer> ec = new HashMap<>();
        ec.put("FRANCAIS", 12);
        ec.put("MATH", 14);
        ec.put("SC_PHYS", 9);
        ec.put("SC_NAT", 16);
            req.setNotesEcrites(ec);
        Map<String, Integer> or_ = new HashMap<>();
        or_.put("LV1", 12);
        or_.put("HIST_GEO", 14);
        or_.put("MATH_ORAL", 9);
            req.setNotesOrales(or_);

            req.setLieuDelivrance("DAKAR");
            req.setDateDelivrance(LocalDate.of(2026, 7, 20));
            req.setPresidentJury("Professeur TEST");

            com.officedubac.project.modules.c2emepartie.model.ReleveC2emePartie releve = c2emepartieService.creer(req);
            afficher("c2emepartie", "/api/v1/releves-c-2eme-partie", releve.getId());
        }

        {
            com.officedubac.project.modules.ddeuxiemepartie.dto.ReleveDDeuxiemePartieSaisieRequest req = new com.officedubac.project.modules.ddeuxiemepartie.dto.ReleveDDeuxiemePartieSaisieRequest();
            req.setJuryNumero("36");
            req.setNomPrenom("CANDIDAT TEST 26");
            req.setDateNaissance(LocalDate.of(2005, 1, 15));
            req.setLieuNaissance("DAKAR");
            req.setEtablissement("LYCEE TEST");
            req.setIndicatif("LT");
            req.setOptions("ddeuxiemepartie");
            req.setNumeroTable("100026");
            req.setNationalite("SEN");
            req.setNombreDeFois("1");
        Map<String, Integer> ec = new HashMap<>();
        ec.put("PHILO", 12);
        ec.put("MATH", 14);
        ec.put("SC_PHYS", 9);
        ec.put("SC_NAT", 16);
            req.setNotesEcrites(ec);
        Map<String, Integer> or_ = new HashMap<>();
        or_.put("LV1", 12);
        or_.put("HIST_GEO", 14);
        or_.put("SC_PHYS_NAT_ORAL", 9);
            req.setNotesOrales(or_);

            com.officedubac.project.modules.ddeuxiemepartie.dto.ReleveDDeuxiemePartieSaisieRequest.EducationPhysiqueSaisie ep = new com.officedubac.project.modules.ddeuxiemepartie.dto.ReleveDDeuxiemePartieSaisieRequest.EducationPhysiqueSaisie();
            ep.setNote(13);
            req.setEducationPhysique(ep);

            req.setLieuDelivrance("DAKAR");
            req.setDateDelivrance(LocalDate.of(2026, 7, 20));
            req.setPresidentJury("Professeur TEST");

            com.officedubac.project.modules.ddeuxiemepartie.model.ReleveDDeuxiemePartie releve = ddeuxiemepartieService.creer(req);
            afficher("ddeuxiemepartie", "/api/v1/releves-d-2eme-partie", releve.getId());
        }

        {
            com.officedubac.project.modules.f1deuxiemepartie.dto.ReleveF1DeuxiemePartieSaisieRequest req = new com.officedubac.project.modules.f1deuxiemepartie.dto.ReleveF1DeuxiemePartieSaisieRequest();
            req.setJuryNumero("37");
            req.setNomPrenom("CANDIDAT TEST 27");
            req.setDateNaissance(LocalDate.of(2005, 1, 15));
            req.setLieuNaissance("DAKAR");
            req.setEtablissement("LYCEE TEST");
            req.setIndicatif("LT");
            req.setOptions("f1deuxiemepartie");
            req.setNumeroTable("100027");
            req.setNationalite("SEN");
            req.setNombreDeFois("1");
        Map<String, Integer> ec = new HashMap<>();
        ec.put("MATHS", 12);
        ec.put("ELECTRICITE_METAL", 14);
        ec.put("MECANIQUE", 9);
        ec.put("ETUDE_PROJET", 16);
        ec.put("ANALYSE_FAB_OUTIL", 11);
            req.setNotesEcrites(ec);
        Map<String, Integer> or_ = new HashMap<>();
        or_.put("AUTOMATISME", 12);
        or_.put("TECHNOLOGIE", 14);
        or_.put("LV", 9);
        or_.put("EPREUVE_ATELIER", 16);
            req.setNotesOrales(or_);

            req.setLieuDelivrance("DAKAR");
            req.setDateDelivrance(LocalDate.of(2026, 7, 20));
            req.setPresidentJury("Professeur TEST");

            com.officedubac.project.modules.f1deuxiemepartie.model.ReleveF1DeuxiemePartie releve = f1deuxiemepartieService.creer(req);
            afficher("f1deuxiemepartie", "/api/v1/releves-f1-2eme-partie", releve.getId());
        }

        {
            com.officedubac.project.modules.a1deuxiemepartie.dto.ReleveA1DeuxiemePartieSaisieRequest req = new com.officedubac.project.modules.a1deuxiemepartie.dto.ReleveA1DeuxiemePartieSaisieRequest();
            req.setJuryNumero("38");
            req.setNomPrenom("CANDIDAT TEST 28");
            req.setDateNaissance(LocalDate.of(2005, 1, 15));
            req.setLieuNaissance("DAKAR");
            req.setEtablissement("LYCEE TEST");
            req.setIndicatif("LT");
            req.setOptions("a1deuxiemepartie");
            req.setNumeroTable("100028");
            req.setNationalite("SEN");
            req.setNombreDeFois("1");
        Map<String, Integer> ec = new HashMap<>();
        ec.put("PHILO", 12);
        ec.put("LAT_AR", 14);
        ec.put("GREC", 9);
        ec.put("LV", 16);
            req.setNotesEcrites(ec);
        Map<String, Integer> or_ = new HashMap<>();
        or_.put("LAT_GR_AR_ORAL", 12);
        or_.put("HIST_GEO", 14);
        or_.put("MATH", 9);
            req.setNotesOrales(or_);

            req.setLieuDelivrance("DAKAR");
            req.setDateDelivrance(LocalDate.of(2026, 7, 20));
            req.setPresidentJury("Professeur TEST");

            com.officedubac.project.modules.a1deuxiemepartie.model.ReleveA1DeuxiemePartie releve = a1deuxiemepartieService.creer(req);
            afficher("a1deuxiemepartie", "/api/releves-a1-2eme-partie", releve.getId());
        }

        {
            com.officedubac.project.modules.a2deuxiemepartie.dto.ReleveA2DeuxiemePartieSaisieRequest req = new com.officedubac.project.modules.a2deuxiemepartie.dto.ReleveA2DeuxiemePartieSaisieRequest();
            req.setJuryNumero("39");
            req.setNomPrenom("CANDIDAT TEST 29");
            req.setDateNaissance(LocalDate.of(2005, 1, 15));
            req.setLieuNaissance("DAKAR");
            req.setEtablissement("LYCEE TEST");
            req.setIndicatif("LT");
            req.setOptions("a2deuxiemepartie");
            req.setNumeroTable("100029");
            req.setNationalite("SEN");
            req.setNombreDeFois("1");
        Map<String, Integer> ec = new HashMap<>();
        ec.put("PHILO", 12);
        ec.put("LV1_ECRIT", 14);
        ec.put("HIST_GEO", 9);
        ec.put("LAT_AR", 16);
            req.setNotesEcrites(ec);
        Map<String, Integer> or_ = new HashMap<>();
        or_.put("LV1_ORAL", 12);
        or_.put("LV2_ORAL", 14);
        or_.put("MATH", 9);
            req.setNotesOrales(or_);

            req.setLieuDelivrance("DAKAR");
            req.setDateDelivrance(LocalDate.of(2026, 7, 20));
            req.setPresidentJury("Professeur TEST");

            com.officedubac.project.modules.a2deuxiemepartie.model.ReleveA2DeuxiemePartie releve = a2deuxiemepartieService.creer(req);
            afficher("a2deuxiemepartie", "/api/releves-a2-2eme-partie", releve.getId());
        }

    }
}
