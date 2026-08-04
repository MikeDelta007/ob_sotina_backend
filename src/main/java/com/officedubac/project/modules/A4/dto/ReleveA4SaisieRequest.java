package com.officedubac.project.modules.A4.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ReleveA4SaisieRequest {

    private String juryNumero;
    private String centre;
    private String session;

    private String ligne1IdentiteEtablissement;
    private String ligne2Naissance;
    private String ligne3SerieOptions;
    private String ligne4Eaf;

    /** clé = code matière (cf. MatieresA4.PREMIER_GROUPE), valeur = note sur 20 */
    private Map<String, Integer> notesPremierGroupe;

    /** clé = code matière (cf. MatieresA4.DEUXIEME_GROUPE), valeur = note sur 20 */
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

    public String getJuryNumero() { return juryNumero; }
    public void setJuryNumero(String juryNumero) { this.juryNumero = juryNumero; }

    public String getCentre() { return centre; }
    public void setCentre(String centre) { this.centre = centre; }

    public String getSession() { return session; }
    public void setSession(String session) { this.session = session; }

    public String getLigne1IdentiteEtablissement() { return ligne1IdentiteEtablissement; }
    public void setLigne1IdentiteEtablissement(String v) { this.ligne1IdentiteEtablissement = v; }

    public String getLigne2Naissance() { return ligne2Naissance; }
    public void setLigne2Naissance(String ligne2Naissance) { this.ligne2Naissance = ligne2Naissance; }

    public String getLigne3SerieOptions() { return ligne3SerieOptions; }
    public void setLigne3SerieOptions(String ligne3SerieOptions) { this.ligne3SerieOptions = ligne3SerieOptions; }

    public String getLigne4Eaf() { return ligne4Eaf; }
    public void setLigne4Eaf(String ligne4Eaf) { this.ligne4Eaf = ligne4Eaf; }

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
