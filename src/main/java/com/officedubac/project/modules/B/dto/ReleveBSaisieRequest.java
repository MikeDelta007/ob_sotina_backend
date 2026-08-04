package com.officedubac.project.modules.B.dto;

import com.officedubac.project.modules.B.model.Enums.TypeSession;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ReleveBSaisieRequest {

    private TypeSession session;
    private String juryNumero;
    private Integer annee;

    private String nomPrenom;
    private LocalDate dateNaissance;
    private String lieuNaissance;
    private String etablissement;
    private String indicatif;
    private String options;

    /** clé = code matière (cf. MatieresB.PREMIER_GROUPE), valeur = note sur 20 */
    private Map<String, Integer> notesPremierGroupe;

    private List<EpreuveDeControleSaisie> epreuvesDeControle;
    private List<EpreuveFacultativeSaisie> epreuvesFacultatives;
    private EducationPhysiqueSaisie educationPhysique;

    private String lieuDelivrance;
    private LocalDate dateDelivrance;
    private String presidentJury;

    public static class EpreuveDeControleSaisie {
        private String matiereChoisie;
        private Integer rappelPointsObtenus1erGroupe;
        private Integer nouvelleNoteSur20;
        private Integer coefficient;

        public String getMatiereChoisie() { return matiereChoisie; }
        public void setMatiereChoisie(String matiereChoisie) { this.matiereChoisie = matiereChoisie; }
        public Integer getRappelPointsObtenus1erGroupe() { return rappelPointsObtenus1erGroupe; }
        public void setRappelPointsObtenus1erGroupe(Integer v) { this.rappelPointsObtenus1erGroupe = v; }
        public Integer getNouvelleNoteSur20() { return nouvelleNoteSur20; }
        public void setNouvelleNoteSur20(Integer nouvelleNoteSur20) { this.nouvelleNoteSur20 = nouvelleNoteSur20; }
        public Integer getCoefficient() { return coefficient; }
        public void setCoefficient(Integer coefficient) { this.coefficient = coefficient; }
    }

    public static class EpreuveFacultativeSaisie {
        private String type; // LANGUE, DESSIN, COUTURE, MUSIQUE
        private Integer note;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Integer getNote() { return note; }
        public void setNote(Integer note) { this.note = note; }
    }

    public static class EducationPhysiqueSaisie {
        private Integer note;
        private Boolean inapteOuControleAssidu;

        public Integer getNote() { return note; }
        public void setNote(Integer note) { this.note = note; }
        public Boolean getInapteOuControleAssidu() { return inapteOuControleAssidu; }
        public void setInapteOuControleAssidu(Boolean v) { this.inapteOuControleAssidu = v; }
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

    public List<EpreuveDeControleSaisie> getEpreuvesDeControle() { return epreuvesDeControle; }
    public void setEpreuvesDeControle(List<EpreuveDeControleSaisie> epreuvesDeControle) { this.epreuvesDeControle = epreuvesDeControle; }

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
