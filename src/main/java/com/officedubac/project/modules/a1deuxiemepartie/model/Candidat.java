package com.officedubac.project.modules.a1deuxiemepartie.model;

import java.time.LocalDate;

public class Candidat {

    private String nomPrenom;
    private LocalDate dateNaissance;
    private String lieuNaissance;
    private String etablissement;
    private String indicatif;
    private String options;

    /** N° de table (nouveau champ, imprimé en haut du formulaire) */
    private String numeroTable;

    /** (N) — nationalité du candidat */
    private String nationalite;

    /** (F) — nombre de fois que le candidat se présente à l'examen */
    private String nombreDeFois;

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

    public String getNumeroTable() { return numeroTable; }
    public void setNumeroTable(String numeroTable) { this.numeroTable = numeroTable; }

    public String getNationalite() { return nationalite; }
    public void setNationalite(String nationalite) { this.nationalite = nationalite; }

    public String getNombreDeFois() { return nombreDeFois; }
    public void setNombreDeFois(String nombreDeFois) { this.nombreDeFois = nombreDeFois; }
}
