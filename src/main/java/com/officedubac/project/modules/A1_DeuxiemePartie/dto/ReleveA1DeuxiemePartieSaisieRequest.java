package com.officedubac.project.modules.A1_DeuxiemePartie.dto;

import java.time.LocalDate;
import java.util.Map;


public class ReleveA1DeuxiemePartieSaisieRequest {

    private String juryNumero;
    private String nomPrenom;
    private LocalDate dateNaissance;
    private String lieuNaissance;
    private String etablissement;
    private String indicatif;
    private String options;

    /** clé = code matière (cf. MatieresA1DeuxiemePartie.EPREUVES_ECRITES), valeur = note sur 20 */
    private Map<String, Integer> notesEcrites;

    /** clé = code matière (cf. MatieresA1DeuxiemePartie.EPREUVES_ORALES), valeur = note sur 20 */
    private Map<String, Integer> notesOrales;

    private String lieuDelivrance;
    private LocalDate dateDelivrance;
    private String presidentJury;

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

    public Map<String, Integer> getNotesEcrites() { return notesEcrites; }
    public void setNotesEcrites(Map<String, Integer> notesEcrites) { this.notesEcrites = notesEcrites; }

    public Map<String, Integer> getNotesOrales() { return notesOrales; }
    public void setNotesOrales(Map<String, Integer> notesOrales) { this.notesOrales = notesOrales; }

    public String getLieuDelivrance() { return lieuDelivrance; }
    public void setLieuDelivrance(String lieuDelivrance) { this.lieuDelivrance = lieuDelivrance; }

    public LocalDate getDateDelivrance() { return dateDelivrance; }
    public void setDateDelivrance(LocalDate dateDelivrance) { this.dateDelivrance = dateDelivrance; }

    public String getPresidentJury() { return presidentJury; }
    public void setPresidentJury(String presidentJury) { this.presidentJury = presidentJury; }
}
