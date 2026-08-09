package com.officedubac.project.modules.A1.model;

/**
 * Correspond au bloc "EPR. ORALES de CONTROLE" du formulaire :
 * colonnes (a) matière choisie, (b) rappel des points obtenus au 1er groupe,
 * (c) nouvelle note sur 20, (d) points obtenus à l'épreuve de contrôle,
 * (e) différence en plus.
 *
 * (d) = (c) × coefficient de la matière choisie (le coefficient n'est pas
 * imprimé sur le gabarit A1, mais il est nécessaire au calcul).
 */
public class EpreuveOraleControle {

    private String matiereChoisie;              // (a)
    private Integer coefficient;                 // coefficient de la matière choisie
    private Integer rappelPointsObtenus1erGroupe; // (b)
    private Integer nouvelleNoteSur20;            // (c)
    private Integer pointsObtenusEpreuveControle; // (d) = (c) * coefficient
    private Integer differenceEnPlus;             // (e) = max(0, (d) - (b))

    public String getMatiereChoisie() { return matiereChoisie; }
    public void setMatiereChoisie(String matiereChoisie) { this.matiereChoisie = matiereChoisie; }

    public Integer getCoefficient() { return coefficient; }
    public void setCoefficient(Integer coefficient) { this.coefficient = coefficient; }

    public Integer getRappelPointsObtenus1erGroupe() { return rappelPointsObtenus1erGroupe; }
    public void setRappelPointsObtenus1erGroupe(Integer v) { this.rappelPointsObtenus1erGroupe = v; }

    public Integer getNouvelleNoteSur20() { return nouvelleNoteSur20; }
    public void setNouvelleNoteSur20(Integer nouvelleNoteSur20) { this.nouvelleNoteSur20 = nouvelleNoteSur20; }

    public Integer getPointsObtenusEpreuveControle() { return pointsObtenusEpreuveControle; }
    public void setPointsObtenusEpreuveControle(Integer v) { this.pointsObtenusEpreuveControle = v; }

    public Integer getDifferenceEnPlus() { return differenceEnPlus; }
    public void setDifferenceEnPlus(Integer differenceEnPlus) { this.differenceEnPlus = differenceEnPlus; }
}
