package com.officedubac.project.modules.A3.pdf;

/**
 * Coordonnées (x, y en points PDF, origine en bas à gauche) mesurées sur le
 * formulaire officiel "RELEVE DE NOTES - SERIE A3" (page A4 :
 * 595.276 x 841.890 pt), pour le calage des valeurs saisies par-dessus le
 * scan utilisé comme fond de page.
 */
public final class ReleveA3Coordinates {

    private ReleveA3Coordinates() { }

    // ---- En-tête ----
    public static final float JURY_NUMERO_X = 330f, JURY_NUMERO_Y = 735f;
    public static final float ANNEE_X       = 330f, ANNEE_Y       = 712f;

    // ---- Identité du candidat ----
    public static final float NOM_PRENOM_X       = 58f,  NOM_PRENOM_Y       = 652f;
    public static final float DATE_NAISSANCE_X   = 78f,  DATE_NAISSANCE_Y   = 628f;
    public static final float LIEU_NAISSANCE_X   = 233f, LIEU_NAISSANCE_Y   = 629f;
    public static final float ETABLISSEMENT_X    = 433f, ETABLISSEMENT_Y    = 652f;
    public static final float INDICATIF_X        = 515f, INDICATIF_Y        = 652f;
    public static final float OPTIONS_X          = 443f, OPTIONS_Y          = 627f;
    public static final float N_X                = 427f, N_Y                = 604f;
    public static final float F_X                = 512f, F_Y                = 604f;

    // ---- 1er groupe : colonne "Note sur 20" (x) et "Pts Obt." (x) ----
    public static final float G1_NOTE_X   = 222f;
    public static final float G1_POINTS_X = 270f;

    public static final float G1_FR_ECRIT_Y  = 546f;
    public static final float G1_FR_ORAL_Y   = 527f;
    public static final float G1_PHILO_Y     = 505f;
    public static final float G1_HISTGEO_Y   = 487f;
    public static final float G1_LV1_ECRIT_Y = 468f;
    public static final float G1_MATH_Y      = 447f;
    public static final float G1_LV2_Y       = 429f;
    public static final float G1_LV1_ORAL_Y  = 410f;

    public static final float G1_TOTAL_POINTS_X = 270f, G1_TOTAL_Y = 336f;   // "1er TOTAL"
    public static final float G2_TOTAL_POINTS_X = 270f, G2_TOTAL_Y = 281f;   // "2eme TOTAL"

    // ---- 2eme groupe (droite) ----
    public static final float REPORT_1ER_TOTAL_X = 530f, REPORT_1ER_TOTAL_Y = 545f;

    // ---- Epreuve de contrôle : une ligne par matière choisie ----
    public static final float CTRL_MATIERE_X  = 318f;
    public static final float CTRL_RAPPEL_X   = 418f;
    public static final float CTRL_NOUVELLE_X = 456f;
    public static final float CTRL_POINTS_X   = 503f;
    public static final float CTRL_DIFF_X     = 528f;
    public static final float CTRL_FIRST_ROW_Y = 455f;
    public static final float CTRL_ROW_HEIGHT  = 15f;

    // ---- Education physique ----
    public static final float EP_NOTE_X = 95f,  EP_NOTE_Y = 362f;
    public static final float EP_POS_X  = 215f, EP_POS_Y  = 372f;
    public static final float EP_NEG_X  = 217f, EP_NEG_Y  = 354f;

    // ---- Epreuves facultatives (droite, alimentent le TOTAL DEFINITIF) ----
    public static final float FAC_D_LANGUE_X = 440f, FAC_D_LANGUE_Y = 372f;
    public static final float FAC_D_ARTS_X   = 487f, FAC_D_ARTS_Y   = 354f;

    // ---- Epreuve facultative (gauche, alimente le 2eme TOTAL) ----
    public static final float FAC_G_LANGUE_X = 172f, FAC_G_LANGUE_Y = 314f;
    public static final float FAC_G_ARTS_X   = 215f, FAC_G_ARTS_Y   = 300f;

    // ---- Total définitif ----
    public static final float TOTAL_DEFINITIF_X = 530f, TOTAL_DEFINITIF_Y = 336f;

    // ---- Décisions du jury : 1er groupe ----
    public static final float DEC1_MENTION_X = 148f, DEC1_MENTION_Y = 226f;
    public static final float DEC1_COCHE_ADMIS_X    = 38f, DEC1_COCHE_ADMIS_Y    = 224f;
    public static final float DEC1_COCHE_AUTORISE_X = 38f, DEC1_COCHE_AUTORISE_Y = 205f;
    public static final float DEC1_COCHE_AJOURNE_X  = 38f, DEC1_COCHE_AJOURNE_Y  = 189f;
    public static final float DEC1_LIEU_X = 76f,  DEC1_LIEU_Y = 172f;
    public static final float DEC1_DATE_X = 180f, DEC1_DATE_Y = 171f;
    public static final float DEC1_PRESIDENT_X = 50f, DEC1_PRESIDENT_Y = 132f;

    // ---- Décisions du jury : 2eme groupe ----
    public static final float DEC2_MENTION_X = 455f, DEC2_MENTION_Y = 270f;
    public static final float DEC2_COCHE_ADMIS_X   = 311f, DEC2_COCHE_ADMIS_Y   = 269f;
    public static final float DEC2_COCHE_AJOURNE_X = 311f, DEC2_COCHE_AJOURNE_Y = 251f;
    public static final float DEC2_LIEU_X = 347f, DEC2_LIEU_Y = 225f;
    public static final float DEC2_DATE_X = 455f, DEC2_DATE_Y = 225f;
    public static final float DEC2_PRESIDENT_X = 323f, DEC2_PRESIDENT_Y = 190f;
}
