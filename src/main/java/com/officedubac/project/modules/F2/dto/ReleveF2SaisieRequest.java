package com.officedubac.project.modules.F2.dto;

import com.officedubac.project.modules.F2.model.Enums.TypeSession;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ReleveF2SaisieRequest {

    private TypeSession session;
    private String juryNumero;

    private String nomPrenom;
    private LocalDate dateNaissance;
    private String lieuNaissance;
    private String etablissement;
    private String indicatif;
    private String options;

    private String anticipeesSubies;
    private String anticipeesCentre;
    private String anticipeesAnnee;
    private String anticipeesLieu;
    private Integer anticipeesNoteEcrit;
    private Integer anticipeesNoteOral;

    /** clé = code matière (cf. MatieresF2.PREMIER_GROUPE), valeur = note sur 20 */
    private Map<String, Integer> notesPremierGroupe;

    private DominantesSaisie dominantes;
    private List<EpreuveDeControleSaisie> epreuvesDeControle;
    private List<EpreuveFacultativeSaisie> epreuvesFacultatives;
    private EducationPhysiqueSaisie educationPhysique;

    private String lieuDelivrance;
    private LocalDate dateDelivrance;
    private String presidentJury;

    public static class DominantesSaisie {
        private String dominanteEcrit1;
        private String dominanteEcrit2;
        private String dominanteOral;

        public String getDominanteEcrit1() { return dominanteEcrit1; }
        public void setDominanteEcrit1(String dominanteEcrit1) { this.dominanteEcrit1 = dominanteEcrit1; }
        public String getDominanteEcrit2() { return dominanteEcrit2; }
        public void setDominanteEcrit2(String dominanteEcrit2) { this.dominanteEcrit2 = dominanteEcrit2; }
        public String getDominanteOral() { return dominanteOral; }
        public void setDominanteOral(String dominanteOral) { this.dominanteOral = dominanteOral; }
    }

    public static class EpreuveDeControleSaisie {
        /** Code de la matière choisie (cf. MatieresF2.PREMIER_GROUPE) — le coefficient est repris automatiquement (règle d) */
        private String matiereCode;
        private Integer rappelPointsObtenus1erGroupe;
        private Integer nouvelleNoteSur20;

        public String getMatiereCode() { return matiereCode; }
        public void setMatiereCode(String matiereCode) { this.matiereCode = matiereCode; }
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

    public String getAnticipeesSubies() { return anticipeesSubies; }
    public void setAnticipeesSubies(String anticipeesSubies) { this.anticipeesSubies = anticipeesSubies; }

    public String getAnticipeesCentre() { return anticipeesCentre; }
    public void setAnticipeesCentre(String anticipeesCentre) { this.anticipeesCentre = anticipeesCentre; }

    public String getAnticipeesAnnee() { return anticipeesAnnee; }
    public void setAnticipeesAnnee(String anticipeesAnnee) { this.anticipeesAnnee = anticipeesAnnee; }

    public String getAnticipeesLieu() { return anticipeesLieu; }
    public void setAnticipeesLieu(String anticipeesLieu) { this.anticipeesLieu = anticipeesLieu; }

    public Integer getAnticipeesNoteEcrit() { return anticipeesNoteEcrit; }
    public void setAnticipeesNoteEcrit(Integer anticipeesNoteEcrit) { this.anticipeesNoteEcrit = anticipeesNoteEcrit; }

    public Integer getAnticipeesNoteOral() { return anticipeesNoteOral; }
    public void setAnticipeesNoteOral(Integer anticipeesNoteOral) { this.anticipeesNoteOral = anticipeesNoteOral; }

    public Map<String, Integer> getNotesPremierGroupe() { return notesPremierGroupe; }
    public void setNotesPremierGroupe(Map<String, Integer> notesPremierGroupe) { this.notesPremierGroupe = notesPremierGroupe; }

    public DominantesSaisie getDominantes() { return dominantes; }
    public void setDominantes(DominantesSaisie dominantes) { this.dominantes = dominantes; }

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
