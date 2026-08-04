package com.officedubac.project.modules.A3.service;

import com.officedubac.project.modules.A3.dto.ReleveA3SaisieRequest;
import com.officedubac.project.modules.A3.model.*;
import com.officedubac.project.modules.A3.model.Enums.DecisionJury;
import com.officedubac.project.modules.A3.model.Enums.Mention;
import com.officedubac.project.modules.A3.model.Enums.TypeFacultative;
import com.officedubac.project.modules.A3.repository.ReleveA3Repository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service métier : saisie des notes et calcul des totaux/mentions/décisions
 * selon les règles imprimées sur le formulaire officiel Série A3.
 *
 * Particularité A3 : le barème complet (480) est atteint dès le 1er groupe.
 * Le 2eme groupe (report + contrôle + éducation physique + facultatives)
 * ne fait qu'ajuster ce total, il ne comporte pas de matières écrites propres.
 */
@Service
public class ReleveA3Service {

    private final ReleveA3Repository repository;

    public ReleveA3Service(ReleveA3Repository repository) {
        this.repository = repository;
    }

    public ReleveA3 creer(ReleveA3SaisieRequest request) {
        ReleveA3 releve = new ReleveA3();
        appliquer(releve, request);
        return repository.save(releve);
    }

    public ReleveA3 mettreAJour(String id, ReleveA3SaisieRequest request) {
        ReleveA3 releve = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Relevé introuvable : " + id));
        appliquer(releve, request);
        return repository.save(releve);
    }

    public ReleveA3 obtenir(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Relevé introuvable : " + id));
    }

    private void appliquer(ReleveA3 releve, ReleveA3SaisieRequest request) {
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
        releve.setCandidat(candidat);

        // ---- 1er groupe (barème 480) ----
        List<NoteEpreuve> notesPremierGroupe = new ArrayList<>();
        int totalPremierGroupe = 0;
        for (Matiere matiere : MatieresA3.PREMIER_GROUPE) {
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

        // ---- Epreuves de contrôle ----
        List<EpreuveOraleControle> controles = new ArrayList<>();
        if (request.getEpreuvesDeControle() != null) {
            for (ReleveA3SaisieRequest.EpreuveDeControleSaisie s : request.getEpreuvesDeControle()) {
                EpreuveOraleControle c = new EpreuveOraleControle();
                c.setMatiereChoisie(s.getMatiereChoisie());
                c.setRappelPointsObtenus1erGroupe(s.getRappelPointsObtenus1erGroupe());
                c.setNouvelleNoteSur20(s.getNouvelleNoteSur20());
                int rappel = defaut(s.getRappelPointsObtenus1erGroupe());
                int nouveauxPoints = defaut(s.getNouvelleNoteSur20());
                c.setPointsObtenusEpreuveControle(nouveauxPoints);
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

        // ---- Epreuves facultatives : uniquement les points au-dessus de la moyenne ----
        List<EpreuveFacultative> facultatives = new ArrayList<>();
        int totalPointsFacultatifs = 0;
        if (request.getEpreuvesFacultatives() != null) {
            for (ReleveA3SaisieRequest.EpreuveFacultativeSaisie s : request.getEpreuvesFacultatives()) {
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
        Mention mention1erGroupe = mention(totalPremierGroupe, MatieresA3.BAREME_PREMIER_GROUPE);
        releve.setMentionPremierGroupe(mention1erGroupe);
        releve.setDecisionPremierGroupe(decision(totalPremierGroupe, MatieresA3.BAREME_PREMIER_GROUPE));

        // les points facultatifs ne comptent que pour les mentions BIEN / TRES BIEN
        int totalDefinitifAvecFacultatifs = totalDefinitif;
        Mention mentionAvantBonus = mention(totalDefinitif, MatieresA3.BAREME_TOTAL_DEFINITIF);
        if (mentionAvantBonus == Mention.BIEN || mentionAvantBonus == Mention.TRES_BIEN) {
            totalDefinitifAvecFacultatifs += totalPointsFacultatifs;
        }
        releve.setTotalDefinitif(totalDefinitifAvecFacultatifs);
        releve.setMentionDeuxiemeGroupe(mention(totalDefinitifAvecFacultatifs, MatieresA3.BAREME_TOTAL_DEFINITIF));
        releve.setDecisionDeuxiemeGroupe(decision(totalDefinitifAvecFacultatifs, MatieresA3.BAREME_TOTAL_DEFINITIF));

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
