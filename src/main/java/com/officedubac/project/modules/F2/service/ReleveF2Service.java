package com.officedubac.project.modules.F2.service;

import com.officedubac.project.modules.F2.dto.ReleveF2SaisieRequest;
import com.officedubac.project.modules.F2.model.*;
import com.officedubac.project.modules.F2.model.Enums.DecisionJury;
import com.officedubac.project.modules.F2.model.Enums.Mention;
import com.officedubac.project.modules.F2.model.Enums.TypeFacultative;
import com.officedubac.project.modules.F2.repository.ReleveF2Repository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service métier : saisie des notes et calcul des totaux/mentions/décisions
 * selon les règles imprimées sur le "CERTIFICAT PROCES-VERBAL D'EXAMEN"
 * Option F2.
 *
 * Particularité F2 (comme A3/B/D/E/F1) : le barème complet (580) est
 * atteint dès le 1er groupe. Le 2eme groupe ne fait qu'ajuster ce total.
 *
 * Particularité propre à F2 : le coefficient de l'épreuve de contrôle est
 * automatiquement repris de la matière choisie (règle (d) du formulaire),
 * il n'est pas ressaisi séparément.
 */
@Service
public class ReleveF2Service {

    private final ReleveF2Repository repository;

    public ReleveF2Service(ReleveF2Repository repository) {
        this.repository = repository;
    }

    public ReleveF2 creer(ReleveF2SaisieRequest request) {
        ReleveF2 releve = new ReleveF2();
        appliquer(releve, request);
        return repository.save(releve);
    }

    public ReleveF2 mettreAJour(String id, ReleveF2SaisieRequest request) {
        ReleveF2 releve = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Relevé introuvable : " + id));
        appliquer(releve, request);
        return repository.save(releve);
    }

    public ReleveF2 obtenir(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Relevé introuvable : " + id));
    }

