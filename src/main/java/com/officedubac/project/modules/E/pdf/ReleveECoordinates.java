package com.officedubac.project.modules.E.pdf;

/**
 * Coordonnées (x, y en points PDF, origine en bas à gauche) mesurées sur le
 * formulaire officiel "RELEVE DE NOTES - SERIE E" (page A4 :
 * 595.276 x 841.890 pt).
 */
public final class ReleveECoordinates {

    private ReleveECoordinates() { }

    // ---- En-tête ----
    public static final float JURY_NUMERO_X = 487f, JURY_NUMERO_Y = 766f;
    public static final float ANNEE_X       = 486f, ANNEE_Y       = 743f;

    // ---- Identité du candidat ----
    public static final float NOM_PRENOM_X       = 68f,  NOM_PRENOM_Y       = 701f;
    public static final float DATE_NAISSANCE_X   = 96f,  DATE_NAISSANCE_Y   = 682f;
    public static final float LIEU_NAISSANCE_X   = 205f, LIEU_NAISSANCE_Y   = 683f;
    public static final float ETABLISSEMENT_X    = 394f, ETABLISSEMENT_Y    = 701f;
    public static final float INDICATIF_X        = 488f, INDICATIF_Y        = 701f;
    public static final float OPTIONS_X          = 409f, OPTIONS_Y          = 681f;
    public static final float N_X                = 389f, N_Y                = 665f;
    public static final float F_X                = 487f, F_Y                = 665f;

    // ---- 1er groupe : colonnes "Note sur 20" (x) et "Pts obt." (x) ----
    public static final float G1_NOTE_X   = 183f;
    public static final float G1_POINTS_X = 228f;

    public static final float G1_FR_ECRIT_Y = 591f;
    public static final float G1_FR_ORAL_Y  = 579f;
    public static final float G1_PHILO_Y    = 570f;
    public static final float G1_MATH_Y     = 561f;
    public static final float G1_SCPHY_Y    = 551f;
    public static final float G1_CONSTRMECA_Y = 543f;
    public static final float G1_ANFABR_Y   = 535f;
    public static final float G1_TECHPRAT_Y = 519f;
    public static final float G1_LV_Y       = 511f;

    public static final float G1_TOTAL_POINTS_X = 228f, G1_TOTAL_Y = 455f;   // "1er TOTAL"
    public static final float G2_TOTAL_POINTS_X = 228f, G2_TOTAL_Y = 407f;   // "2eme TOTAL"

    // ---- 2eme groupe (droite) ----
    public static final float REPORT_1ER_TOTAL_X = 507f, REPORT_1ER_TOTAL_Y = 600f;

    // ---- Epreuve de contrôle : une ligne par matière choisie (avec Cœff propre) ----
    public static final float CTRL_MATIERE_X  = 289f;
    public static final float CTRL_RAPPEL_X   = 395f;
    public static final float CTRL_NOUVELLE_X = 432f;
    public static final float CTRL_COEFF_X    = 455f;
    public static final float CTRL_POINTS_X   = 478f;
    public static final float CTRL_DIFF_X     = 502f;
    public static final float CTRL_FIRST_ROW_Y = 540f;
    public static final float CTRL_ROW_HEIGHT  = 15f;

    // ---- Education physique (bloc unique, à gauche, alimente 2eme TOTAL et TOTAL DEFINITIF) ----
    public static final float EP_NOTE_X = 90f,  EP_NOTE_Y = 486f;
    public static final float EP_POS_X  = 200f, EP_POS_Y  = 494f;
    public static final float EP_NEG_X  = 206f, EP_NEG_Y  = 474f;

    // ---- Epreuve facultative (gauche, alimente le 2eme TOTAL) ----
    public static final float FAC_G_LANGUE_X = 169f, FAC_G_LANGUE_Y = 439f;
    public static final float FAC_G_ARTS_X   = 213f, FAC_G_ARTS_Y   = 424f;

    // ---- Epreuves facultatives (droite, alimentent le TOTAL DEFINITIF) ----
    public static final float FAC_D_LANGUE_X = 432f, FAC_D_LANGUE_Y = 497f;
    public static final float FAC_D_ARTS_X   = 494f, FAC_D_ARTS_Y   = 482f;

    // ---- Total définitif ----
    public static final float TOTAL_DEFINITIF_X = 505f, TOTAL_DEFINITIF_Y = 467f;

    // ---- Décisions du jury : 1er groupe ----
    public static final float DEC1_MENTION_X = 161f, DEC1_MENTION_Y = 361f;
    public static final float DEC1_COCHE_ADMIS_X    = 44f, DEC1_COCHE_ADMIS_Y    = 360f;
    public static final float DEC1_COCHE_AUTORISE_X = 44f, DEC1_COCHE_AUTORISE_Y = 348f;
    public static final float DEC1_COCHE_AJOURNE_X  = 44f, DEC1_COCHE_AJOURNE_Y  = 337f;
    public static final float DEC1_LIEU_X = 87f,  DEC1_LIEU_Y = 326f;
    public static final float DEC1_DATE_X = 165f, DEC1_DATE_Y = 326f;
    public static final float DEC1_PRESIDENT_X = 58f, DEC1_PRESIDENT_Y = 288f;

    // ---- Décisions du jury : 2eme groupe ----
    public static final float DEC2_MENTION_X = 424f, DEC2_MENTION_Y = 421f;
    public static final float DEC2_COCHE_ADMIS_X   = 269f, DEC2_COCHE_ADMIS_Y   = 420f;
    public static final float DEC2_COCHE_AJOURNE_X = 269f, DEC2_COCHE_AJOURNE_Y = 405f;
    public static final float DEC2_LIEU_X = 321f, DEC2_LIEU_Y = 390f;
    public static final float DEC2_DATE_X = 417f, DEC2_DATE_Y = 390f;
    public static final float DEC2_PRESIDENT_X = 280f, DEC2_PRESIDENT_Y = 365f;
}
