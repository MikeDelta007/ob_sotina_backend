package com.officedubac.project.modules.A3_DeuxiemePartie.pdf;

/**
 * Coordonnées (x, y en points PDF, origine en bas à gauche) mesurées sur le
 * formulaire officiel "RELEVE DE NOTES - Série A3 - 2ème PARTIE - PREMIERE
 * SESSION" (page A4 : 595.276 x 841.890 pt).
 */
public final class ReleveA3DeuxiemePartieCoordinates {

    private ReleveA3DeuxiemePartieCoordinates() { }

    // ---- En-tête ----
    public static final float JURY_NUMERO_X = 500f, JURY_NUMERO_Y = 739f;

    // ---- Identité du candidat (formulaire sans Etab/Ind/Options/(N)/(F)) ----
    public static final float NOM_PRENOM_X     = 190f, NOM_PRENOM_Y     = 627f;
    public static final float DATE_NAISSANCE_X = 225f, DATE_NAISSANCE_Y = 601f;
    public static final float LIEU_NAISSANCE_X = 372f, LIEU_NAISSANCE_Y = 601f;

    // ---- Epreuves écrites : colonnes Note/20 (x) et Pts obte. (x) ----
    public static final float EC_NOTE_X   = 236f;
    public static final float EC_POINTS_X = 300f;

    public static final float EC_PHILO_Y   = 478f;
    public static final float EC_LV1_Y     = 444f;
    public static final float EC_HISTGEO_Y = 410f;
    public static final float EC_LV2_Y     = 376f;

    public static final float EC_TOTAL_POINTS_X = 300f, EC_TOTAL_Y = 310f;

    // ---- Epreuves orales : colonnes Note/20 (x) et Pts obte (x) ----
    public static final float OR_NOTE_X   = 429f;
    public static final float OR_POINTS_X = 495f;

    public static final float OR_LV1_Y   = 478f;
    public static final float OR_MATHS_Y = 444f;

    public static final float OR_TOTAL_POINTS_X = 495f, OR_TOTAL_Y = 310f;

    // ---- Décision (ADMIS / AJOURNE / 2me SESSION) : coche à gauche du mot choisi ----
    public static final float COCHE_ADMIS_X    = 332f, COCHE_ADMIS_Y    = 276f;
    public static final float COCHE_AJOURNE_X  = 393f, COCHE_AJOURNE_Y  = 276f;
    public static final float COCHE_2EME_SESSION_X = 462f, COCHE_2EME_SESSION_Y = 276f;

    // ---- Pied de page ----
    public static final float LIEU_DELIVRANCE_X = 290f, LIEU_DELIVRANCE_Y = 243f;
    public static final float DATE_DELIVRANCE_X = 405f, DATE_DELIVRANCE_Y = 243f;
    public static final float PRESIDENT_JURY_X  = 268f, PRESIDENT_JURY_Y  = 220f;
}
