package com.officedubac.project.modules.A2_DeuxiemePartie.service;

import com.officedubac.project.modules.A2_DeuxiemePartie.dto.ReleveA2DeuxiemePartieSaisieRequest;
import com.officedubac.project.modules.A2_DeuxiemePartie.model.*;
import com.officedubac.project.modules.A2_DeuxiemePartie.model.Enums.DecisionJury;
import com.officedubac.project.modules.A2_DeuxiemePartie.repository.ReleveA2DeuxiemePartieRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ReleveA2DeuxiemePartieService {

    private final ReleveA2DeuxiemePartieRepository repository;

    public ReleveA2DeuxiemePartieService(ReleveA2DeuxiemePartieRepository repository) {
        this.repository = repository;
    }

    public ReleveA2DeuxiemePartie creer(ReleveA2DeuxiemePartieSaisieRequest request) {
        ReleveA2DeuxiemePartie releve = new ReleveA2DeuxiemePartie();
        appliquer(releve, request);
        return repository.save(releve);
    }

    public ReleveA2DeuxiemePartie mettreAJour(String id, ReleveA2DeuxiemePartieSaisieRequest request) {
        ReleveA2DeuxiemePartie releve = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Relevé introuvable : " + id));
        appliquer(releve, request);
        return repository.save(releve);
    }

    public ReleveA2DeuxiemePartie obtenir(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Relevé introuvable : " + id));
    }

    private void appliquer(ReleveA2DeuxiemePartie releve, ReleveA2DeuxiemePartieSaisieRequest request) {
        releve.setJuryNumero(request.getJuryNumero());

        Candidat candidat = new Candidat();
        candidat.setNomPrenom(request.getNomPrenom());
        candidat.setDateNaissance(request.getDateNaissance());
        candidat.setLieuNaissance(request.getLieuNaissance());
        candidat.setEtablissement(request.getEtablissement());
        candidat.setIndicatif(request.getIndicatif());
        candidat.setOptions(request.getOptions());
        releve.setCandidat(candidat);

        // ---- Epreuves écrites ----
        List<NoteEpreuve> notesEcrites = new ArrayList<>();
        int totalEcrit = 0;
        for (Matiere m : MatieresA2DeuxiemePartie.EPREUVES_ECRITES) {
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
        for (Matiere m : MatieresA2DeuxiemePartie.EPREUVES_ORALES) {
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

        // ---- Décision (ADMIS si moyenne >= 10/20, sinon AJOURNE) ----
        double moyenneSur20 = (totalGeneral / (double) MatieresA2DeuxiemePartie.BAREME_GENERAL) * 20;
        releve.setDecision(moyenneSur20 >= 10 ? DecisionJury.ADMIS : DecisionJury.AJOURNE);

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
