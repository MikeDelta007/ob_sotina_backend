package com.officedubac.project.modules.A4.pdf;


public final class ReleveA4Coordinates {

    private ReleveA4Coordinates() { }

    // ---- En-tête ----
    public static final float JURY_NUMERO_X = 456f, JURY_NUMERO_Y = 744f;
    public static final float CENTRE_X      = 545f, CENTRE_Y      = 744f;
    public static final float SESSION_X     = 145f, SESSION_Y     = 696f;

    // ---- Identité du candidat (encadré libre, positions approximatives) ----
    public static final float LIGNE1_X = 300f, LIGNE1_Y = 695f;
    public static final float LIGNE2_X = 300f, LIGNE2_Y = 675f;
    public static final float LIGNE3_X = 300f, LIGNE3_Y = 655f;
    public static final float LIGNE4_X = 300f, LIGNE4_Y = 635f;

    // ---- 1er groupe : colonnes "Note sur 20" (x) et "Points obtenus" (x) ----
    public static final float G1_NOTE_X   = 148f;
    public static final float G1_POINTS_X = 200f;

    public static final float G1_FR_ECRIT_Y = 508f;
    public static final float G1_FR_ORAL_Y  = 486f;
    public static final float G1_PHILO_Y    = 461f;
    public static final float G1_LV1_Y      = 434f;
    public static final float G1_HISTGEO_Y  = 407f;
    public static final float G1_MATH_Y     = 380f;

    public static final float G1_TOTAL_POINTS_X = 200f, G1_TOTAL_Y = 362f;   // "1er TOTAL"
    public static final float G2_TOTAL_POINTS_X = 200f, G2_TOTAL_Y = 238f;   // "2e TOTAL"

    // ---- 2eme groupe (droite) ----
    public static final float REPORT_1ER_TOTAL_X = 495f, REPORT_1ER_TOTAL_Y = 508f;

    public static final float LV2_NOTE_X   = 408f, LV2_NOTE_Y   = 481f;
    public static final float LV2_POINTS_X = 489f, LV2_POINTS_Y = 481f;

    // ---- Epreuves orales de contrôle ----
    public static final float CTRL_MATIERE_X  = 260f;
    public static final float CTRL_RAPPEL_X   = 375f;
    public static final float CTRL_NOUVELLE_X = 408f;
    public static final float CTRL_POINTS_X   = 460f;
    public static final float CTRL_DIFF_X     = 490f;
    public static final float CTRL_FIRST_ROW_Y = 395f;
    public static final float CTRL_ROW_HEIGHT  = 15f;

    // ---- Bloc gauche (points bonus résumés, alimentent le 2e TOTAL) ----
    public static final float BONUS_EDUCPHYS_LEFT_X = 160f, BONUS_EDUCPHYS_LEFT_Y = 290f;
    public static final float BONUS_LANGUE_LEFT_X   = 160f, BONUS_LANGUE_LEFT_Y   = 272f;
    public static final float BONUS_ARTS_LEFT_X     = 160f, BONUS_ARTS_LEFT_Y     = 256f;

    // ---- Bloc droit (détail, alimente le TOTAL DEFINITIF) ----
    public static final float EP_NOTE_X = 310f, EP_NOTE_Y = 263f;
    public static final float EP_POS_X  = 432f, EP_POS_Y  = 276f;
    public static final float EP_NEG_X  = 427f, EP_NEG_Y  = 258f;

    public static final float FAC_LANGUE_X = 406f, FAC_LANGUE_Y = 338f;
    public static final float FAC_ARTS_X   = 483f, FAC_ARTS_Y   = 319f;

    public static final float TOTAL_PROVISOIRE_X = 420f, TOTAL_PROVISOIRE_Y = 299f;
    public static final float TOTAL_DEFINITIF_X  = 495f, TOTAL_DEFINITIF_Y  = 236f;

    // ---- Décisions du jury : 1er groupe ----
    public static final float DEC1_MENTION_X = 132f, DEC1_MENTION_Y = 168f;
    public static final float DEC1_COCHE_ADMIS_X    = 8f, DEC1_COCHE_ADMIS_Y    = 166f;
    public static final float DEC1_COCHE_AUTORISE_X = 8f, DEC1_COCHE_AUTORISE_Y = 143f;
    public static final float DEC1_COCHE_AJOURNE_X  = 8f, DEC1_COCHE_AJOURNE_Y  = 122f;
    public static final float DEC1_DATE_X = 124f, DEC1_DATE_Y = 100f;
    public static final float DEC1_PRESIDENT_X = 20f, DEC1_PRESIDENT_Y = 108f;

    // ---- Décisions du jury : 2eme groupe ----
    public static final float DEC2_MENTION_X = 375f, DEC2_MENTION_Y = 166f;
    public static final float DEC2_COCHE_ADMIS_X   = 245f, DEC2_COCHE_ADMIS_Y   = 165f;
    public static final float DEC2_COCHE_AJOURNE_X = 245f, DEC2_COCHE_AJOURNE_Y = 143f;
    public static final float DEC2_DATE_X = 357f, DEC2_DATE_Y = 99f;
    public static final float DEC2_PRESIDENT_X = 311f, DEC2_PRESIDENT_Y = 108f;
}
