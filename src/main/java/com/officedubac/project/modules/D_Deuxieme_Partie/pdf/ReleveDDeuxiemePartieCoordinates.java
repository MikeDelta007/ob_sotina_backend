package com.officedubac.project.modules.D_Deuxieme_Partie.pdf;

/**
 * Coordonnées (x, y en points PDF, origine en bas à gauche) mesurées sur le
 * formulaire officiel "RELEVE DE NOTES - Série D - 2ème PARTIE - PREMIERE
 * SESSION" (page A4 : 595.276 x 841.890 pt).
 */
public final class ReleveDDeuxiemePartieCoordinates {

    private ReleveDDeuxiemePartieCoordinates() { }

    // ---- En-tête ----
    public static final float JURY_NUMERO_X = 408f, JURY_NUMERO_Y = 580f;

    // ---- Identité du candidat ----
    public static final float NOM_PRENOM_X       = 198f, NOM_PRENOM_Y       = 530f;
    public static final float DATE_NAISSANCE_X   = 220f, DATE_NAISSANCE_Y   = 497f;
    public static final float LIEU_NAISSANCE_X   = 192f, LIEU_NAISSANCE_Y   = 480f;
    public static final float ETABLISSEMENT_X    = 358f, ETABLISSEMENT_Y    = 517f;
    public static final float INDICATIF_X        = 410f, INDICATIF_Y        = 517f;
    public static final float OPTIONS_X          = 366f, OPTIONS_Y          = 500f;
    public static final float N_X                = 350f, N_Y                = 465f;
    public static final float F_X                = 407f, F_Y                = 468f;

    // ---- Epreuves écrites : colonnes Note/20 (x) et pts obte. (x) ----
    public static final float EC_NOTE_X   = 227f;
    public static final float EC_POINTS_X = 277f;

    public static final float EC_PHILO_Y = 396f;
    public static final float EC_MATH_Y  = 373f;
    public static final float EC_SCPHY_Y = 351f;
    public static final float EC_SCNAT_Y = 329f;

    public static final float EC_TOTAL_POINTS_X = 277f, EC_TOTAL_Y = 285f;

    // ---- Epreuves orales : colonnes Note/20 (x) et pts obte. (x) ----
    public static final float OR_NOTE_X   = 360f;
    public static final float OR_POINTS_X = 408f;

    public static final float OR_LV1_Y         = 396f;
    public static final float OR_HISTGEO_Y     = 374f;
    public static final float OR_SCPHYOUNAT_Y  = 353f;

    public static final float OR_TOTAL_POINTS_X = 408f, OR_TOTAL_Y = 286f;

    // ---- EPS (ajustement +/-, pas une matière notée avec barème propre) ----
    public static final float EPS_X = 392f, EPS_Y = 310f;

    // ---- Total général ----
    public static final float TOTAL_GENERAL_X = 390f, TOTAL_GENERAL_Y = 262f;

    // ---- Décision (ADMIS / AJOURNE) : coche à gauche du mot choisi ----
    public static final float COCHE_ADMIS_X   = 289f, COCHE_ADMIS_Y   = 237f;
    public static final float COCHE_AJOURNE_X = 340f, COCHE_AJOURNE_Y = 237f;

    // ---- Pied de page ----
    public static final float LIEU_DELIVRANCE_X = 267f, LIEU_DELIVRANCE_Y = 216f;
    public static final float DATE_DELIVRANCE_X = 347f, DATE_DELIVRANCE_Y = 215f;
    public static final float PRESIDENT_JURY_X  = 250f, PRESIDENT_JURY_Y  = 186f;
}
