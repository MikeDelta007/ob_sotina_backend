package com.officedubac.project.modules.F1.pdf;

/**
 * Coordonnées (x, y en points PDF, origine en bas à gauche) mesurées sur le
 * formulaire officiel "RELEVE DE NOTES - SERIE F1" (page A4 :
 * 595.276 x 841.890 pt).
 */
public final class ReleveF1Coordinates {

    private ReleveF1Coordinates() { }

    // ---- En-tête ----
    public static final float JURY_NUMERO_X = 457f, JURY_NUMERO_Y = 751f;
    public static final float ANNEE_X       = 453f, ANNEE_Y       = 735f;

    // ---- Identité du candidat ----
    public static final float NOM_PRENOM_X       = 33f,  NOM_PRENOM_Y       = 702f;
    public static final float DATE_NAISSANCE_X   = 62f,  DATE_NAISSANCE_Y   = 684f;
    public static final float LIEU_NAISSANCE_X   = 175f, LIEU_NAISSANCE_Y   = 684f;
    public static final float ETABLISSEMENT_X    = 360f, ETABLISSEMENT_Y    = 701f;
    public static final float INDICATIF_X        = 449f, INDICATIF_Y        = 701f;
    public static final float OPTIONS_X          = 371f, OPTIONS_Y          = 685f;
    public static final float N_X                = 353f, N_Y                = 669f;
    public static final float F_X                = 451f, F_Y                = 669f;

    // ---- 1er groupe : colonnes "Note sur 20" (x) et "Pts obt." (x) ----
    public static final float G1_NOTE_X   = 149f;
    public static final float G1_POINTS_X = 194f;

    public static final float G1_FR_ECRIT_Y    = 604f;
    public static final float G1_FR_ORAL_Y     = 589f;
    public static final float G1_MATH_Y        = 575f;
    public static final float G1_MECANIQUE_Y   = 559f;
    public static final float G1_CONSTRMECA_Y  = 543f;
    public static final float G1_ANALYSEFABR_Y = 526f;
    public static final float G1_ELECMETAL_Y   = 511f;
    public static final float G1_TECHNOAUTOM_Y = 495f;
    public static final float G1_ANGLAIS_Y     = 483f;
    public static final float G1_EPREUVEPRAT_Y = 471f;

    public static final float G1_TOTAL_POINTS_X = 194f, G1_TOTAL_Y = 416f;   // "1er TOTAL"
    public static final float G2_TOTAL_POINTS_X = 194f, G2_TOTAL_Y = 368f;   // "2eme TOTAL"

    // ---- 2eme groupe (droite) ----
    public static final float REPORT_1ER_TOTAL_X = 471f, REPORT_1ER_TOTAL_Y = 606f;

    // ---- Epreuve de contrôle : une ligne par matière choisie (avec Cœff propre) ----
    public static final float CTRL_MATIERE_X  = 254f;
    public static final float CTRL_RAPPEL_X   = 358f;
    public static final float CTRL_NOUVELLE_X = 396f;
    public static final float CTRL_COEFF_X    = 418f;
    public static final float CTRL_POINTS_X   = 443f;
    public static final float CTRL_DIFF_X     = 465f;
    public static final float CTRL_FIRST_ROW_Y = 553f;
    public static final float CTRL_ROW_HEIGHT  = 15f;

    // ---- Education physique (bloc unique, à gauche, alimente 2eme TOTAL et TOTAL DEFINITIF) ----
    public static final float EP_NOTE_X = 54f,  EP_NOTE_Y = 444f;
    public static final float EP_POS_X  = 164f, EP_POS_Y  = 454f;
    public static final float EP_NEG_X  = 170f, EP_NEG_Y  = 436f;

    // ---- Epreuve facultative (gauche, alimente le 2eme TOTAL) ----
    public static final float FAC_G_LANGUE_X = 133f, FAC_G_LANGUE_Y = 399f;
    public static final float FAC_G_ARTS_X   = 178f, FAC_G_ARTS_Y   = 384f;

    // ---- Epreuves facultatives (droite, alimentent le TOTAL DEFINITIF) ----
    public static final float FAC_D_LANGUE_X = 397f, FAC_D_LANGUE_Y = 489f;
    public static final float FAC_D_ARTS_X   = 458f, FAC_D_ARTS_Y   = 475f;

    // ---- Total définitif ----
    public static final float TOTAL_DEFINITIF_X = 471f, TOTAL_DEFINITIF_Y = 456f;

    // ---- Décisions du jury : 1er groupe ----
    public static final float DEC1_MENTION_X = 118f, DEC1_MENTION_Y = 329f;
    public static final float DEC1_COCHE_ADMIS_X    = 7f, DEC1_COCHE_ADMIS_Y    = 328f;
    public static final float DEC1_COCHE_AUTORISE_X = 7f, DEC1_COCHE_AUTORISE_Y = 317f;
    public static final float DEC1_COCHE_AJOURNE_X  = 7f, DEC1_COCHE_AJOURNE_Y  = 307f;
    public static final float DEC1_LIEU_X = 39f,  DEC1_LIEU_Y = 295f;
    public static final float DEC1_DATE_X = 100f, DEC1_DATE_Y = 295f;
    public static final float DEC1_PRESIDENT_X = 20f, DEC1_PRESIDENT_Y = 260f;

    // ---- Décisions du jury : 2eme groupe ----
    public static final float DEC2_MENTION_X = 388f, DEC2_MENTION_Y = 407f;
    public static final float DEC2_COCHE_ADMIS_X   = 232f, DEC2_COCHE_ADMIS_Y   = 406f;
    public static final float DEC2_COCHE_AJOURNE_X = 232f, DEC2_COCHE_AJOURNE_Y = 391f;
    public static final float DEC2_LIEU_X = 276f, DEC2_LIEU_Y = 377f;
    public static final float DEC2_DATE_X = 380f, DEC2_DATE_Y = 377f;
    public static final float DEC2_PRESIDENT_X = 243f, DEC2_PRESIDENT_Y = 356f;
}
