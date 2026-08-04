package com.officedubac.project.modules.F1_Deuxieme_Partie.model;

public class Enums {

    public enum TypeEpreuve {
        ECRIT,
        ORAL
    }

    /**
     * Le formulaire propose trois issues : "ADMIS - AJOURNE - 2me SESSION"
     * (comme A3 2ème Partie).
     */
    public enum DecisionJury {
        ADMIS,
        AJOURNE,
        DEUXIEME_SESSION
    }
}
