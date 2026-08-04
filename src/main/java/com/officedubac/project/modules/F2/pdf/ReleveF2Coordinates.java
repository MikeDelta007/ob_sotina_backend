package com.officedubac.project.modules.F2.pdf;

/**
 * Coordonnées (x, y en points PDF, origine en bas à gauche) mesurées sur le
 * "CERTIFICAT PROCES-VERBAL D'EXAMEN - OPTION F2" (page A4 :
 * 595.276 x 841.890 pt).
 */
public final class ReleveF2Coordinates {

    private ReleveF2Coordinates() { }

    // ---- En-tête ----
    public static final float JURY_NUMERO_X = 491f, JURY_NUMERO_Y = 704f;

    // ---- Identité du candidat ----
    public static final float NOM_PRENOM_X       = 99f,  NOM_PRENOM_Y       = 639f;
    public static final float DATE_NAISSANCE_X   = 70f,  DATE_NAISSANCE_Y   = 624f;
    public static final float LIEU_NAISSANCE_X   = 199f, LIEU_NAISSANCE_Y   = 623f;
    public static final float ETABLISSEMENT_X    = 417f, ETABLISSEMENT_Y    = 631f;
    public static final float INDICATIF_X        = 499f, INDICATIF_Y        = 630f;
    public static final float OPTIONS_X          = 432f, OPTIONS_Y          = 611f;
    public static final float N_X                = 411f, N_Y                = 591f;
    public static final float F_X                = 490f, F_Y                = 591f;

    // ---- Bloc "Anticipées" ----
    public static final float ANTICIPEES_SUBIES_X = 112f, ANTICIPEES_SUBIES_Y = 608f;
    public static final float ANTICIPEES_CENTRE_X = 167f, ANTICIPEES_CENTRE_Y = 608f;
    public static final float ANTICIPEES_ANNEE_X  = 93f,  ANTICIPEES_ANNEE_Y  = 591f;
    public static final float ANTICIPEES_LIEU_X   = 157f, ANTICIPEES_LIEU_Y   = 591f;
    public static final float ANTICIPEES_E_X = 334f, ANTICIPEES_E_Y = 606f;
    public static final float ANTICIPEES_O_X = 334f, ANTICIPEES_O_Y = 588f;

    // ---- 1er groupe : colonnes "Note sur 20" (x) et "Pts obts" (x) ----
    public static final float G1_NOTE_X   = 192f;
    public static final float G1_POINTS_X = 240f;

    public static final float G1_FR_ECRIT_Y   = 528f;
    public static final float G1_FR_ORAL_Y    = 514f;
    public static final float G1_MATH_Y       = 493f;
    public static final float G1_ELECTROTECHNI_Y = 475f;
    public static final float G1_CONSTRELECMECA_Y = 459f;
    public static final float G1_SHEMAAUTOM_Y = 441f;
    public static final float G1_ETUDEEQUIP_Y = 425f;
    public static final float G1_ANGLAIS_Y    = 411f;
    public static final float G1_CABLAGE_Y    = 400f;
    public static final float G1_ESSAIS_Y     = 390f;

    public static final float G1_TOTAL_POINTS_X = 240f, G1_TOTAL_Y = 345f;   // "1er TOTAL"
    public static final float G2_TOTAL_POINTS_X = 240f, G2_TOTAL_Y = 243f;   // "2e TOTAL"

    // ---- 2eme groupe (droite) ----
    public static final float REPORT_1ER_TOTAL_X = 519f, REPORT_1ER_TOTAL_Y = 522f;

    // ---- Epreuve de contrôle : une ligne par matière choisie ----
    public static final float CTRL_MATIERE_X  = 293f;
    public static final float CTRL_RAPPEL_X   = 403f;
    public static final float CTRL_NOUVELLE_X = 442f;
    public static final float CTRL_POINTS_X   = 492f;
    public static final float CTRL_DIFF_X     = 515f;
    public static final float CTRL_FIRST_ROW_Y = 435f;
    public static final float CTRL_ROW_HEIGHT  = 15f;

    // ---- Education physique (bloc unique, à gauche) ----
    public static final float EP_NOTE_X = 64f,  EP_NOTE_Y = 371f;
    public static final float EP_POS_X  = 196f, EP_POS_Y  = 377f;
    public static final float EP_NEG_X  = 184f, EP_NEG_Y  = 361f;

    // ---- Epreuve facultative (gauche, alimente le 2e TOTAL) ----
    public static final float FAC_G_LANGUE_X = 137f, FAC_G_LANGUE_Y = 275f;
    public static final float FAC_G_ARTS_X   = 218f, FAC_G_ARTS_Y   = 258f;

    // ---- Epreuves facultatives (droite, alimentent le TOTAL DEFINITIF) ----
    public static final float FAC_D_LANGUE_X = 435f, FAC_D_LANGUE_Y = 371f;
    public static final float FAC_D_ARTS_X   = 501f, FAC_D_ARTS_Y   = 354f;

    // ---- Total définitif ----
    public static final float TOTAL_DEFINITIF_X = 519f, TOTAL_DEFINITIF_Y = 337f;

    // ---- Dominantes déclarées (2e groupe) ----
    public static final float DOM_ECRIT1_X = 25f, DOM_ECRIT1_Y = 117f;
    public static final float DOM_ECRIT2_X = 25f, DOM_ECRIT2_Y = 100f;
    public static final float DOM_ORAL_X   = 165f, DOM_ORAL_Y  = 97f;

    // ---- Déclaration / décision : 1er groupe (texte libre, pas de cases à cocher) ----
    public static final float DEC1_DECLARATION_X = 76f,  DEC1_DECLARATION_Y = 226f;
    public static final float DEC1_MENTION_X     = 63f,  DEC1_MENTION_Y     = 213f;
    public static final float DEC1_LIEU_X = 34f,  DEC1_LIEU_Y = 202f;
    public static final float DEC1_DATE_X = 143f, DEC1_DATE_Y = 200f;
    public static final float DEC1_PRESIDENT_X = 117f, DEC1_PRESIDENT_Y = 188f;

    // ---- Déclaration / décision : 2eme groupe ----
    public static final float DEC2_DECLARATION_X = 348f, DEC2_DECLARATION_Y = 322f;
    public static final float DEC2_MENTION_X     = 344f, DEC2_MENTION_Y     = 308f;
    public static final float DEC2_LIEU_X = 306f, DEC2_LIEU_Y = 296f;
    public static final float DEC2_DATE_X = 430f, DEC2_DATE_Y = 293f;
    public static final float DEC2_PRESIDENT_X = 378f, DEC2_PRESIDENT_Y = 281f;
}
