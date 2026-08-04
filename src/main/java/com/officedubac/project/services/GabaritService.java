package com.officedubac.project.services;

import com.officedubac.project.dto.*;
import com.officedubac.project.models.*;
import com.officedubac.project.repository.ReleveNoteRepository;
import com.officedubac.project.repository.SerieReleveRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class GabaritService {

    private static final List<String> CHAMPS_ENTETE = List.of(
            "nom", "prenom", "dateNaissance", "lieuNaissance",
            "centreExamen", "numeroTable", "session", "annee"
    );

    private final SerieReleveRepository serieReleveRepository;
    private final ReleveNoteRepository releveNoteRepository;

    public GabaritService(SerieReleveRepository serieReleveRepository, ReleveNoteRepository releveNoteRepository) {
        this.serieReleveRepository = serieReleveRepository;
        this.releveNoteRepository = releveNoteRepository;
    }

    public GabaritDTO getGabarit(String serieId) {
        SerieReleve serie = serieReleveRepository.findById(serieId)
                .orElseThrow(() -> new IllegalArgumentException("Série introuvable : " + serieId));

        List<MatiereReferentielDTO> matieresGroupe1 = serie.getMatieresGroupe1().stream()
                .sorted(Comparator.comparing(m -> m.getOrdre() == null ? 0 : m.getOrdre()))
                .map(m -> new MatiereReferentielDTO(m.getId(), m.getLibelle(), m.getCoefficient(), m.getOrdre()))
                .toList();

        return new GabaritDTO(
                serie.getId(),
                serie.getCode(),
                serie.getLibelle(),
                CHAMPS_ENTETE,
                matieresGroupe1
        );
    }

    public ReleveNoteResponse creerReleve(ReleveNoteRequest request) {
        SerieReleve serie = serieReleveRepository.findById(request.serieId())
                .orElseThrow(() -> new IllegalArgumentException("Série introuvable"));

        ReleveNotes releve = new ReleveNotes();
        releve.setSerieId(serie.getId());
        releve.setSerieCode(serie.getCode());
        releve.setSerieLibelle(serie.getLibelle());
        releve.setNom(request.nom());
        releve.setPrenom(request.prenom());
        releve.setDateNaissance(request.dateNaissance());
        releve.setLieuNaissance(request.lieuNaissance());
        releve.setCentreExamen(request.centreExamen());
        releve.setNumeroTable(request.numeroTable());
        releve.setSession(request.session());
        releve.setAnnee(request.annee());

        double totalPoints = 0;
        double totalCoeff = 0;
        List<LigneNote> lignesGroupe1 = new ArrayList<>();
        List<LigneNote> lignesGroupe2 = new ArrayList<>();

        // Traitement des matières du groupe 1
        for (LigneNoteRequest ligneReq : request.lignesGroupe1()) {

            validerNote(ligneReq.note());

            LigneNote ligne = new LigneNote();
            ligne.setGroupe(1);

            if (ligneReq.matiereRefId() == null) {
                throw new IllegalArgumentException("matiereRefId requis pour une ligne du groupe 1");
            }

            MatiereReferentiel ref = trouverMatiere(serie, ligneReq.matiereRefId());

            double coefficient = ref.getCoefficient();
            double points = ligneReq.note() * coefficient;

            ligne.setMatiereRefId(ref.getId());
            ligne.setLibelleMatiere(ref.getLibelle());
            ligne.setCoefficient(coefficient);
            ligne.setNote(ligneReq.note());
            ligne.setPoints(points);

            lignesGroupe1.add(ligne);

            totalPoints += points;
            totalCoeff += coefficient;
        }

// Traitement des matières du groupe 2
        for (LigneNoteRequest ligneReq : request.lignesGroupe2()) {

            validerNote(ligneReq.note());

            LigneNote ligne = new LigneNote();
            ligne.setGroupe(2);

            if (ligneReq.libelleMatiere() == null || ligneReq.libelleMatiere().isBlank()) {
                throw new IllegalArgumentException("Le libellé de la matière est obligatoire pour le groupe 2");
            }

            if (ligneReq.coefficient() == null || ligneReq.coefficient() <= 0) {
                throw new IllegalArgumentException("Le coefficient doit être positif pour le groupe 2");
            }

            double coefficient = ligneReq.coefficient();
            double points = ligneReq.note() * coefficient;

            ligne.setLibelleMatiere(ligneReq.libelleMatiere());
            ligne.setCoefficient(coefficient);
            ligne.setNote(ligneReq.note());
            ligne.setPoints(points);

            lignesGroupe2.add(ligne);

            totalPoints += points;
            totalCoeff += coefficient;
        }

        releve.setLignesGroupe1(lignesGroupe1);
        releve.setLignesGroupe2(lignesGroupe2);

        double moyenne = totalCoeff > 0 ? totalPoints / totalCoeff : 0;
        releve.setMoyenneGenerale(round2(moyenne));
        releve.setMention(calculerMention(moyenne));

        ReleveNotes saved = releveNoteRepository.save(releve);
        return toResponse(saved);
    }

    private int validerGroupe(Integer groupe) {
        if (groupe == null || (groupe != 1 && groupe != 2)) {
            throw new IllegalArgumentException("Le champ groupe doit valoir 1 ou 2");
        }
        return groupe;
    }

    private MatiereReferentiel trouverMatiere(SerieReleve serie, String matiereRefId) {
        return serie.getMatieresGroupe1().stream()
                .filter(m -> m.getId().equals(matiereRefId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Matière du référentiel introuvable"));
    }

    private void validerNote(Double note) {
        if (note == null || note < 0 || note > 20) {
            throw new IllegalArgumentException("La note doit être comprise entre 0 et 20");
        }
    }

    private String calculerMention(double moyenne) {
        if (moyenne >= 16) return "TRES BIEN";
        if (moyenne >= 14) return "BIEN";
        if (moyenne >= 12) return "ASSEZ BIEN";
        if (moyenne >= 10) return "PASSABLE";
        return "AJOURNE";
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private ReleveNoteResponse toResponse(ReleveNotes releve) {

        List<LigneNoteResponse> lignesGroupe1 = releve.getLignesGroupe1().stream()
                .map(l -> new LigneNoteResponse(
                        l.getGroupe(),
                        l.getLibelleMatiere(),
                        l.getCoefficient(),
                        l.getNote(),
                        l.getPoints()))
                .toList();

        List<LigneNoteResponse> lignesGroupe2 = releve.getLignesGroupe2().stream()
                .map(l -> new LigneNoteResponse(
                        l.getGroupe(),
                        l.getLibelleMatiere(),
                        l.getCoefficient(),
                        l.getNote(),
                        l.getPoints()))
                .toList();

        return new ReleveNoteResponse(
                releve.getId(),
                releve.getNom(),
                releve.getPrenom(),
                releve.getSerieCode(),
                releve.getMoyenneGenerale(),
                releve.getMention(),
                lignesGroupe1,
                lignesGroupe2
        );
    }

    public List<SerieReleveDTO> lister() {
        return serieReleveRepository.findAll().stream()
                .map(s -> new SerieReleveDTO(s.getId(), s.getCode(), s.getLibelle()))
                .toList();
    }

    public SerieDetailResponse getDetail(String id) {
        SerieReleve serie = serieReleveRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Série introuvable : " + id));
        return toDetailResponse(serie);
    }

    public SerieDetailResponse creer(SerieRequest request) {
        validerRequest(request);

        serieReleveRepository.findByCode(request.code()).ifPresent(s -> {
            throw new IllegalArgumentException("Le code série \"" + request.code() + "\" existe déjà");
        });

        SerieReleve serie = new SerieReleve();
        serie.setCode(request.code());
        serie.setLibelle(request.libelle());
        serie.setMatieresGroupe1(mapMatieres(request.matieresGroupe1()));

        SerieReleve saved = serieReleveRepository.save(serie);
        return toDetailResponse(saved);
    }

    public SerieDetailResponse modifier(String id, SerieRequest request) {
        validerRequest(request);

        SerieReleve serie = serieReleveRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Série introuvable : " + id));

        serieReleveRepository.findByCode(request.code()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new IllegalArgumentException("Le code série \"" + request.code() + "\" est déjà utilisé par une autre série");
            }
        });

        serie.setCode(request.code());
        serie.setLibelle(request.libelle());
        serie.setMatieresGroupe1(mapMatieres(request.matieresGroupe1()));

        SerieReleve saved = serieReleveRepository.save(serie);
        return toDetailResponse(saved);
    }

    public void supprimer(String id) {
        if (!serieReleveRepository.existsById(id)) {
            throw new IllegalArgumentException("Série introuvable : " + id);
        }
        serieReleveRepository.deleteById(id);
    }

    private void validerRequest(SerieRequest request) {
        if (request.code() == null || request.code().isBlank()) {
            throw new IllegalArgumentException("Le code de la série est obligatoire");
        }
        if (request.libelle() == null || request.libelle().isBlank()) {
            throw new IllegalArgumentException("Le libellé de la série est obligatoire");
        }
        if (request.matieresGroupe1() == null || request.matieresGroupe1().isEmpty()) {
            throw new IllegalArgumentException("Au moins une matière du 1er groupe est requise");
        }
        for (MatiereReferentielRequest m : request.matieresGroupe1()) {
            if (m.libelle() == null || m.libelle().isBlank()) {
                throw new IllegalArgumentException("Le libellé d'une matière ne peut pas être vide");
            }
            if (m.coefficient() == null || m.coefficient() <= 0) {
                throw new IllegalArgumentException("Le coefficient de \"" + m.libelle() + "\" doit être positif");
            }
        }
    }

    // Génère un id serveur pour toute nouvelle matière (celles sans id fourni par le front)
    private List<MatiereReferentiel> mapMatieres(List<MatiereReferentielRequest> requests) {
        return requests.stream()
                .map(m -> new MatiereReferentiel(
                        (m.id() == null || m.id().isBlank()) ? UUID.randomUUID().toString() : m.id(),
                        m.libelle(),
                        m.coefficient(),
                        m.ordre()
                ))
                .toList();
    }

    private SerieDetailResponse toDetailResponse(SerieReleve serie) {
        List<MatiereReferentielDTO> matieres = serie.getMatieresGroupe1().stream()
                .sorted(Comparator.comparing(m -> m.getOrdre() == null ? 0 : m.getOrdre()))
                .map(m -> new MatiereReferentielDTO(m.getId(), m.getLibelle(), m.getCoefficient(), m.getOrdre()))
                .toList();

        return new SerieDetailResponse(serie.getId(), serie.getCode(), serie.getLibelle(), matieres);
    }
}