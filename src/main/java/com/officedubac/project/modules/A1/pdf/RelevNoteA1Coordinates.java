package com.officedubac.project.modules.A1.pdf;

/**
 * Coordonnées (x, y en points PDF, origine en bas à gauche) mesurées sur le
 * NOUVEAU gabarit "RELEVE DE NOTES - OPTION A1" (PDF vectoriel avec QR code,
 * page A4 : 595.32 x 841.92 pt) — remplace l'ancien gabarit scanné.
 *
 * Ce gabarit ajoute : N° de table, cases à cocher réelles pour la décision,
 * (N)/(F) identifiés comme nationalité/nombre de fois.
 *
 * Toutes les positions ont été vérifiées visuellement par superposition de
 * repères sur le gabarit (rendu PDF→PNG), y compris les lignes
 * "Fait à ... le ... 19..." dont le texte est fusionné par l'extraction PDF
 * (labels + pointillés sur une seule ligne). Le nom du Président du Jury
 * n'est volontairement pas imprimé sur ce gabarit : la ligne correspondante
 * est l'instruction de signature manuscrite elle-même, sans espace blanc
 * dédié à un nom pré-imprimé (voir les commentaires plus bas).
 */
public final class RelevNoteA1Coordinates {

    private RelevNoteA1Coordinates() { }

    // ---- Session : NORMALE / DE REMPLACEMENT (l'option non choisie est barrée) ----
    public static final float SESSION_NORMALE_X0 = 334f, SESSION_NORMALE_X1 = 391f, SESSION_NORMALE_Y = 802.5f;
    public static final float SESSION_REMPLACEMENT_X0 = 334f, SESSION_REMPLACEMENT_X1 = 441f, SESSION_REMPLACEMENT_Y = 790.3f;

    // ---- En-tête ----
    public static final float NUMERO_TABLE_X = 388f, NUMERO_TABLE_Y = 726.3f;
    public static final float JURY_NUMERO_X = 530f, JURY_NUMERO_Y = 766.4f;
    public static final float ANNEE_X       = 528f, ANNEE_Y       = 742.1f;

    // ---- Identité du candidat ----
    public static final float NOM_PRENOM_X       = 44f,  NOM_PRENOM_Y       = 684f;
    public static final float DATE_NAISSANCE_X   = 72f,  DATE_NAISSANCE_Y   = 659f;
    public static final float LIEU_NAISSANCE_X   = 210f, LIEU_NAISSANCE_Y   = 659f;
    public static final float ETABLISSEMENT_X    = 386f, ETABLISSEMENT_Y    = 684f;
    public static final float INDICATIF_X        = 493f, INDICATIF_Y        = 684f;
    public static final float OPTIONS_X          = 395f, OPTIONS_Y          = 659f;
    public static final float NATIONALITE_X      = 372f, NATIONALITE_Y      = 633f;
    public static final float NOMBRE_DE_FOIS_X   = 535f, NOMBRE_DE_FOIS_Y   = 633f;

    // ---- 1er groupe : colonnes centrées "Note sur 20" et "Points obtenus" ----
    public static final float G1_NOTE_CENTER_X   = 141f;
    public static final float G1_POINTS_CENTER_X = 207f;

    public static final float G1_FR_ECRIT_Y  = 553.3f;
    public static final float G1_FR_ORAL_Y   = 538.6f;
    public static final float G1_PHILO_Y     = 525.7f;
    public static final float G1_LATGREC_Y   = 511.4f;
    public static final float G1_HISTGEO_Y   = 497.3f;
    public static final float G1_LV_Y        = 481.0f;

    public static final float G1_TOTAL_Y = 463.3f; // "1er TOTAL"
    public static final float G2_TOTAL_Y = 341.9f; // "2ème TOTAL"

    // ---- 2eme groupe : colonnes centrées ----
    public static final float G2_NOTE_CENTER_X   = 416f;
    public static final float G2_POINTS_CENTER_X = 519f;

    public static final float REPORT_1ER_TOTAL_Y = 554.3f;
    public static final float G2_GRECLATIN_Y     = 540.1f;
    public static final float G2_MATHS_Y          = 527.2f;

