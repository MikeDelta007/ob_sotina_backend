package com.officedubac.project.modules.F1.model;

/**
 * Bloc "EPR. DE CONTROLE" du formulaire F1 : Matières choisies / Rappel des
 * points obtenus au 1er groupe / Nouvelle note sur 20 / Cœff. / Points au
 * contrôle / Différence en plus.
 *
 * Comme B, D et E, ce formulaire a une colonne "Cœff" propre à l'épreuve de
 * contrôle : les points au contrôle = nouvelle note × coefficient.
 */
public class EpreuveDeControle {

    private String matiereChoisie;
    private Integer rappelPointsObtenus1erGroupe;
    private Integer nouvelleNoteSur20;
    private Integer coefficient;
    private Integer pointsAuControle;   // = nouvelleNoteSur20 * coefficient
    private Integer differenceEnPlus;   // = max(0, pointsAuControle - rappel)

    public String getMatiereChoisie() { return matiereChoisie; }
    public void setMatiereChoisie(String matiereChoisie) { this.matiereChoisie = matiereChoisie; }

    public Integer getRappelPointsObtenus1erGroupe() { return rappelPointsObtenus1erGroupe; }
    public void setRappelPointsObtenus1erGroupe(Integer v) { this.rappelPointsObtenus1erGroupe = v; }

    public Integer getNouvelleNoteSur20() { return nouvelleNoteSur20; }
    public void setNouvelleNoteSur20(Integer nouvelleNoteSur20) { this.nouvelleNoteSur20 = nouvelleNoteSur20; }

    public Integer getCoefficient() { return coefficient; }
    public void setCoefficient(Integer coefficient) { this.coefficient = coefficient; }

    public Integer getPointsAuControle() { return pointsAuControle; }
    public void setPointsAuControle(Integer pointsAuControle) { this.pointsAuControle = pointsAuControle; }

    public Integer getDifferenceEnPlus() { return differenceEnPlus; }
    public void setDifferenceEnPlus(Integer differenceEnPlus) { this.differenceEnPlus = differenceEnPlus; }
}
