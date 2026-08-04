package com.officedubac.project.modules.A1.dto;


import com.officedubac.project.modules.A1.model.Enums.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Ce que le frontend (react-hook-form) envoie pour saisir/mettre à jour un relevé.
 * Le backend ne reçoit QUE les notes brutes saisies par l'utilisateur ;
 * tous les points, totaux, mentions et décisions sont recalculés côté serveur
 * pour éviter toute incohérence avec le PDF officiel.
 */
public class RelevNoteA1SaisieRequest {

    private TypeSession session;
    private String juryNumero;
    private Integer annee;

    private String nomPrenom;
    private LocalDate dateNaissance;
    private String lieuNaissance;
    private String etablissement;
    private String indicatif;
    private String options;

    /** Notes du 1er groupe : clé = code matière (cf. MatieresA1), valeur = note sur 20 */
    private Map<String, Integer> notesPremierGroupe;

    /** Notes du 2eme groupe : clé = code matière, valeur = note sur 20 */
    private Map<String, Integer> notesDeuxiemeGroupe;

    private List<EpreuveOraleControleSaisie> epreuvesOralesControle;
    private List<EpreuveFacultativeSaisie> epreuvesFacultatives;
    private EducationPhysiqueSaisie educationPhysique;

    private String lieuDelivrance;
    private LocalDate dateDelivrance;
    private String presidentJury;

    public static class EpreuveOraleControleSaisie {
        private String matiereChoisie;
        private Integer rappelPointsObtenus1erGroupe;
        private Integer nouvelleNoteSur20;

        public String getMatiereChoisie() { return matiereChoisie; }
        public void setMatiereChoisie(String matiereChoisie) { this.matiereChoisie = matiereChoisie; }
        public Integer getRappelPointsObtenus1erGroupe() { return rappelPointsObtenus1erGroupe; }
        public void setRappelPointsObtenus1erGroupe(Integer v) { this.rappelPointsObtenus1erGroupe = v; }
        public Integer getNouvelleNoteSur20() { return nouvelleNoteSur20; }
        public void setNouvelleNoteSur20(Integer nouvelleNoteSur20) { this.nouvelleNoteSur20 = nouvelleNoteSur20; }
    }

    public static class EpreuveFacultativeSaisie {
        private String type; // LANGUE, DESSIN, MUSIQUE, COUTURE
        private Integer note;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Integer getNote() { return note; }
        public void setNote(Integer note) { this.note = note; }
    }

    public static class EducationPhysiqueSaisie {
        private Integer note;

        public Integer getNote() { return note; }
        public void setNote(Integer note) { this.note = note; }
    }

    // ---- Getters / Setters ----

    public TypeSession getSession() { return session; }
    public void setSession(TypeSession session) { this.session = session; }

    public String getJuryNumero() { return juryNumero; }
    public void setJuryNumero(String juryNumero) { this.juryNumero = juryNumero; }

    public Integer getAnnee() { return annee; }
    public void setAnnee(Integer annee) { this.annee = annee; }

    public String getNomPrenom() { return nomPrenom; }
    public void setNomPrenom(String nomPrenom) { this.nomPrenom = nomPrenom; }

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public String getLieuNaissance() { return lieuNaissance; }
    public void setLieuNaissance(String lieuNaissance) { this.lieuNaissance = lieuNaissance; }

    public String getEtablissement() { return etablissement; }
    public void setEtablissement(String etablissement) { this.etablissement = etablissement; }

    public String getIndicatif() { return indicatif; }
    public void setIndicatif(String indicatif) { this.indicatif = indicatif; }

    public String getOptions() { return options; }
    public void setOptions(String options) { this.options = options; }

    public Map<String, Integer> getNotesPremierGroupe() { return notesPremierGroupe; }
    public void setNotesPremierGroupe(Map<String, Integer> notesPremierGroupe) { this.notesPremierGroupe = notesPremierGroupe; }

    public Map<String, Integer> getNotesDeuxiemeGroupe() { return notesDeuxiemeGroupe; }
    public void setNotesDeuxiemeGroupe(Map<String, Integer> notesDeuxiemeGroupe) { this.notesDeuxiemeGroupe = notesDeuxiemeGroupe; }

    public List<EpreuveOraleControleSaisie> getEpreuvesOralesControle() { return epreuvesOralesControle; }
    public void setEpreuvesOralesControle(List<EpreuveOraleControleSaisie> epreuvesOralesControle) { this.epreuvesOralesControle = epreuvesOralesControle; }

    public List<EpreuveFacultativeSaisie> getEpreuvesFacultatives() { return epreuvesFacultatives; }
    public void setEpreuvesFacultatives(List<EpreuveFacultativeSaisie> epreuvesFacultatives) { this.epreuvesFacultatives = epreuvesFacultatives; }

    public EducationPhysiqueSaisie getEducationPhysique() { return educationPhysique; }
    public void setEducationPhysique(EducationPhysiqueSaisie educationPhysique) { this.educationPhysique = educationPhysique; }

    public String getLieuDelivrance() { return lieuDelivrance; }
    public void setLieuDelivrance(String lieuDelivrance) { this.lieuDelivrance = lieuDelivrance; }

    public LocalDate getDateDelivrance() { return dateDelivrance; }
    public void setDateDelivrance(LocalDate dateDelivrance) { this.dateDelivrance = dateDelivrance; }

    public String getPresidentJury() { return presidentJury; }
    public void setPresidentJury(String presidentJury) { this.presidentJury = presidentJury; }
}
