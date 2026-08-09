package com.officedubac.project.modules.a1deuxiemepartie.service;

import com.officedubac.project.modules.a1deuxiemepartie.dto.ReleveA1DeuxiemePartieResume;
import com.officedubac.project.modules.a1deuxiemepartie.dto.ReleveA1DeuxiemePartieSaisieRequest;
import com.officedubac.project.modules.a1deuxiemepartie.model.*;
import com.officedubac.project.modules.a1deuxiemepartie.model.Enums.DecisionJury;
import com.officedubac.project.modules.a1deuxiemepartie.repository.ReleveA1DeuxiemePartieRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ReleveA1DeuxiemePartieService {

    private final ReleveA1DeuxiemePartieRepository repository;

    public ReleveA1DeuxiemePartieService(ReleveA1DeuxiemePartieRepository repository) {
        this.repository = repository;
    }

    public ReleveA1DeuxiemePartie creer(ReleveA1DeuxiemePartieSaisieRequest request) {
        ReleveA1DeuxiemePartie releve = new ReleveA1DeuxiemePartie();
        releve.setCreatedAt(Instant.now());
        appliquer(releve, request);
        return repository.save(releve);
    }

    public ReleveA1DeuxiemePartie mettreAJour(String id, ReleveA1DeuxiemePartieSaisieRequest request) {
        ReleveA1DeuxiemePartie releve = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Relevé introuvable : " + id));
        appliquer(releve, request);
        return repository.save(releve);
    }

    public ReleveA1DeuxiemePartie obtenir(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Relevé introuvable : " + id));
    }

    public Page<ReleveA1DeuxiemePartieResume> lister(String numeroTable, Pageable pageable) {
        Page<ReleveA1DeuxiemePartie> page = (numeroTable != null && !numeroTable.isBlank())
                ? repository.findByCandidat_NumeroTableContainingIgnoreCase(numeroTable, pageable)
                : repository.findAll(pageable);
        return page.map(this::versResume);
    }

    private ReleveA1DeuxiemePartieResume versResume(ReleveA1DeuxiemePartie r) {
        return new ReleveA1DeuxiemePartieResume(
                r.getId(),
                r.getCandidat() != null ? r.getCandidat().getNumeroTable() : null,
                r.getCandidat() != null ? r.getCandidat().getNomPrenom() : null,
                r.getJuryNumero(),
                r.getTotalGeneral(),
                r.getDecision(),
                r.getCreatedAt()
        );
    }

    private void appliquer(ReleveA1DeuxiemePartie releve, ReleveA1DeuxiemePartieSaisieRequest request) {
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

        // ---- Epreuves écrites ----
        List<NoteEpreuve> notesEcrites = new ArrayList<>();
        int totalEcrit = 0;
        for (Matiere m : MatieresA1DeuxiemePartie.EPREUVES_ECRITES) {
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
        for (Matiere m : MatieresA1DeuxiemePartie.EPREUVES_ORALES) {
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
        double moyenneSur20 = (totalGeneral / (double) MatieresA1DeuxiemePartie.BAREME_GENERAL) * 20;
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
