package com.officedubac.project.modules.F2.model;

import java.time.LocalDate;

public class Candidat {

    private String nomPrenom;
    private LocalDate dateNaissance;
    private String lieuNaissance;
    private String etablissement;
    private String indicatif;
    private String options;
    private String n;
    private String f;

    // ---- Bloc "Anticipées" (candidats ayant subi certaines épreuves en avance) ----
    private String anticipeesSubies;   // matières subies par anticipation
    private String anticipeesCentre;   // centre où l'épreuve anticipée a été subie
    private String anticipeesAnnee;    // "en ......"
    private String anticipeesLieu;     // "de ......"
    private Integer anticipeesNoteEcrit;  // "E ="
    private Integer anticipeesNoteOral;   // "0 ="

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

    public String getN() { return n; }
    public void setN(String n) { this.n = n; }

    public String getF() { return f; }
    public void setF(String f) { this.f = f; }

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
}
