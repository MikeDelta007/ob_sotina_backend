package com.officedubac.project.modules.F2.model;

/**
 * Bloc "EPR. de CONTROLE" du formulaire F2 : (a) matière choisie parmi
 * celles subies à l'écrit, (b) rappel des points obtenus au 1er groupe,
 * (c) nouvelle note sur 20, (d) points obtenus à l'épreuve de contrôle,
 * (e) différence en plus.
 *
 * Règles imprimées sur le formulaire (encadré explicatif) :
 * - (d) le coefficient appliqué est le MEME qu'à l'écrit (pour le
 *   Français : écrit + oral) — ce module reprend donc automatiquement le
 *   coefficient de la matière via {@link MatieresF2}, il n'est pas ressaisi.
 * - (e) si la nouvelle note est égale ou inférieure à celle de l'écrit, le
 *   formulaire demande de porter un "DOUBLE ZERO" (pénalité), et pas
 *   seulement une différence nulle. Ce module calcule une différence à 0
 *   dans ce cas mais N'APPLIQUE PAS la pénalité de double-zéro sur la note
 *   écrite d'origine — à gérer manuellement par le jury si ce cas survient,
 *   car cela réviserait rétroactivement une note déjà entrée au 1er groupe.
 */
public class EpreuveDeControle {

    /** Code de la matière choisie, cf. {@link MatieresF2#PREMIER_GROUPE} */
    private String matiereCode;
    private Integer rappelPointsObtenus1erGroupe;
    private Integer nouvelleNoteSur20;
    private Integer pointsAuControle;   // = nouvelleNoteSur20 * coefficient (coefficient reprend celui de la matière)
    private Integer differenceEnPlus;   // = max(0, pointsAuControle - rappel)

    public String getMatiereCode() { return matiereCode; }
    public void setMatiereCode(String matiereCode) { this.matiereCode = matiereCode; }

    public Integer getRappelPointsObtenus1erGroupe() { return rappelPointsObtenus1erGroupe; }
    public void setRappelPointsObtenus1erGroupe(Integer v) { this.rappelPointsObtenus1erGroupe = v; }

    public Integer getNouvelleNoteSur20() { return nouvelleNoteSur20; }
    public void setNouvelleNoteSur20(Integer nouvelleNoteSur20) { this.nouvelleNoteSur20 = nouvelleNoteSur20; }

    public Integer getPointsAuControle() { return pointsAuControle; }
    public void setPointsAuControle(Integer pointsAuControle) { this.pointsAuControle = pointsAuControle; }

    public Integer getDifferenceEnPlus() { return differenceEnPlus; }
    public void setDifferenceEnPlus(Integer differenceEnPlus) { this.differenceEnPlus = differenceEnPlus; }
}
