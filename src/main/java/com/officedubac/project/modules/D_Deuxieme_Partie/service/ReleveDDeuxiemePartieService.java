package com.officedubac.project.modules.D_Deuxieme_Partie.service;

import com.officedubac.project.modules.D_Deuxieme_Partie.dto.ReleveDDeuxiemePartieSaisieRequest;
import com.officedubac.project.modules.D_Deuxieme_Partie.model.*;
import com.officedubac.project.modules.D_Deuxieme_Partie.model.Enums.DecisionJury;
import com.officedubac.project.modules.D_Deuxieme_Partie.repository.ReleveDDeuxiemePartieRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ReleveDDeuxiemePartieService {

    private final ReleveDDeuxiemePartieRepository repository;

    public ReleveDDeuxiemePartieService(ReleveDDeuxiemePartieRepository repository) {
        this.repository = repository;
    }

    public ReleveDDeuxiemePartie creer(ReleveDDeuxiemePartieSaisieRequest request) {
        ReleveDDeuxiemePartie releve = new ReleveDDeuxiemePartie();
        appliquer(releve, request);
        return repository.save(releve);
    }

    public ReleveDDeuxiemePartie mettreAJour(String id, ReleveDDeuxiemePartieSaisieRequest request) {
        ReleveDDeuxiemePartie releve = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Relevé introuvable : " + id));
        appliquer(releve, request);
        return repository.save(releve);
    }

    public ReleveDDeuxiemePartie obtenir(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Relevé introuvable : " + id));
    }

    private void appliquer(ReleveDDeuxiemePartie releve, ReleveDDeuxiemePartieSaisieRequest request) {
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
        for (Matiere m : MatieresDDeuxiemePartie.EPREUVES_ECRITES) {
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
        for (Matiere m : MatieresDDeuxiemePartie.EPREUVES_ORALES) {
            Integer note = valeur(request.getNotesOrales(), m.getCode());
            NoteEpreuve ne = new NoteEpreuve(m.getCode(), note);
            int pts = points(note, m.getCoefficient());
            ne.setPointsObtenus(pts);
            notesOrales.add(ne);
            totalOral += pts;
        }
        releve.setNotesOrales(notesOrales);
        releve.setTotalOral(totalOral);

        // ---- Ajustement EPS (+/-) ----
        int ajustementEps = request.getAjustementEps() == null ? 0 : request.getAjustementEps();
        releve.setAjustementEps(ajustementEps);

        // ---- Total général = écrit + oral + EPS ----
        int totalGeneral = totalEcrit + totalOral + ajustementEps;
        releve.setTotalGeneral(totalGeneral);

        // ---- Décision (ADMIS si moyenne >= 10/20 sur le barème hors EPS, sinon AJOURNE) ----
        double moyenneSur20 = (totalGeneral / (double) MatieresDDeuxiemePartie.BAREME_GENERAL) * 20;
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
