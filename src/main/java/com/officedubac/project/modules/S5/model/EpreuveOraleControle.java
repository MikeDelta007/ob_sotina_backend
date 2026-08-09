package com.officedubac.project.modules.S5.model;

/**
 * Bloc "EPREUVE DE CONTROLE" du formulaire : (a) matière choisie par le
 * candidat, (b) rappel des points obtenus au 1er groupe, (c) nouvelle note
 * sur 20, (d) points obtenus à l'épreuve de contrôle = (c) x coefficient,
 * (e) différence en plus = max(0, (d) - (b)).
 */
public class EpreuveOraleControle {

    private String matiereChoisie;
    private Integer coefficient;
    private Integer rappelPointsObtenus1erGroupe;
    private Integer nouvelleNoteSur20;
    private Integer pointsObtenusEpreuveControle;
    private Integer differenceEnPlus;

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
