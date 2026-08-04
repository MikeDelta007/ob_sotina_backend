package com.officedubac.project.modules.A3_DeuxiemePartie.dto;

import java.time.LocalDate;
import java.util.Map;

/**
 * Ce que le frontend envoie pour saisir/mettre à jour un relevé A3 2ème Partie.
 * Seules les notes brutes sont reçues : points, totaux et décision sont
 * recalculés côté serveur.
 */
public class ReleveA3DeuxiemePartieSaisieRequest {

    private String juryNumero;

    private String nomPrenom;
    private LocalDate dateNaissance;
    private String lieuNaissance;

    /** clé = code matière (cf. MatieresA3DeuxiemePartie.EPREUVES_ECRITES), valeur = note sur 20 */
    private Map<String, Integer> notesEcrites;

    /** clé = code matière (cf. MatieresA3DeuxiemePartie.EPREUVES_ORALES), valeur = note sur 20 */
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
