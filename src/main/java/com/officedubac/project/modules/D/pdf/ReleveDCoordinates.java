package com.officedubac.project.modules.D.pdf;

/**
 * Coordonnées (x, y en points PDF, origine en bas à gauche) mesurées sur le
 * formulaire officiel "RELEVE DE NOTES - SERIE D" (page A4 :
 * 595.276 x 841.890 pt).
 */
public final class ReleveDCoordinates {

    private ReleveDCoordinates() { }

    // ---- En-tête ----
    public static final float JURY_NUMERO_X = 486f, JURY_NUMERO_Y = 745f;
    public static final float ANNEE_X       = 486f, ANNEE_Y       = 723f;

    // ---- Identité du candidat ----
    public static final float NOM_PRENOM_X       = 68f,  NOM_PRENOM_Y       = 681f;
    public static final float DATE_NAISSANCE_X   = 96f,  DATE_NAISSANCE_Y   = 662f;
    public static final float LIEU_NAISSANCE_X   = 205f, LIEU_NAISSANCE_Y   = 663f;
    public static final float ETABLISSEMENT_X    = 394f, ETABLISSEMENT_Y    = 681f;
    public static final float INDICATIF_X        = 488f, INDICATIF_Y        = 681f;
    public static final float OPTIONS_X          = 410f, OPTIONS_Y          = 660f;
    public static final float N_X                = 389f, N_Y                = 644f;
    public static final float F_X                = 489f, F_Y                = 644f;

    // ---- 1er groupe : colonnes "Note sur 20" (x) et "Pts obt." (x) ----
    public static final float G1_NOTE_X   = 183f;
    public static final float G1_POINTS_X = 228f;

    public static final float G1_FR_ECRIT_Y = 571f;
    public static final float G1_FR_ORAL_Y  = 559f;
    public static final float G1_PHILO_Y    = 549f;
    public static final float G1_MATH_Y     = 539f;
    public static final float G1_SCPHY_Y    = 528f;
    public static final float G1_SCNAT_Y    = 517f;
    public static final float G1_HISTGEO_Y  = 506f;
    public static final float G1_LV_Y       = 496f;

    public static final float G1_TOTAL_POINTS_X = 228f, G1_TOTAL_Y = 435f;   // "1er TOTAL"
    public static final float G2_TOTAL_POINTS_X = 228f, G2_TOTAL_Y = 387f;   // "2eme TOTAL"

    // ---- 2eme groupe (droite) ----
    public static final float REPORT_1ER_TOTAL_X = 507f, REPORT_1ER_TOTAL_Y = 576f;

    // ---- Epreuve de contrôle : une ligne par matière choisie (avec Cœff propre) ----
    public static final float CTRL_MATIERE_X  = 289f;
    public static final float CTRL_RAPPEL_X   = 395f;
    public static final float CTRL_NOUVELLE_X = 430f;
    public static final float CTRL_COEFF_X    = 453f;
    public static final float CTRL_POINTS_X   = 478f;
    public static final float CTRL_DIFF_X     = 502f;
    public static final float CTRL_FIRST_ROW_Y = 515f;
    public static final float CTRL_ROW_HEIGHT  = 15f;

    // ---- Education physique (bloc unique, à gauche, alimente 2eme TOTAL et TOTAL DEFINITIF) ----
    public static final float EP_NOTE_X = 90f,  EP_NOTE_Y = 465f;
    public static final float EP_POS_X  = 203f, EP_POS_Y  = 474f;
    public static final float EP_NEG_X  = 205f, EP_NEG_Y  = 453f;

    // ---- Epreuve facultative (gauche, alimente le 2eme TOTAL) ----
    public static final float FAC_G_LANGUE_X = 168f, FAC_G_LANGUE_Y = 418f;
    public static final float FAC_G_ARTS_X   = 213f, FAC_G_ARTS_Y   = 402f;

    // ---- Epreuves facultatives (droite, alimentent le TOTAL DEFINITIF) ----
    public static final float FAC_D_LANGUE_X = 432f, FAC_D_LANGUE_Y = 476f;
    public static final float FAC_D_ARTS_X   = 493f, FAC_D_ARTS_Y   = 461f;

    // ---- Total définitif ----
    public static final float TOTAL_DEFINITIF_X = 505f, TOTAL_DEFINITIF_Y = 445f;

    // ---- Décisions du jury : 1er groupe ----
    public static final float DEC1_MENTION_X = 160f, DEC1_MENTION_Y = 341f;
    public static final float DEC1_COCHE_ADMIS_X    = 44f, DEC1_COCHE_ADMIS_Y    = 340f;
    public static final float DEC1_COCHE_AUTORISE_X = 44f, DEC1_COCHE_AUTORISE_Y = 328f;
    public static final float DEC1_COCHE_AJOURNE_X  = 44f, DEC1_COCHE_AJOURNE_Y  = 317f;
    public static final float DEC1_LIEU_X = 88f,  DEC1_LIEU_Y = 305f;
    public static final float DEC1_DATE_X = 166f, DEC1_DATE_Y = 305f;
    public static final float DEC1_PRESIDENT_X = 58f, DEC1_PRESIDENT_Y = 266f;

    // ---- Décisions du jury : 2eme groupe ----
    public static final float DEC2_MENTION_X = 424f, DEC2_MENTION_Y = 400f;
    public static final float DEC2_COCHE_ADMIS_X   = 268f, DEC2_COCHE_ADMIS_Y   = 399f;
    public static final float DEC2_COCHE_AJOURNE_X = 268f, DEC2_COCHE_AJOURNE_Y = 384f;
    public static final float DEC2_LIEU_X = 321f, DEC2_LIEU_Y = 369f;
    public static final float DEC2_DATE_X = 433f, DEC2_DATE_Y = 369f;
    public static final float DEC2_PRESIDENT_X = 280f, DEC2_PRESIDENT_Y = 344f;
}
