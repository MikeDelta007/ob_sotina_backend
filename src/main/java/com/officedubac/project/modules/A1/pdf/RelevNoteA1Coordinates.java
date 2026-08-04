package com.officedubac.project.modules.A1.pdf;

/**
 * Coordonnées (x, y en points PDF, origine en bas à gauche) de chaque champ
 * à renseigner sur le formulaire officiel "RELEVE DE NOTES - OPTION A1".
 *
 * Ces valeurs ont été mesurées directement sur le PDF officiel fourni
 * (page A4 : 595.276 x 841.890 pt) à partir de la position exacte des
 * libellés imprimés, afin que le texte saisi vienne se caler dans les
 * champs vides du formulaire original utilisé comme fond de page.
 *
 * Si un nouveau scan légèrement différent est utilisé comme template,
 * ces coordonnées devront être réajustées.
 */
public final class RelevNoteA1Coordinates {

    private RelevNoteA1Coordinates() { }

    // ---- En-tête ----
    public static final float JURY_NUMERO_X = 552f,  JURY_NUMERO_Y = 725f;
    public static final float ANNEE_X       = 552f,  ANNEE_Y       = 698f;

    // ---- Identité du candidat ----
    public static final float NOM_PRENOM_X       = 50f,  NOM_PRENOM_Y       = 649f;
    public static final float DATE_NAISSANCE_X   = 82f,  DATE_NAISSANCE_Y   = 626f;
    public static final float LIEU_NAISSANCE_X   = 215f, LIEU_NAISSANCE_Y   = 627f;
    public static final float ETABLISSEMENT_X    = 440f, ETABLISSEMENT_Y    = 646f;
    public static final float INDICATIF_X        = 556f, INDICATIF_Y        = 646f;
    public static final float OPTIONS_X          = 460f, OPTIONS_Y          = 623f;
    public static final float N_X                = 434f, N_Y                = 602f;
    public static final float F_X                = 554f, F_Y                = 602f;

    // ---- 1er groupe : colonne "Note sur 20" (x) et "Points obtenus" (x) ----
    public static final float G1_NOTE_X   = 160f;
    public static final float G1_POINTS_X = 213f;

    public static final float G1_FR_ECRIT_Y  = 518f;
    public static final float G1_FR_ORAL_Y   = 497f;
    public static final float G1_PHILO_Y     = 475f;
    public static final float G1_LATGREC_Y   = 454f;
    public static final float G1_HISTGEO_Y   = 433f;
    public static final float G1_LV_Y        = 413f;

    public static final float G1_TOTAL_POINTS_X = 213f, G1_TOTAL_Y = 387f;   // "1er TOTAL"
    public static final float G2_TOTAL_POINTS_X = 213f, G2_TOTAL_Y = 237f;   // "2e TOTAL"

    // ---- 2eme groupe : report + colonne "Note sur 20" (x) et "Points obtenus" (x) ----
    public static final float REPORT_1ER_TOTAL_X = 535f, REPORT_1ER_TOTAL_Y = 512f;

    public static final float G2_NOTE_X   = 440f;
    public static final float G2_POINTS_X = 535f;
    public static final float G2_GRECLATIN_Y = 487f;
    public static final float G2_MATHS_Y     = 465f;

    // ---- Epreuves orales de contrôle (une ligne = un candidat au contrôle) ----
    public static final float CTRL_MATIERE_X = 283f;
    public static final float CTRL_RAPPEL_X  = 405f;
    public static final float CTRL_NOUVELLE_X = 444f;
    public static final float CTRL_POINTS_X   = 500f;
    public static final float CTRL_DIFF_X     = 530f;
    public static final float CTRL_FIRST_ROW_Y = 392f;
    public static final float CTRL_ROW_HEIGHT  = 15f;

    // ---- Epreuves facultatives ----
    public static final float FAC_LANGUE_X = 460f, FAC_LANGUE_Y = 341f;
    public static final float FAC_ARTS_X   = 522f, FAC_ARTS_Y   = 321f;

    // ---- Total provisoire / Education Physique / Total définitif ----
    public static final float TOTAL_PROVISOIRE_X = 520f, TOTAL_PROVISOIRE_Y = 301f;

    public static final float EP_NOTE_X       = 340f, EP_NOTE_Y       = 262f;
    public static final float EP_POS_X        = 460f, EP_POS_Y        = 278f;
    public static final float EP_NEG_X        = 460f, EP_NEG_Y        = 261f;

    public static final float TOTAL_DEFINITIF_X = 520f, TOTAL_DEFINITIF_Y = 238f;

    // ---- Décisions du jury : 1er groupe ----
    public static final float DEC1_MENTION_X = 140f, DEC1_MENTION_Y = 180f;
    public static final float DEC1_COCHE_ADMIS_X    = 8f,  DEC1_COCHE_ADMIS_Y    = 180f;
    public static final float DEC1_COCHE_AUTORISE_X = 8f,  DEC1_COCHE_AUTORISE_Y = 162f;
    public static final float DEC1_COCHE_AJOURNE_X  = 8f,  DEC1_COCHE_AJOURNE_Y  = 151f;
    public static final float DEC1_LIEU_X = 50f,  DEC1_LIEU_Y = 137f;
    public static final float DEC1_DATE_X = 145f, DEC1_DATE_Y = 133f;
    public static final float DEC1_PRESIDENT_X = 15f, DEC1_PRESIDENT_Y = 96f;

    // ---- Décisions du jury : 2eme groupe ----
    public static final float DEC2_MENTION_X = 460f, DEC2_MENTION_Y = 180f;
    public static final float DEC2_COCHE_ADMIS_X   = 278f, DEC2_COCHE_ADMIS_Y   = 180f;
    public static final float DEC2_COCHE_AJOURNE_X = 278f, DEC2_COCHE_AJOURNE_Y = 162f;
    public static final float DEC2_LIEU_X = 335f, DEC2_LIEU_Y = 144f;
    public static final float DEC2_DATE_X = 445f, DEC2_DATE_Y = 141f;
    public static final float DEC2_PRESIDENT_X = 285f, DEC2_PRESIDENT_Y = 96f;
}
