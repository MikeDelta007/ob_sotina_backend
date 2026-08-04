package com.officedubac.project.modules.A4.service;

import com.officedubac.project.modules.A4.dto.ReleveA4SaisieRequest;
import com.officedubac.project.modules.A4.model.*;
import com.officedubac.project.modules.A4.model.Enums.DecisionJury;
import com.officedubac.project.modules.A4.model.Enums.Mention;
import com.officedubac.project.modules.A4.model.Enums.TypeFacultative;
import com.officedubac.project.modules.A4.repository.ReleveA4Repository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service métier : saisie des notes et calcul des totaux/mentions/décisions
 * selon les règles imprimées sur le formulaire officiel Série A4.
 *
 * Différence notable avec A1/A3 : les points facultatifs et d'éducation
 * physique ne comptent que pour les mentions "ASSEZ BIEN et AU-DESSUS"
 * (contre "BIEN ou TRES BIEN" sur A1/A3).
 */
@Service
public class ReleveA4Service {

    private final ReleveA4Repository repository;

    public ReleveA4Service(ReleveA4Repository repository) {
        this.repository = repository;
    }

    public ReleveA4 creer(ReleveA4SaisieRequest request) {
        ReleveA4 releve = new ReleveA4();
        appliquer(releve, request);
        return repository.save(releve);
    }

    public ReleveA4 mettreAJour(String id, ReleveA4SaisieRequest request) {
        ReleveA4 releve = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Relevé introuvable : " + id));
        appliquer(releve, request);
        return repository.save(releve);
    }

    public ReleveA4 obtenir(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Relevé introuvable : " + id));
    }

    private void appliquer(ReleveA4 releve, ReleveA4SaisieRequest request) {
        releve.setJuryNumero(request.getJuryNumero());
        releve.setCentre(request.getCentre());
        releve.setSession(request.getSession());

        Candidat candidat = new Candidat();
        candidat.setLigne1IdentiteEtablissement(request.getLigne1IdentiteEtablissement());
        candidat.setLigne2Naissance(request.getLigne2Naissance());
        candidat.setLigne3SerieOptions(request.getLigne3SerieOptions());
        candidat.setLigne4Eaf(request.getLigne4Eaf());
        releve.setCandidat(candidat);

        // ---- 1er groupe ----
        List<NoteEpreuve> notesPremierGroupe = new ArrayList<>();
        int totalPremierGroupe = 0;
        for (Matiere matiere : MatieresA4.PREMIER_GROUPE) {
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

        // ---- 2eme groupe ----
        List<NoteEpreuve> notesDeuxiemeGroupe = new ArrayList<>();
        int totalDeuxiemeGroupe = 0;
        for (Matiere matiere : MatieresA4.DEUXIEME_GROUPE) {
            Integer note = valeur(request.getNotesDeuxiemeGroupe(), matiere.getCode());
            NoteEpreuve ne = new NoteEpreuve(matiere.getCode(), note);
            int points = points(note, matiere.getCoefficient());
            ne.setPointsObtenus(points);
            notesDeuxiemeGroupe.add(ne);
            totalDeuxiemeGroupe += points;
        }
        releve.setNotesDeuxiemeGroupe(notesDeuxiemeGroupe);

        // ---- Epreuves orales de contrôle ----
        List<EpreuveOraleControle> controles = new ArrayList<>();
        if (request.getEpreuvesOralesControle() != null) {
            for (ReleveA4SaisieRequest.EpreuveOraleControleSaisie s : request.getEpreuvesOralesControle()) {
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
        releve.setEpreuvesOralesControle(controles);
        int totalDifferencesControle = controles.stream()
                .mapToInt(c -> defaut(c.getDifferenceEnPlus()))
                .sum();

        // ---- Total provisoire = report 1er groupe + 2eme groupe + différences de contrôle ----
        int totalProvisoire = totalPremierGroupe + totalDeuxiemeGroupe + totalDifferencesControle;
        releve.setTotalProvisoire(totalProvisoire);

        // ---- Education Physique ----
        EducationPhysique ep = new EducationPhysique();
        if (request.getEducationPhysique() != null) {
            ep.setNote(request.getEducationPhysique().getNote());
        }
        int notePhysique = defaut(ep.getNote());
        int ecart = notePhysique - 10;
        ep.setPointsPositifs(Math.max(0, ecart));
        ep.setPointsNegatifs(Math.max(0, -ecart));
        releve.setEducationPhysique(ep);

        // ---- Epreuves facultatives ----
        List<EpreuveFacultative> facultatives = new ArrayList<>();
        int totalPointsFacultatifs = 0;
        if (request.getEpreuvesFacultatives() != null) {
            for (ReleveA4SaisieRequest.EpreuveFacultativeSaisie s : request.getEpreuvesFacultatives()) {
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

        // ---- Total définitif ----
        int totalDefinitif = totalProvisoire + ep.getPointsPositifs() - ep.getPointsNegatifs();

        // ---- Mentions et décisions ----
        Mention mention1erGroupe = mention(totalPremierGroupe, MatieresA4.BAREME_PREMIER_GROUPE);
        releve.setMentionPremierGroupe(mention1erGroupe);
        releve.setDecisionPremierGroupe(decision(totalPremierGroupe, MatieresA4.BAREME_PREMIER_GROUPE));

        // les points facultatifs ne comptent que pour les mentions ASSEZ BIEN et au-dessus
        int totalDefinitifAvecFacultatifs = totalDefinitif;
        Mention mentionAvantBonus = mention(totalDefinitif, MatieresA4.BAREME_TOTAL_DEFINITIF);
        if (mentionAvantBonus == Mention.ASSEZ_BIEN || mentionAvantBonus == Mention.BIEN || mentionAvantBonus == Mention.TRES_BIEN) {
            totalDefinitifAvecFacultatifs += totalPointsFacultatifs;
        }
        releve.setTotalDefinitif(totalDefinitifAvecFacultatifs);
        releve.setMentionDeuxiemeGroupe(mention(totalDefinitifAvecFacultatifs, MatieresA4.BAREME_TOTAL_DEFINITIF));
        releve.setDecisionDeuxiemeGroupe(decision(totalDefinitifAvecFacultatifs, MatieresA4.BAREME_TOTAL_DEFINITIF));

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
