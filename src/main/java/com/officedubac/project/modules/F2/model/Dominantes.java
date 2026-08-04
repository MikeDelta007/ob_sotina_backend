package com.officedubac.project.modules.F2.model;

/**
 * Bloc "Je soussigné, admis à subir le 2e Groupe d'épreuves déclare
 * choisir comme épreuve de contrôle les matières suivantes" : jusqu'à deux
 * dominantes à l'écrit de contrôle et une à l'oral.
 */
public class Dominantes {

    private String dominanteEcrit1;
    private String dominanteEcrit2;
    private String dominanteOral;

    public String getDominanteEcrit1() { return dominanteEcrit1; }
    public void setDominanteEcrit1(String dominanteEcrit1) { this.dominanteEcrit1 = dominanteEcrit1; }

    public String getDominanteEcrit2() { return dominanteEcrit2; }
    public void setDominanteEcrit2(String dominanteEcrit2) { this.dominanteEcrit2 = dominanteEcrit2; }

    public String getDominanteOral() { return dominanteOral; }
    public void setDominanteOral(String dominanteOral) { this.dominanteOral = dominanteOral; }
}
