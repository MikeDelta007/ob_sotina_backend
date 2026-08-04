package com.officedubac.project.modules.A1.model;

import java.time.LocalDate;

public class Candidat {

    private String nomPrenom;   // "M. ..................." sur le formulaire
    private LocalDate dateNaissance;
    private String lieuNaissance;
    private String etablissement; // Etab.
    private String indicatif;     // Ind.
    private String options;       // Options
    private String n;             // (N)
    private String f;             // (F)

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
}
