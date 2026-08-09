package com.officedubac.project.modules.G2.service;

import com.officedubac.project.modules.G2.dto.RelevNoteG2Resume;
import com.officedubac.project.modules.G2.dto.RelevNoteG2SaisieRequest;
import com.officedubac.project.modules.G2.model.*;
import com.officedubac.project.modules.G2.model.Enums.DecisionJury;
import com.officedubac.project.modules.G2.model.Enums.Mention;
import com.officedubac.project.modules.G2.repository.RelevNoteG2Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service métier : saisie des notes et calcul des totaux/mentions/décisions
 * selon les règles imprimées sur le formulaire officiel série G2.
 */
@Service
public class RelevNoteG2Service {

    private final RelevNoteG2Repository repository;

    public RelevNoteG2Service(RelevNoteG2Repository repository) {
        this.repository = repository;
    }

    public RelevNoteG2 creer(RelevNoteG2SaisieRequest request) {
        RelevNoteG2 releve = new RelevNoteG2();
        releve.setCreatedAt(Instant.now());
        appliquer(releve, request);
        return repository.save(releve);
    }

    public RelevNoteG2 mettreAJour(String id, RelevNoteG2SaisieRequest request) {
        RelevNoteG2 releve = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Relevé introuvable : " + id));
        appliquer(releve, request);
        return repository.save(releve);
    }

    public RelevNoteG2 obtenir(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Relevé introuvable : " + id));
    }

    public Page<RelevNoteG2Resume> lister(String numeroTable, Integer annee, Pageable pageable) {
        boolean hasNumeroTable = numeroTable != null && !numeroTable.isBlank();
        boolean hasAnnee = annee != null;

        Page<RelevNoteG2> page;
        if (hasNumeroTable && hasAnnee) {
            page = repository.findByCandidat_NumeroTableContainingIgnoreCaseAndAnnee(numeroTable, annee, pageable);
        } else if (hasNumeroTable) {
            page = repository.findByCandidat_NumeroTableContainingIgnoreCase(numeroTable, pageable);
        } else if (hasAnnee) {
            page = repository.findByAnnee(annee, pageable);
        } else {
            page = repository.findAll(pageable);
        }
        return page.map(this::versResume);
    }

    private RelevNoteG2Resume versResume(RelevNoteG2 r) {
        DecisionJury decision = r.getDecisionDeuxiemeGroupe() != null
                ? r.getDecisionDeuxiemeGroupe()
                : r.getDecisionPremierGroupe();
        Mention mention = r.getMentionDeuxiemeGroupe() != null
                ? r.getMentionDeuxiemeGroupe()
                : r.getMentionPremierGroupe();
        return new RelevNoteG2Resume(
                r.getId(),
                r.getCandidat() != null ? r.getCandidat().getNumeroTable() : null,
                r.getCandidat() != null ? r.getCandidat().getNomPrenom() : null,
                r.getJuryNumero(),
                r.getAnnee(),
                r.getTotalDefinitif(),
                decision,
                mention,
                r.getCreatedAt()
        );
    }

