package com.officedubac.project.modules.A4.model;

public class EpreuveOraleControle {

    private String matiereChoisie;
    private Integer rappelPointsObtenus1erGroupe;
    private Integer nouvelleNoteSur20;
    private Integer pointsObtenusEpreuveControle;
    private Integer differenceEnPlus;

    public String getMatiereChoisie() { return matiereChoisie; }
    public void setMatiereChoisie(String matiereChoisie) { this.matiereChoisie = matiereChoisie; }

    public Integer getRappelPointsObtenus1erGroupe() { return rappelPointsObtenus1erGroupe; }
    public void setRappelPointsObtenus1erGroupe(Integer v) { this.rappelPointsObtenus1erGroupe = v; }

    public Integer getNouvelleNoteSur20() { return nouvelleNoteSur20; }
    public void setNouvelleNoteSur20(Integer nouvelleNoteSur20) { this.nouvelleNoteSur20 = nouvelleNoteSur20; }

    public Integer getPointsObtenusEpreuveControle() { return pointsObtenusEpreuveControle; }
    public void setPointsObtenusEpreuveControle(Integer v) { this.pointsObtenusEpreuveControle = v; }

    public Integer getDifferenceEnPlus() { return differenceEnPlus; }
    public void setDifferenceEnPlus(Integer differenceEnPlus) { this.differenceEnPlus = differenceEnPlus; }
}
