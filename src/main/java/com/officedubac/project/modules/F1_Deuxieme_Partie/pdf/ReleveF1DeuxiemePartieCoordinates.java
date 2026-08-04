package com.officedubac.project.modules.F1_Deuxieme_Partie.pdf;

/**
 * Coordonnées (x, y en points PDF, origine en bas à gauche) mesurées sur le
 * formulaire officiel "RELEVE DE NOTES - Série F1 - 2ème PARTIE - PREMIERE
 * SESSION" (page A4 : 595.276 x 841.890 pt).
 */
public final class ReleveF1DeuxiemePartieCoordinates {

    private ReleveF1DeuxiemePartieCoordinates() { }

    // ---- En-tête ----
    public static final float JURY_NUMERO_X = 504f, JURY_NUMERO_Y = 742f;

    // ---- Identité du candidat (formulaire sans Etab/Ind/Options/(N)/(F)) ----
    public static final float NOM_PRENOM_X       = 205f, NOM_PRENOM_Y       = 637f;
    public static final float DATE_NAISSANCE_X   = 242f, DATE_NAISSANCE_Y   = 612f;
    public static final float LIEU_NAISSANCE_X   = 380f, LIEU_NAISSANCE_Y   = 612f;

    // ---- Epreuves écrites : colonnes Note/20 (x) et Pts obte. (x) ----
    public static final float EC_NOTE_X   = 255f;
    public static final float EC_POINTS_X = 317f;

    public static final float EC_MATHS_Y   = 496f;
    public static final float EC_ELECMET_Y = 465f;
    public static final float EC_MECAN_Y   = 432f;
    public static final float EC_ETUDEPROJ_Y = 400f;
    public static final float EC_AFABR_Y   = 368f;

    public static final float EC_TOTAL_POINTS_X = 317f, EC_TOTAL_Y = 338f;

    // ---- Epreuves orales : colonnes Note/20 (x) et Pts obte. (x) ----
    public static final float OR_NOTE_X   = 437f;
    public static final float OR_POINTS_X = 500f;

    public static final float OR_AUTOM_Y   = 496f;
    public static final float OR_TECHNO_Y  = 465f;
    public static final float OR_LV_Y      = 432f;
    public static final float OR_ATELIER_Y = 400f;

    public static final float OR_TOTAL_POINTS_X = 500f, OR_TOTAL_Y = 338f;

    // ---- Décision (ADMIS / AJOURNE / 2me SESSION) : coche à gauche du mot choisi ----
    public static final float COCHE_ADMIS_X   = 339f, COCHE_ADMIS_Y   = 306f;
    public static final float COCHE_AJOURNE_X = 378f, COCHE_AJOURNE_Y = 306f;
    public static final float COCHE_2EME_SESSION_X = 432f, COCHE_2EME_SESSION_Y = 306f;

    // ---- Pied de page ----
    public static final float LIEU_DELIVRANCE_X = 302f, LIEU_DELIVRANCE_Y = 275f;
    public static final float DATE_DELIVRANCE_X = 411f, DATE_DELIVRANCE_Y = 275f;
    public static final float PRESIDENT_JURY_X  = 282f, PRESIDENT_JURY_Y  = 242f;
}
