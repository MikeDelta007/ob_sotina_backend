package com.officedubac.project.modules.A3_DeuxiemePartie.model;

public class Enums {

    public enum TypeEpreuve {
        ECRIT,
        ORAL
    }

    /**
     * Le formulaire propose trois issues : "ADMIS - AJOURNE - 2me SESSION"
     * (le candidat peut être renvoyé à une seconde session au lieu d'être
     * simplement ajourné).
     */
    public enum DecisionJury {
        ADMIS,
        AJOURNE,
        DEUXIEME_SESSION
    }
}
