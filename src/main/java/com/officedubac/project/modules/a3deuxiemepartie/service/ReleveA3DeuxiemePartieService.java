package com.officedubac.project.modules.a3deuxiemepartie.service;

import com.officedubac.project.modules.a3deuxiemepartie.dto.ReleveA3DeuxiemePartieResume;
import com.officedubac.project.modules.a3deuxiemepartie.dto.ReleveA3DeuxiemePartieSaisieRequest;
import com.officedubac.project.modules.a3deuxiemepartie.model.*;
import com.officedubac.project.modules.a3deuxiemepartie.model.Enums.DecisionJury;
import com.officedubac.project.modules.a3deuxiemepartie.repository.ReleveA3DeuxiemePartieRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ReleveA3DeuxiemePartieService {

    private final ReleveA3DeuxiemePartieRepository repository;

    public ReleveA3DeuxiemePartieService(ReleveA3DeuxiemePartieRepository repository) {
        this.repository = repository;
    }

    public ReleveA3DeuxiemePartie creer(ReleveA3DeuxiemePartieSaisieRequest request) {
        ReleveA3DeuxiemePartie releve = new ReleveA3DeuxiemePartie();
        releve.setCreatedAt(Instant.now());
        appliquer(releve, request);
        return repository.save(releve);
    }

    public ReleveA3DeuxiemePartie mettreAJour(String id, ReleveA3DeuxiemePartieSaisieRequest request) {
        ReleveA3DeuxiemePartie releve = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Relevé introuvable : " + id));
        appliquer(releve, request);
        return repository.save(releve);
    }

    public ReleveA3DeuxiemePartie obtenir(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Relevé introuvable : " + id));
    }

    public Page<ReleveA3DeuxiemePartieResume> lister(String numeroTable, Pageable pageable) {
        Page<ReleveA3DeuxiemePartie> page = (numeroTable != null && !numeroTable.isBlank())
                ? repository.findByCandidat_NumeroTableContainingIgnoreCase(numeroTable, pageable)
                : repository.findAll(pageable);
        return page.map(this::versResume);
    }

    private ReleveA3DeuxiemePartieResume versResume(ReleveA3DeuxiemePartie r) {
        return new ReleveA3DeuxiemePartieResume(
                r.getId(),
                r.getCandidat() != null ? r.getCandidat().getNumeroTable() : null,
                r.getCandidat() != null ? r.getCandidat().getNomPrenom() : null,
                r.getJuryNumero(),
                r.getTotalGeneral(),
                r.getDecision(),
                r.getCreatedAt()
        );
    }

    private void appliquer(ReleveA3DeuxiemePartie releve, ReleveA3DeuxiemePartieSaisieRequest request) {
        releve.setJuryNumero(request.getJuryNumero());

        Candidat candidat = new Candidat();
        candidat.setNomPrenom(request.getNomPrenom());
        candidat.setDateNaissance(request.getDateNaissance());
        candidat.setLieuNaissance(request.getLieuNaissance());
        candidat.setEtablissement(request.getEtablissement());
        candidat.setIndicatif(request.getIndicatif());
        candidat.setOptions(request.getOptions());
        candidat.setNumeroTable(request.getNumeroTable());
        candidat.setNationalite(request.getNationalite());
        candidat.setNombreDeFois(request.getNombreDeFois());
        releve.setCandidat(candidat);

        List<NoteEpreuve> notesEcrites = new ArrayList<>();
        int totalEcrit = 0;
        for (Matiere m : MatieresA3DeuxiemePartie.EPREUVES_ECRITES) {
            Integer note = valeur(request.getNotesEcrites(), m.getCode());
            NoteEpreuve ne = new NoteEpreuve(m.getCode(), note);
            int pts = points(note, m.getCoefficient());
            ne.setPointsObtenus(pts);
            notesEcrites.add(ne);
            totalEcrit += pts;
        }
        releve.setNotesEcrites(notesEcrites);
        releve.setTotalEcrit(totalEcrit);

        List<NoteEpreuve> notesOrales = new ArrayList<>();
        int totalOral = 0;
        for (Matiere m : MatieresA3DeuxiemePartie.EPREUVES_ORALES) {
            Integer note = valeur(request.getNotesOrales(), m.getCode());
            NoteEpreuve ne = new NoteEpreuve(m.getCode(), note);
            int pts = points(note, m.getCoefficient());
            ne.setPointsObtenus(pts);
            notesOrales.add(ne);
            totalOral += pts;
        }
        releve.setNotesOrales(notesOrales);
        releve.setTotalOral(totalOral);

        int totalGeneral = totalEcrit + totalOral;
        releve.setTotalGeneral(totalGeneral);

        double moyenneSur20 = (totalGeneral / (double) MatieresA3DeuxiemePartie.BAREME_GENERAL) * 20;
        releve.setDecision(moyenneSur20 >= 10 ? DecisionJury.ADMIS : DecisionJury.AJOURNE);

        releve.setLieuDelivrance(request.getLieuDelivrance());
        releve.setDateDelivrance(request.getDateDelivrance());
        releve.setPresidentJury(request.getPresidentJury());
    }

    private int points(Integer note, int coefficient) {
        return note == null ? 0 : note * coefficient;
    }

    private int defaut(Integer v) {
        return v == null ? 0 : v;
    }

    private Integer valeur(Map<String, Integer> map, String code) {
        return map == null ? null : map.get(code);
    }
}