    // ---- Epreuves orales de contrôle ----
    public static final float CTRL_MATIERE_X        = 272f;
    public static final float CTRL_RAPPEL_CENTER_X  = 383f;
    public static final float CTRL_NOUVELLE_CENTER_X = 416f;
    public static final float CTRL_POINTS_CENTER_X   = 480f;
    public static final float CTRL_DIFF_CENTER_X     = 519f;
    public static final float CTRL_FIRST_ROW_Y = 465f;
    public static final float CTRL_ROW_HEIGHT  = 15f;

    // ---- Epreuves facultatives (droite, alimentent le TOTAL DEFINITIF) ----
    public static final float FAC_LANGUE_X = 480f, FAC_LANGUE_Y = 425.2f;
    public static final float FAC_ARTS_X   = 467f, FAC_ARTS_Y   = 404.4f;

    // ---- Epreuve facultative + Education Physique (gauche, alimentent le 2ème TOTAL) ----
    public static final float FAC_EDUCPHYS_GAUCHE_X = 160f, FAC_EDUCPHYS_GAUCHE_Y = 387.2f;
    public static final float FAC_LANGUE_GAUCHE_X   = 186f, FAC_LANGUE_GAUCHE_Y   = 374.0f;
    public static final float FAC_ARTS_GAUCHE_X     = 181f, FAC_ARTS_GAUCHE_Y     = 360.0f;

    // ---- Total provisoire / Education Physique (droite) / Total définitif ----
    public static final float TOTAL_PROVISOIRE_X = 506f, TOTAL_PROVISOIRE_Y = 386.4f;

    public static final float EP_NOTE_X = 338f, EP_NOTE_Y = 362.1f;
    public static final float EP_POS_X  = 414f, EP_POS_Y  = 373.8f;
    public static final float EP_NEG_X  = 417f, EP_NEG_Y  = 359.8f;

    public static final float TOTAL_DEFINITIF_Y = 342.3f;

    // ---- Décisions du jury : 1er groupe (cases à cocher réelles) ----
    public static final float DEC1_MENTION_X = 156f, DEC1_MENTION_Y = 297.3f;
    public static final float DEC1_COCHE_ADMIS_CX    = 34.75f, DEC1_COCHE_ADMIS_CY    = 300.05f;
    public static final float DEC1_COCHE_AUTORISE_CX = 34.75f, DEC1_COCHE_AUTORISE_CY = 285.4f;
    public static final float DEC1_COCHE_AJOURNE_CX  = 34.75f, DEC1_COCHE_AJOURNE_CY  = 270.8f;

    // ---- Pied de page 1er groupe ----
    public static final float DEC1_LIEU_X = 65f,  DEC1_LIEU_Y = 253.2f;
    public static final float DEC1_JOUR_MOIS_X = 158f, DEC1_JOUR_MOIS_Y = 253.2f;
    public static final float DEC1_ANNEE2_X = 245f, DEC1_ANNEE2_Y = 253.2f; // 2 derniers chiffres, après "19"
    // NB : pas de champ Président du Jury sur ce pied de page — la ligne
    // "Cachet obligatoire Prénom (s), Nom et signature du Président du Jury."
    // est l'instruction de signature manuscrite elle-même, il n'y a pas de
    // ligne blanche dédiée à un nom pré-imprimé sur ce gabarit.

    // ---- Décisions du jury : 2eme groupe (cases à cocher réelles) ----
    public static final float DEC2_MENTION_X = 428f, DEC2_MENTION_Y = 297.3f;
    public static final float DEC2_COCHE_ADMIS_CX   = 269.65f, DEC2_COCHE_ADMIS_CY   = 300.05f;
    public static final float DEC2_COCHE_AJOURNE_CX = 269.65f, DEC2_COCHE_AJOURNE_CY = 285.4f;

    // ---- Pied de page 2eme groupe ----
    public static final float DEC2_LIEU_X = 291f, DEC2_LIEU_Y = 268.2f;
    public static final float DEC2_JOUR_MOIS_X = 405f, DEC2_JOUR_MOIS_Y = 268.2f;
    public static final float DEC2_ANNEE2_X = 552f, DEC2_ANNEE2_Y = 268.2f;
    // NB : pas de champ Président du Jury sur ce pied de page non plus,
    // même raison que le 1er groupe (voir ci-dessus).
}
