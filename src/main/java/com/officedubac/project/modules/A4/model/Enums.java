package com.officedubac.project.modules.A4.model;

public class Enums {

    public enum TypeEpreuve {
        ECRIT,
        ORAL
    }

    public enum DecisionJury {
        ADMIS,
        AUTORISE_SECOND_GROUPE,
        AJOURNE
    }

    /**
     * Sur ce formulaire, les points facultatifs et d'éducation physique ne
     * comptent que pour les mentions "ASSEZ BIEN et AU-DESSUS" (contre "BIEN
     * ou TRES BIEN" sur les formulaires A1/A3).
     */
    public enum Mention {
        AUCUNE,
        PASSABLE,
        ASSEZ_BIEN,
        BIEN,
        TRES_BIEN
    }

    public enum TypeFacultative {
        LANGUE,
        DESSIN,
        MUSIQUE,
        COUTURE
    }
}