    private void appliquer(RelevNoteG2 releve, RelevNoteG2SaisieRequest request) {
        releve.setSession(request.getSession());
        releve.setJuryNumero(request.getJuryNumero());
        releve.setAnnee(request.getAnnee());

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

        List<NoteEpreuve> notesPremierGroupe = new ArrayList<>();
        int totalPremierGroupe = 0;
        for (Matiere matiere : MatieresG2.PREMIER_GROUPE) {
            Integer note = valeurOuNull(request.getNotesPremierGroupe(), matiere.getCode());
            NoteEpreuve ne = new NoteEpreuve(matiere.getCode(), note);
            int points = points(note, matiere.getCoefficient());
            ne.setPointsObtenus(points);
            notesPremierGroupe.add(ne);
            totalPremierGroupe += points;
        }
        releve.setNotesPremierGroupe(notesPremierGroupe);
        releve.setTotalPremierGroupe(totalPremierGroupe);
        releve.setReportPremierTotal(totalPremierGroupe);

        List<NoteEpreuve> notesDeuxiemeGroupe = new ArrayList<>();
        int totalDeuxiemeGroupe = 0;
        for (Matiere matiere : MatieresG2.DEUXIEME_GROUPE) {
            Integer note = valeurOuNull(request.getNotesDeuxiemeGroupe(), matiere.getCode());
            NoteEpreuve ne = new NoteEpreuve(matiere.getCode(), note);
            int points = points(note, matiere.getCoefficient());
            ne.setPointsObtenus(points);
            notesDeuxiemeGroupe.add(ne);
            totalDeuxiemeGroupe += points;
        }
        releve.setNotesDeuxiemeGroupe(notesDeuxiemeGroupe);

        List<EpreuveOraleControle> controles = new ArrayList<>();
        if (request.getEpreuvesOralesControle() != null) {
            for (RelevNoteG2SaisieRequest.EpreuveOraleControleSaisie s : request.getEpreuvesOralesControle()) {
                EpreuveOraleControle c = new EpreuveOraleControle();
                c.setMatiereChoisie(s.getMatiereChoisie());
                c.setCoefficient(s.getCoefficient());
                c.setRappelPointsObtenus1erGroupe(s.getRappelPointsObtenus1erGroupe());
                c.setNouvelleNoteSur20(s.getNouvelleNoteSur20());
                int rappel = defaut(s.getRappelPointsObtenus1erGroupe());
                int coefficient = defaut(s.getCoefficient());
                int nouveauxPoints = defaut(s.getNouvelleNoteSur20()) * coefficient;
                c.setPointsObtenusEpreuveControle(nouveauxPoints);
                c.setDifferenceEnPlus(Math.max(0, nouveauxPoints - rappel));
                controles.add(c);
            }
        }
        releve.setEpreuvesOralesControle(controles);
        int totalDifferencesControle = controles.stream()
                .mapToInt(c -> defaut(c.getDifferenceEnPlus()))
                .sum();

        int totalProvisoire = totalPremierGroupe + totalDeuxiemeGroupe + totalDifferencesControle;
        releve.setTotalProvisoire(totalProvisoire);

        EducationPhysique ep = new EducationPhysique();
        if (request.getEducationPhysique() != null) {
            ep.setNote(request.getEducationPhysique().getNote());
        }
        int notePhysique = defaut(ep.getNote());
        int ecart = notePhysique - 10;
        ep.setPointsPositifs(Math.max(0, ecart));
        ep.setPointsNegatifs(Math.max(0, -ecart));
        releve.setEducationPhysique(ep);

        List<EpreuveFacultative> facultatives = new ArrayList<>();
        int totalPointsFacultatifs = 0;
        if (request.getEpreuvesFacultatives() != null) {
            for (RelevNoteG2SaisieRequest.EpreuveFacultativeSaisie s : request.getEpreuvesFacultatives()) {
                EpreuveFacultative f = new EpreuveFacultative();
                f.setType(TypeFacultative.valueOf(s.getType()));
                f.setNote(s.getNote());
                int pts = Math.max(0, defaut(s.getNote()) - 10);
                f.setPointsAuDessusMoyenne(pts);
                facultatives.add(f);
                totalPointsFacultatifs += pts;
            }
        }
        releve.setEpreuvesFacultatives(facultatives);

        int totalDefinitif = totalProvisoire + ep.getPointsPositifs() - ep.getPointsNegatifs();
        releve.setTotalDefinitif(totalDefinitif);

        Mention mention1erGroupe = mention(totalPremierGroupe, MatieresG2.BAREME_PREMIER_GROUPE);
        releve.setMentionPremierGroupe(mention1erGroupe);
        releve.setDecisionPremierGroupe(decision(totalPremierGroupe, MatieresG2.BAREME_PREMIER_GROUPE));

        int totalDefinitifAvecFacultatifs = totalDefinitif;
        Mention mentionAvantBonus = mention(totalDefinitif, MatieresG2.BAREME_TOTAL_DEFINITIF);
        if (mentionAvantBonus == Mention.BIEN || mentionAvantBonus == Mention.TRES_BIEN) {
            totalDefinitifAvecFacultatifs += totalPointsFacultatifs;
        }
        releve.setMentionDeuxiemeGroupe(mention(totalDefinitifAvecFacultatifs, MatieresG2.BAREME_TOTAL_DEFINITIF));
        releve.setDecisionDeuxiemeGroupe(decision(totalDefinitifAvecFacultatifs, MatieresG2.BAREME_TOTAL_DEFINITIF));

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

    private Integer valeurOuNull(Map<String, Integer> map, String code) {
        return map == null ? null : map.get(code);
    }

    private Mention mention(int totalPoints, int bareme) {
        double moyenneSur20 = (totalPoints / (double) bareme) * 20;
        if (moyenneSur20 >= 16) return Mention.TRES_BIEN;
        if (moyenneSur20 >= 14) return Mention.BIEN;
        if (moyenneSur20 >= 12) return Mention.ASSEZ_BIEN;
        if (moyenneSur20 >= 10) return Mention.PASSABLE;
        return Mention.AUCUNE;
    }

    private DecisionJury decision(int totalPoints, int bareme) {
        double moyenneSur20 = (totalPoints / (double) bareme) * 20;
        if (moyenneSur20 >= 10) return DecisionJury.ADMIS;
        if (moyenneSur20 >= 8) return DecisionJury.AUTORISE_SECOND_GROUPE;
        return DecisionJury.AJOURNE;
    }
}
