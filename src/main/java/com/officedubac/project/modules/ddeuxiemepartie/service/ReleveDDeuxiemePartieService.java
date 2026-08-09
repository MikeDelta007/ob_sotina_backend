package com.officedubac.project.modules.ddeuxiemepartie.service;

import com.officedubac.project.modules.ddeuxiemepartie.dto.ReleveDDeuxiemePartieResume;
import com.officedubac.project.modules.ddeuxiemepartie.dto.ReleveDDeuxiemePartieSaisieRequest;
import com.officedubac.project.modules.ddeuxiemepartie.model.*;
import com.officedubac.project.modules.ddeuxiemepartie.model.Enums.DecisionJury;
import com.officedubac.project.modules.ddeuxiemepartie.repository.ReleveDDeuxiemePartieRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
        releve.setCreatedAt(Instant.now());
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

    public Page<ReleveDDeuxiemePartieResume> lister(String numeroTable, Pageable pageable) {
        Page<ReleveDDeuxiemePartie> page = (numeroTable != null && !numeroTable.isBlank())
                ? repository.findByCandidat_NumeroTableContainingIgnoreCase(numeroTable, pageable)
                : repository.findAll(pageable);
        return page.map(this::versResume);
    }

    private ReleveDDeuxiemePartieResume versResume(ReleveDDeuxiemePartie r) {
        return new ReleveDDeuxiemePartieResume(
                r.getId(),
                r.getCandidat() != null ? r.getCandidat().getNumeroTable() : null,
                r.getCandidat() != null ? r.getCandidat().getNomPrenom() : null,
                r.getJuryNumero(),
                r.getTotalGeneral(),
                r.getDecision(),
                r.getCreatedAt()
        );
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
        candidat.setNumeroTable(request.getNumeroTable());
        candidat.setNationalite(request.getNationalite());
        candidat.setNombreDeFois(request.getNombreDeFois());
        releve.setCandidat(candidat);

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

        EducationPhysique ep = new EducationPhysique();
        if (request.getEducationPhysique() != null) {
            ep.setNote(request.getEducationPhysique().getNote());
        }
        int notePhysique = defaut(ep.getNote());
        int ecart = notePhysique - 10;
        ep.setPointsPositifs(Math.max(0, ecart));
        ep.setPointsNegatifs(Math.max(0, -ecart));
        releve.setEducationPhysique(ep);

        int totalGeneral = totalEcrit + totalOral + ep.getPointsPositifs() - ep.getPointsNegatifs();
        releve.setTotalGeneral(totalGeneral);

        double moyenneSur20 = (totalGeneral / (double) MatieresDDeuxiemePartie.BAREME_GENERAL) * 20;
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
