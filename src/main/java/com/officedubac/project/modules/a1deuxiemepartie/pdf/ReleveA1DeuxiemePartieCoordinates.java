package com.officedubac.project.modules.a1deuxiemepartie.pdf;

/**
 * Coordonnées (x, y en points PDF, origine en bas à gauche) mesurées sur le
 * NOUVEAU gabarit "RELEVE DE NOTES - Série A1 - 2ème PARTIE - DEUXIEME
 * SESSION" (PDF vectoriel avec QR code, page A4 : 595.32 x 841.92 pt).
 *
 * Toutes les positions ont été vérifiées visuellement par superposition de
 * repères sur un rendu du gabarit avant livraison.
 *
 * Le nom du Président du Jury n'est volontairement pas imprimé : la ligne
 * "Prénom (s), Nom et signature du Président du Jury" est l'instruction de
 * signature manuscrite elle-même, sans espace blanc dédié à un nom
 * pré-imprimé (même choix que pour le module A1 1ère partie).
 */
public final class ReleveA1DeuxiemePartieCoordinates {

    private ReleveA1DeuxiemePartieCoordinates() { }

    // ---- En-tête ----
    public static final float JURY_NUMERO_X = 509f, JURY_NUMERO_Y = 759.4f;
    public static final float NUMERO_TABLE_X = 352f, NUMERO_TABLE_Y = 686.4f;

    // ---- Identité du candidat ----
    public static final float NOM_PRENOM_X       = 46f,  NOM_PRENOM_Y       = 637f;
    public static final float DATE_NAISSANCE_X   = 72f,  DATE_NAISSANCE_Y   = 611f;
    public static final float LIEU_NAISSANCE_X   = 210f, LIEU_NAISSANCE_Y   = 611f;
    public static final float ETABLISSEMENT_X    = 386f, ETABLISSEMENT_Y    = 637.4f;
    public static final float INDICATIF_X        = 493f, INDICATIF_Y        = 637.4f;
    public static final float OPTIONS_X          = 395f, OPTIONS_Y          = 611f;
    public static final float NATIONALITE_X      = 372f, NATIONALITE_Y      = 585.5f;
    public static final float NOMBRE_DE_FOIS_X   = 535f, NOMBRE_DE_FOIS_Y   = 585.5f;

    // ---- Epreuves écrites : colonnes centrées "Note/20" et "Pts obte." ----
    public static final float EC_NOTE_CENTER_X   = 118f;
    public static final float EC_POINTS_CENTER_X = 217f;

    public static final float EC_PHILO_Y   = 508.4f;
    public static final float EC_LATAR_Y   = 487.9f;
    public static final float EC_GREC_Y    = 467.2f;
    public static final float EC_LV_Y      = 446.6f;

    public static final float EC_TOTAL_Y = 407.4f;

    // ---- Epreuves orales : colonnes centrées ----
    public static final float OR_NOTE_CENTER_X   = 413f;
    public static final float OR_POINTS_CENTER_X = 512f;

    public static final float OR_LATGRECARABE_Y = 508.4f;
    public static final float OR_HISTGEO_Y      = 487.9f;
    public static final float OR_MATH_Y         = 467.2f;

    public static final float OR_TOTAL_Y = 407.4f;

    // ---- Total général (grande ligne) ----
    public static final float TOTAL_GENERAL_X = 466f, TOTAL_GENERAL_Y = 382.7f;

    // ---- Décision : tableau ADMIS | AJOURNE, croix dans la cellule choisie ----
    public static final float COCHE_ADMIS_CX   = 267.45f, COCHE_ADMIS_CY   = 329.55f;
    public static final float COCHE_AJOURNE_CX = 327.95f, COCHE_AJOURNE_CY = 329.55f;

    // ---- Pied de page ----
    public static final float LIEU_DELIVRANCE_X = 129f, LIEU_DELIVRANCE_Y = 286.7f;
    public static final float JOUR_MOIS_X        = 270f, JOUR_MOIS_Y       = 286.7f;
    public static final float ANNEE_2_CHIFFRES_X = 480f, ANNEE_2_CHIFFRES_Y = 286.7f;
}
