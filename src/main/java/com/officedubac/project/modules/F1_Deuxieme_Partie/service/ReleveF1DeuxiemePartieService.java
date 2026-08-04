package com.officedubac.project.modules.F1_Deuxieme_Partie.service;

import com.officedubac.project.modules.F1_Deuxieme_Partie.dto.ReleveF1DeuxiemePartieSaisieRequest;
import com.officedubac.project.modules.F1_Deuxieme_Partie.model.*;
import com.officedubac.project.modules.F1_Deuxieme_Partie.model.Enums.DecisionJury;
import com.officedubac.project.modules.F1_Deuxieme_Partie.repository.ReleveF1DeuxiemePartieRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ReleveF1DeuxiemePartieService {

    private final ReleveF1DeuxiemePartieRepository repository;

    public ReleveF1DeuxiemePartieService(ReleveF1DeuxiemePartieRepository repository) {
        this.repository = repository;
    }

    public ReleveF1DeuxiemePartie creer(ReleveF1DeuxiemePartieSaisieRequest request) {
        ReleveF1DeuxiemePartie releve = new ReleveF1DeuxiemePartie();
        appliquer(releve, request);
        return repository.save(releve);
    }

    public ReleveF1DeuxiemePartie mettreAJour(String id, ReleveF1DeuxiemePartieSaisieRequest request) {
        ReleveF1DeuxiemePartie releve = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Relevé introuvable : " + id));
        appliquer(releve, request);
        return repository.save(releve);
    }

    public ReleveF1DeuxiemePartie obtenir(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Relevé introuvable : " + id));
    }

    private void appliquer(ReleveF1DeuxiemePartie releve, ReleveF1DeuxiemePartieSaisieRequest request) {
        releve.setJuryNumero(request.getJuryNumero());

        Candidat candidat = new Candidat();
        candidat.setNomPrenom(request.getNomPrenom());
        candidat.setDateNaissance(request.getDateNaissance());
        candidat.setLieuNaissance(request.getLieuNaissance());
        releve.setCandidat(candidat);

        // ---- Epreuves écrites ----
        List<NoteEpreuve> notesEcrites = new ArrayList<>();
        int totalEcrit = 0;
        for (Matiere m : MatieresF1DeuxiemePartie.EPREUVES_ECRITES) {
            Integer note = valeur(request.getNotesEcrites(), m.getCode());
            NoteEpreuve ne = new NoteEpreuve(m.getCode(), note);
            int pts = points(note, m.getCoefficient());
            ne.setPointsObtenus(pts);
            notesEcrites.add(ne);
            totalEcrit += pts;
        }
        releve.setNotesEcrites(notesEcrites);
        releve.setTotalEcrit(totalEcrit);

        // ---- Epreuves orales ----
        List<NoteEpreuve> notesOrales = new ArrayList<>();
        int totalOral = 0;
        for (Matiere m : MatieresF1DeuxiemePartie.EPREUVES_ORALES) {
            Integer note = valeur(request.getNotesOrales(), m.getCode());
            NoteEpreuve ne = new NoteEpreuve(m.getCode(), note);
            int pts = points(note, m.getCoefficient());
            ne.setPointsObtenus(pts);
            notesOrales.add(ne);
            totalOral += pts;
        }
        releve.setNotesOrales(notesOrales);
        releve.setTotalOral(totalOral);

        // ---- Total général ----
        int totalGeneral = totalEcrit + totalOral;
        releve.setTotalGeneral(totalGeneral);

        // ---- Décision : ADMIS (>=10/20), DEUXIEME_SESSION (>=8/20), sinon AJOURNE ----
        double moyenneSur20 = (totalGeneral / (double) MatieresF1DeuxiemePartie.BAREME_GENERAL) * 20;
        DecisionJury decision;
        if (moyenneSur20 >= 10) {
            decision = DecisionJury.ADMIS;
        } else if (moyenneSur20 >= 8) {
            decision = DecisionJury.DEUXIEME_SESSION;
        } else {
            decision = DecisionJury.AJOURNE;
        }
        releve.setDecision(decision);

        releve.setLieuDelivrance(request.getLieuDelivrance());
        releve.setDateDelivrance(request.getDateDelivrance());
        releve.setPresidentJury(request.getPresidentJury());
    }

    private int points(Integer note, int coefficient) {
        return note == null ? 0 : note * coefficient;
    }

    private Integer valeur(Map<String, Integer> map, String code) {
        return map == null ? null : map.get(code);
    }
}
