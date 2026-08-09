package com.officedubac.project.modules.c2emepartie.service;

import com.officedubac.project.modules.c2emepartie.dto.ReleveC2emePartieResume;
import com.officedubac.project.modules.c2emepartie.dto.ReleveC2emePartieSaisieRequest;
import com.officedubac.project.modules.c2emepartie.model.*;
import com.officedubac.project.modules.c2emepartie.model.Enums.DecisionJury;
import com.officedubac.project.modules.c2emepartie.repository.ReleveC2emePartieRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ReleveC2emePartieService {

    private final ReleveC2emePartieRepository repository;

    public ReleveC2emePartieService(ReleveC2emePartieRepository repository) {
        this.repository = repository;
    }

    public ReleveC2emePartie creer(ReleveC2emePartieSaisieRequest request) {
        ReleveC2emePartie releve = new ReleveC2emePartie();
        releve.setCreatedAt(Instant.now());
        appliquer(releve, request);
        return repository.save(releve);
    }

    public ReleveC2emePartie mettreAJour(String id, ReleveC2emePartieSaisieRequest request) {
        ReleveC2emePartie releve = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Relevé introuvable : " + id));
        appliquer(releve, request);
        return repository.save(releve);
    }

    public ReleveC2emePartie obtenir(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Relevé introuvable : " + id));
    }

    public Page<ReleveC2emePartieResume> lister(String numeroTable, Pageable pageable) {
        Page<ReleveC2emePartie> page = (numeroTable != null && !numeroTable.isBlank())
                ? repository.findByCandidat_NumeroTableContainingIgnoreCase(numeroTable, pageable)
                : repository.findAll(pageable);
        return page.map(this::versResume);
    }

    private ReleveC2emePartieResume versResume(ReleveC2emePartie r) {
        return new ReleveC2emePartieResume(
                r.getId(),
                r.getCandidat() != null ? r.getCandidat().getNumeroTable() : null,
                r.getCandidat() != null ? r.getCandidat().getNomPrenom() : null,
                r.getJuryNumero(),
                r.getTotalGeneral(),
                r.getDecision(),
                r.getCreatedAt()
        );
    }

    private void appliquer(ReleveC2emePartie releve, ReleveC2emePartieSaisieRequest request) {
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
        for (Matiere m : MatieresC2emePartie.EPREUVES_ECRITES) {
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
        for (Matiere m : MatieresC2emePartie.EPREUVES_ORALES) {
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

        double moyenneSur20 = (totalGeneral / (double) MatieresC2emePartie.BAREME_GENERAL) * 20;
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
