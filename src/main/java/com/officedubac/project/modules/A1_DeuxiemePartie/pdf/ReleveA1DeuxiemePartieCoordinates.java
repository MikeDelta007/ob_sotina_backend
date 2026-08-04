package com.officedubac.project.modules.A1_DeuxiemePartie.pdf;

/**
 * Coordonnées (x, y en points PDF, origine en bas à gauche) mesurées sur le
 * formulaire officiel "RELEVE DE NOTES - Série A1 - 2ème PARTIE - DEUXIEME
 * SESSION" (page A4 : 595.276 x 841.890 pt), pour le calage des valeurs
 * saisies par-dessus le scan utilisé comme fond de page.
 */
public final class ReleveA1DeuxiemePartieCoordinates {

    private ReleveA1DeuxiemePartieCoordinates() { }

    // ---- En-tête ----
    public static final float JURY_NUMERO_X = 438f, JURY_NUMERO_Y = 648f;

    // ---- Identité du candidat ----
    public static final float NOM_PRENOM_X     = 150f, NOM_PRENOM_Y     = 574f;
    public static final float DATE_NAISSANCE_X = 165f, DATE_NAISSANCE_Y = 529f;
    public static final float LIEU_NAISSANCE_X = 145f, LIEU_NAISSANCE_Y = 506f;
    public static final float ETABLISSEMENT_X  = 373f, ETABLISSEMENT_Y  = 564f;
    public static final float INDICATIF_X      = 446f, INDICATIF_Y      = 564f;
    public static final float OPTIONS_X        = 386f, OPTIONS_Y        = 539f;
    public static final float N_X              = 363f, N_Y              = 498f;
    public static final float F_X              = 441f, F_Y              = 498f;

    // ---- Epreuves écrites : colonnes Note/20 (x) et Pts obte. (x) ----
    public static final float EC_NOTE_X   = 195f;
    public static final float EC_POINTS_X = 260f;

    public static final float EC_PHILO_Y   = 396f;
    public static final float EC_LATAR_Y   = 365f;
    public static final float EC_GREC_Y    = 333f;
    public static final float EC_LV_Y      = 303f;

    public static final float EC_TOTAL_POINTS_X = 260f, EC_TOTAL_Y = 241f;

    // ---- Epreuves orales : colonnes Note/20 (x) et Pts obte (x) ----
    public static final float OR_NOTE_X   = 375f;
    public static final float OR_POINTS_X = 440f;

    public static final float OR_LATGRAR_Y = 397f;
    public static final float OR_HISTGEO_Y = 366f;
    public static final float OR_MATHS_Y   = 334f;

    public static final float OR_TOTAL_POINTS_X = 440f, OR_TOTAL_Y = 241f;

    // ---- Total général ----
    public static final float TOTAL_GENERAL_X = 428f, TOTAL_GENERAL_Y = 207f;

    // ---- Décision (ADMIS / AJOURNE) : coche à gauche du mot choisi ----
    public static final float COCHE_ADMIS_X  = 289f, COCHE_ADMIS_Y  = 168f;
    public static final float COCHE_AJOURNE_X = 355f, COCHE_AJOURNE_Y = 168f;

    // ---- Pied de page ----
    public static final float LIEU_DELIVRANCE_X = 245f, LIEU_DELIVRANCE_Y = 139f;
    public static final float DATE_DELIVRANCE_X = 365f, DATE_DELIVRANCE_Y = 139f;
    public static final float PRESIDENT_JURY_X  = 222f, PRESIDENT_JURY_Y  = 117f;
}