    private void appliquer(ReleveF2 releve, ReleveF2SaisieRequest request) {
        releve.setSession(request.getSession());
        releve.setJuryNumero(request.getJuryNumero());

        Candidat candidat = new Candidat();
        candidat.setNomPrenom(request.getNomPrenom());
        candidat.setDateNaissance(request.getDateNaissance());
        candidat.setLieuNaissance(request.getLieuNaissance());
        candidat.setEtablissement(request.getEtablissement());
        candidat.setIndicatif(request.getIndicatif());
        candidat.setOptions(request.getOptions());
        candidat.setAnticipeesSubies(request.getAnticipeesSubies());
        candidat.setAnticipeesCentre(request.getAnticipeesCentre());
        candidat.setAnticipeesAnnee(request.getAnticipeesAnnee());
        candidat.setAnticipeesLieu(request.getAnticipeesLieu());
        candidat.setAnticipeesNoteEcrit(request.getAnticipeesNoteEcrit());
        candidat.setAnticipeesNoteOral(request.getAnticipeesNoteOral());
        releve.setCandidat(candidat);

        // ---- 1er groupe (barème 580) ----
        List<NoteEpreuve> notesPremierGroupe = new ArrayList<>();
        int totalPremierGroupe = 0;
        for (Matiere matiere : MatieresF2.PREMIER_GROUPE) {
            Integer note = valeur(request.getNotesPremierGroupe(), matiere.getCode());
            NoteEpreuve ne = new NoteEpreuve(matiere.getCode(), note);
            int points = points(note, matiere.getCoefficient());
            ne.setPointsObtenus(points);
            notesPremierGroupe.add(ne);
            totalPremierGroupe += points;
        }
        releve.setNotesPremierGroupe(notesPremierGroupe);
        releve.setTotalPremierGroupe(totalPremierGroupe);
        releve.setReportPremierTotal(totalPremierGroupe);

        // ---- Dominantes déclarées ----
        if (request.getDominantes() != null) {
            Dominantes d = new Dominantes();
            d.setDominanteEcrit1(request.getDominantes().getDominanteEcrit1());
            d.setDominanteEcrit2(request.getDominantes().getDominanteEcrit2());
            d.setDominanteOral(request.getDominantes().getDominanteOral());
            releve.setDominantes(d);
        }

        // ---- Epreuves de contrôle (coefficient repris de la matière, règle d) ----
        List<EpreuveDeControle> controles = new ArrayList<>();
        if (request.getEpreuvesDeControle() != null) {
            for (ReleveF2SaisieRequest.EpreuveDeControleSaisie s : request.getEpreuvesDeControle()) {
                EpreuveDeControle c = new EpreuveDeControle();
                c.setMatiereCode(s.getMatiereCode());
                c.setRappelPointsObtenus1erGroupe(s.getRappelPointsObtenus1erGroupe());
                c.setNouvelleNoteSur20(s.getNouvelleNoteSur20());
                int rappel = defaut(s.getRappelPointsObtenus1erGroupe());
                int coeff = s.getMatiereCode() == null ? 0 : MatieresF2.findByCode(s.getMatiereCode()).getCoefficient();
                int nouveauxPoints = defaut(s.getNouvelleNoteSur20()) * coeff;
                c.setPointsAuControle(nouveauxPoints);
                c.setDifferenceEnPlus(Math.max(0, nouveauxPoints - rappel));
                controles.add(c);
            }
        }
        releve.setEpreuvesDeControle(controles);
        int totalDifferencesControle = controles.stream()
                .mapToInt(c -> defaut(c.getDifferenceEnPlus()))
                .sum();

        // ---- Education Physique (coeff. 1, moyenne 10) ----
        EducationPhysique ep = new EducationPhysique();
        if (request.getEducationPhysique() != null) {
            ep.setNote(request.getEducationPhysique().getNote());
            ep.setInapteOuControleAssidu(request.getEducationPhysique().getInapteOuControleAssidu());
        }
        int notePhysique = defaut(ep.getNote());
        int ecart = notePhysique - 10;
        boolean inapte = Boolean.TRUE.equals(ep.getInapteOuControleAssidu());
        ep.setPointsPositifs(inapte ? 0 : Math.max(0, ecart));
        ep.setPointsNegatifs(inapte ? 0 : Math.max(0, -ecart));
        releve.setEducationPhysique(ep);

        // ---- Epreuves facultatives ----
        List<EpreuveFacultative> facultatives = new ArrayList<>();
        int totalPointsFacultatifs = 0;
        if (request.getEpreuvesFacultatives() != null) {
            for (ReleveF2SaisieRequest.EpreuveFacultativeSaisie s : request.getEpreuvesFacultatives()) {
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

        // ---- Total définitif = report + différences de contrôle + éduc. physique ----
        int totalDefinitif = totalPremierGroupe + totalDifferencesControle
                + ep.getPointsPositifs() - ep.getPointsNegatifs();

        // ---- Mentions et décisions ----
        Mention mention1erGroupe = mention(totalPremierGroupe, MatieresF2.BAREME_PREMIER_GROUPE);
        releve.setMentionPremierGroupe(mention1erGroupe);
        releve.setDecisionPremierGroupe(decision(totalPremierGroupe, MatieresF2.BAREME_PREMIER_GROUPE));

        int totalDefinitifAvecFacultatifs = totalDefinitif;
        Mention mentionAvantBonus = mention(totalDefinitif, MatieresF2.BAREME_TOTAL_DEFINITIF);
        if (mentionAvantBonus == Mention.BIEN || mentionAvantBonus == Mention.TRES_BIEN) {
            totalDefinitifAvecFacultatifs += totalPointsFacultatifs;
        }
        releve.setTotalDefinitif(totalDefinitifAvecFacultatifs);
        releve.setMentionDeuxiemeGroupe(mention(totalDefinitifAvecFacultatifs, MatieresF2.BAREME_TOTAL_DEFINITIF));
        releve.setDecisionDeuxiemeGroupe(decision(totalDefinitifAvecFacultatifs, MatieresF2.BAREME_TOTAL_DEFINITIF));

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
