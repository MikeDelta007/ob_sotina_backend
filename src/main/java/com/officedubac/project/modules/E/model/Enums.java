package com.officedubac.project.modules.E.model;

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

    public enum Mention {
        AUCUNE,
        PASSABLE,
        ASSEZ_BIEN,
        BIEN,
        TRES_BIEN
    }

    public enum TypeSession {
        NORMALE,
        REMPLACEMENT
    }

    public enum TypeFacultative {
        LANGUE,
        DESSIN,
        COUTURE,
        MUSIQUE
    }
}
