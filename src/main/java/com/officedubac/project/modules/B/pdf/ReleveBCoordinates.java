package com.officedubac.project.modules.B.pdf;

/**
 * Coordonnées (x, y en points PDF, origine en bas à gauche) mesurées sur le
 * formulaire officiel "RELEVE DE NOTES - SERIE B" (page A4 :
 * 595.276 x 841.890 pt).
 */
public final class ReleveBCoordinates {

    private ReleveBCoordinates() { }

    // ---- En-tête ----
    public static final float JURY_NUMERO_X = 313f, JURY_NUMERO_Y = 733f;
    public static final float ANNEE_X       = 315f, ANNEE_Y       = 710f;

    // ---- Identité du candidat ----
    public static final float NOM_PRENOM_X       = 48f,  NOM_PRENOM_Y       = 650f;
    public static final float DATE_NAISSANCE_X   = 64f,  DATE_NAISSANCE_Y   = 625f;
    public static final float LIEU_NAISSANCE_X   = 222f, LIEU_NAISSANCE_Y   = 627f;
    public static final float ETABLISSEMENT_X    = 420f, ETABLISSEMENT_Y    = 650f;
    public static final float INDICATIF_X        = 503f, INDICATIF_Y        = 650f;
    public static final float OPTIONS_X          = 429f, OPTIONS_Y          = 625f;
    public static final float N_X                = 428f, N_Y                = 602f;
    public static final float F_X                = 498f, F_Y                = 602f;

    // ---- 1er groupe : colonnes "Note sur 20" (x) et "Pts. Obt." (x) ----
    public static final float G1_NOTE_X   = 210f;
    public static final float G1_POINTS_X = 258f;

    public static final float G1_FR_ECRIT_Y = 544f;
    public static final float G1_FR_ORAL_Y  = 525f;
    public static final float G1_PHILO_Y    = 504f;
    public static final float G1_HISTGEO_Y  = 485f;
    public static final float G1_SCIECO_Y   = 466f;
    public static final float G1_MATH_Y     = 446f;
    public static final float G1_LV1_Y      = 427f;
    public static final float G1_LV2_Y      = 409f;

    public static final float G1_TOTAL_POINTS_X = 258f, G1_TOTAL_Y = 334f;   // "1er TOTAL"
    public static final float G2_TOTAL_POINTS_X = 258f, G2_TOTAL_Y = 280f;   // "2eme TOTAL"

    // ---- 2eme groupe (droite) ----
    public static final float REPORT_1ER_TOTAL_X = 518f, REPORT_1ER_TOTAL_Y = 543f;

    // ---- Epreuve de contrôle : une ligne par matière choisie (avec Coeff propre) ----
    public static final float CTRL_MATIERE_X  = 307f;
    public static final float CTRL_RAPPEL_X   = 402f;
    public static final float CTRL_NOUVELLE_X = 440f;
    public static final float CTRL_COEFF_X    = 465f;
    public static final float CTRL_POINTS_X   = 489f;
    public static final float CTRL_DIFF_X     = 513f;
    public static final float CTRL_FIRST_ROW_Y = 460f;
    public static final float CTRL_ROW_HEIGHT  = 15f;

    // ---- Education physique (bloc unique, à gauche, alimente 2eme TOTAL et TOTAL DEFINITIF) ----
    public static final float EP_NOTE_X = 70f,  EP_NOTE_Y = 359f;
    public static final float EP_POS_X  = 202f, EP_POS_Y  = 369f;
    public static final float EP_NEG_X  = 202f, EP_NEG_Y  = 352f;

    // ---- Epreuve facultative (gauche, singulier, alimente le 2eme TOTAL) ----
    public static final float FAC_G_LANGUE_X = 161f, FAC_G_LANGUE_Y = 313f;
    public static final float FAC_G_ARTS_X   = 203f, FAC_G_ARTS_Y   = 299f;

    // ---- Epreuves facultatives (droite, pluriel, alimentent le TOTAL DEFINITIF) ----
    public static final float FAC_D_LANGUE_X = 427f, FAC_D_LANGUE_Y = 369f;
    public static final float FAC_D_ARTS_X   = 473f, FAC_D_ARTS_Y   = 352f;

    // ---- Total définitif ----
    public static final float TOTAL_DEFINITIF_X = 513f, TOTAL_DEFINITIF_Y = 334f;

    // ---- Décisions du jury : 1er groupe ----
    public static final float DEC1_MENTION_X = 130f, DEC1_MENTION_Y = 224f;
    public static final float DEC1_COCHE_ADMIS_X    = 25f, DEC1_COCHE_ADMIS_Y    = 223f;
    public static final float DEC1_COCHE_AUTORISE_X = 25f, DEC1_COCHE_AUTORISE_Y = 205f;
    public static final float DEC1_COCHE_AJOURNE_X  = 25f, DEC1_COCHE_AJOURNE_Y  = 188f;
    public static final float DEC1_LIEU_X = 78f,  DEC1_LIEU_Y = 172f;
    public static final float DEC1_DATE_X = 170f, DEC1_DATE_Y = 172f;
    public static final float DEC1_PRESIDENT_X = 40f, DEC1_PRESIDENT_Y = 132f;

    // ---- Décisions du jury : 2eme groupe ----
    public static final float DEC2_MENTION_X = 443f, DEC2_MENTION_Y = 269f;
    public static final float DEC2_COCHE_ADMIS_X   = 298f, DEC2_COCHE_ADMIS_Y   = 268f;
    public static final float DEC2_COCHE_AJOURNE_X = 298f, DEC2_COCHE_AJOURNE_Y = 251f;
    public static final float DEC2_LIEU_X = 334f, DEC2_LIEU_Y = 224f;
    public static final float DEC2_DATE_X = 441f, DEC2_DATE_Y = 224f;
    public static final float DEC2_PRESIDENT_X = 310f, DEC2_PRESIDENT_Y = 190f;
}
