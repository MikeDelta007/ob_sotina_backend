package com.officedubac.project.modules.F1_Deuxieme_Partie.model;

import java.time.LocalDate;

public class Candidat {

    private String nomPrenom;
    private LocalDate dateNaissance;
    private String lieuNaissance;

    public String getNomPrenom() { return nomPrenom; }
    public void setNomPrenom(String nomPrenom) { this.nomPrenom = nomPrenom; }

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public String getLieuNaissance() { return lieuNaissance; }
    public void setLieuNaissance(String lieuNaissance) { this.lieuNaissance = lieuNaissance; }
}
