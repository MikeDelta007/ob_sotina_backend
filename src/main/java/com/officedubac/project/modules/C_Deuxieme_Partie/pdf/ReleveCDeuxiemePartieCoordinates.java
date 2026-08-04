package com.officedubac.project.modules.C_Deuxieme_Partie.pdf;

/**
 * Coordonnées (x, y en points PDF, origine en bas à gauche) mesurées sur le
 * formulaire officiel "RELEVE DE NOTES - Série C - 2ème PARTIE - PREMIERE
 * SESSION". Page au format paysage (841.68 x 595.08 pt) — le contenu
 * scanné n'occupe que la moitié gauche de la page.
 */
public final class ReleveCDeuxiemePartieCoordinates {

    private ReleveCDeuxiemePartieCoordinates() { }

    // ---- En-tête ----
    public static final float JURY_NUMERO_X = 440f, JURY_NUMERO_Y = 657f;

    // ---- Identité du candidat ----
    public static final float NOM_PRENOM_X       = 120f, NOM_PRENOM_Y       = 580f;
    public static final float DATE_NAISSANCE_X   = 152f, DATE_NAISSANCE_Y   = 531f;
    public static final float LIEU_NAISSANCE_X   = 112f, LIEU_NAISSANCE_Y   = 509f;
    public static final float ETABLISSEMENT_X    = 354f, ETABLISSEMENT_Y    = 564f;
    public static final float INDICATIF_X        = 427f, INDICATIF_Y        = 566f;
    public static final float OPTIONS_X          = 368f, OPTIONS_Y          = 541f;
    public static final float N_X                = 342f, N_Y                = 494f;
    public static final float F_X                = 427f, F_Y                = 496f;

    // ---- Epreuves écrites : colonnes Note/20 (x) et Pts obte. (x) ----
    public static final float EC_NOTE_X   = 168f;
    public static final float EC_POINTS_X = 236f;

    public static final float EC_PHILO_Y = 388f;
    public static final float EC_MATH_Y  = 357f;
    public static final float EC_SCPHY_Y = 325f;
    public static final float EC_SCNAT_Y = 294f;

    public static final float EC_TOTAL_POINTS_X = 236f, EC_TOTAL_Y = 227f;

    // ---- Epreuves orales : colonnes Note/20 (x) et pts obte. (x) ----
    public static final float OR_NOTE_X   = 356f;
    public static final float OR_POINTS_X = 426f;

    public static final float OR_LV1_Y      = 392f;
    public static final float OR_HISTGEO_Y  = 360f;
    public static final float OR_MATH_Y     = 329f;

    public static final float OR_TOTAL_POINTS_X = 426f, OR_TOTAL_Y = 228f;

    // ---- EPS (ajustement +/-, pas une matière notée avec barème propre) ----
    public static final float EPS_X = 380f, EPS_Y = 265f;

    // ---- Total général ----
    public static final float TOTAL_GENERAL_X = 400f, TOTAL_GENERAL_Y = 194f;

    // ---- Décision (ADMIS / AJOURNE) : coche à gauche du mot choisi ----
    public static final float COCHE_ADMIS_X   = 255f, COCHE_ADMIS_Y   = 156f;
    public static final float COCHE_AJOURNE_X = 320f, COCHE_AJOURNE_Y = 156f;

    // ---- Pied de page ----
    public static final float LIEU_DELIVRANCE_X = 216f, LIEU_DELIVRANCE_Y = 126f;
    public static final float DATE_DELIVRANCE_X = 334f, DATE_DELIVRANCE_Y = 124f;
    public static final float PRESIDENT_JURY_X  = 190f, PRESIDENT_JURY_Y  = 90f;
}
