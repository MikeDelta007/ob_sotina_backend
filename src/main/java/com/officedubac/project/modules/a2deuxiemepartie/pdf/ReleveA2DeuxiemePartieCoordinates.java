package com.officedubac.project.modules.a2deuxiemepartie.pdf;

/**
 * Coordonnées (x, y en points PDF, origine en bas à gauche) du gabarit
 * "RELEVE DE NOTES - Série A2 - 2ème PARTIE - DEUXIEME SESSION" (vectoriel,
 * QR code, page A4 : 595.32 x 841.92 pt).
 *
 * Ce gabarit est identique à celui d'A1 2ème Partie, décalé uniformément
 * de +2,0 pt en Y (X inchangés) — recalé sur les coordonnées d'A1 2ème
 * Partie (voir ReleveA1DeuxiemePartieCoordinates) après régénération du
 * gabarit source.
 */
public final class ReleveA2DeuxiemePartieCoordinates {

    private ReleveA2DeuxiemePartieCoordinates() { }

    // ---- En-tête ----
    public static final float JURY_NUMERO_X = 509f, JURY_NUMERO_Y = 750f;
    public static final float NUMERO_TABLE_X = 352f, NUMERO_TABLE_Y = 698f;

    // ---- Identité du candidat ----
    public static final float NOM_PRENOM_X       = 22f,  NOM_PRENOM_Y       = 617f;
    public static final float DATE_NAISSANCE_X   = 72f,  DATE_NAISSANCE_Y   = 589f;
    public static final float LIEU_NAISSANCE_X   = 210f, LIEU_NAISSANCE_Y   = 589f;
    public static final float ETABLISSEMENT_X    = 392f, ETABLISSEMENT_Y    = 617f;
    public static final float INDICATIF_X        = 497f, INDICATIF_Y        = 617f;
    public static final float OPTIONS_X          = 400f, OPTIONS_Y          = 589f;
    public static final float NATIONALITE_X      = 372f, NATIONALITE_Y      = 568f;
    public static final float NOMBRE_DE_FOIS_X   = 535f, NOMBRE_DE_FOIS_Y   = 568f;

    // ---- Epreuves écrites : colonnes centrées "Note/20" et "Pts obte." ----
    public static final float EC_NOTE_CENTER_X   = 118f;
    public static final float EC_POINTS_CENTER_X = 217f;

    public static final float EC_PHILO_Y   = 498f;
    public static final float EC_LV1_Y     = 477f;
    public static final float EC_HISTGEO_Y = 455f;
    public static final float EC_LATAR_Y   = 433f;

    public static final float EC_TOTAL_Y = 400f;

    // ---- Epreuves orales : colonnes centrées ----
    public static final float OR_NOTE_CENTER_X   = 413f;
    public static final float OR_POINTS_CENTER_X = 512f;

    public static final float OR_LV1_Y   = 498f;
    public static final float OR_LV2_Y   = 477f;
    public static final float OR_MATH_Y  = 455f;

    public static final float OR_TOTAL_Y = 400f;

    // ---- Total général (grande ligne) ----
    public static final float TOTAL_GENERAL_X = 466f, TOTAL_GENERAL_Y = 360f;

    // ---- Décision : tableau ADMIS | AJOURNE (cellule vérifiée par ses bordures exactes) ----
    public static final float COCHE_ADMIS_CX   = 267.45f, COCHE_ADMIS_CY   = 313f;
    public static final float COCHE_AJOURNE_CX = 327.95f, COCHE_AJOURNE_CY = 313f;

    // ---- Pied de page ----
    public static final float LIEU_DELIBERATION_X = 129f, LIEU_DELIBERATION_Y = 266f;
    public static final float JOUR_MOIS_X        = 270f, JOUR_MOIS_Y       = 266f;
    public static final float ANNEE_2_CHIFFRES_X = 480f, ANNEE_2_CHIFFRES_Y = 266f;

    // ---- Tampon "DAKAR, le [date du jour]" — imprimé juste en dessous de
    // "Cachet obligatoire" (lieu fixe + date système, indépendants du lieu/
    // date de délibération saisis ci-dessus). ----
    public static final float GENERE_X = 129f, GENERE_Y = 197f;

    // NB : le nom du Président du Jury n'est pas imprimé — la ligne
    // "Prénom (s), Nom et signature du Président du Jury" est l'instruction
    // de signature manuscrite elle-même (même choix que sur A1/A1 2ème Partie).
}
