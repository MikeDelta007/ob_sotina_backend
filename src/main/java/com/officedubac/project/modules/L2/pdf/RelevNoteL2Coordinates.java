package com.officedubac.project.modules.L2.pdf;

/**
 * Coordonnées (x, y en points PDF, origine en bas à gauche) mesurées par
 * extraction du texte du gabarit officiel "releve-L2-template.pdf"
 * (page A4 : 595.32 x 841.92 pt). Positions approximatives dans les zones à
 * espacement variable (champs en pointillés) : à ajuster visuellement si besoin.
 */
public final class RelevNoteL2Coordinates {

    private RelevNoteL2Coordinates() { }

    // ---- Session : NORMALE / DE REMPLACEMENT (l'option non choisie est barrée) ----
    public static final float SESSION_NORMALE_X0 = 185.2f, SESSION_NORMALE_X1 = 225.2f, SESSION_NORMALE_Y = 710.9f;
    public static final float SESSION_REMPLACEMENT_X0 = 185.2f, SESSION_REMPLACEMENT_X1 = 267.2f, SESSION_REMPLACEMENT_Y = 698.6f;

    // ---- En-tête ----
    public static final float JURY_NUMERO_X = 370.3f, JURY_NUMERO_Y = 714.2f;
    public static final float ANNEE_X = 380.3f, ANNEE_Y = 696.0f;
    public static final float NUMERO_TABLE_X = 110.4f, NUMERO_TABLE_Y = 674.0f;

    // ---- Identité du candidat ----
    public static final float NOM_PRENOM_X = 44.3f, NOM_PRENOM_Y = 632.4f;
    public static final float DATE_NAISSANCE_X = 74.3f, DATE_NAISSANCE_Y = 606.8f;
    public static final float LIEU_NAISSANCE_X = 224.3f, LIEU_NAISSANCE_Y = 606.8f;
    public static final float ETABLISSEMENT_X = 388.2f, ETABLISSEMENT_Y = 632.4f;
    public static final float INDICATIF_X = 518.2f, INDICATIF_Y = 632.4f;
    public static final float OPTIONS_X = 398.2f, OPTIONS_Y = 606.8f;
    public static final float NATIONALITE_X = 375.2f, NATIONALITE_Y = 581.3f;
    public static final float NOMBRE_DE_FOIS_X = 501.9f, NOMBRE_DE_FOIS_Y = 581.3f;

    // ---- 1er groupe : colonnes centrées "Note sur 20" et "Points obtenus" ----
    public static final float G1_NOTE_CENTER_X = 148.8f;
    public static final float G1_POINTS_CENTER_X = 209.2f;
    public static final float[] G1_ROW_Y = { 521.1f, 506.6f, 492.9f, 477.3f, 462.7f, 448.9f, 435.1f };

    public static final float G1_TOTAL_Y = 376.6f;
    public static final float G2_TOTAL_Y = 319.0f;

    // ---- 2eme groupe : colonnes centrées ----
    public static final float G2_NOTE_CENTER_X = 410.6f;
    public static final float G2_POINTS_CENTER_X = 510.6f;
    public static final float[] G2_ROW_Y = {  };

    public static final float REPORT_1ER_TOTAL_Y = 519.5f;

    // ---- Epreuve de contrôle ----
    public static final float CTRL_MATIERE_X = 275.7f;
    public static final float CTRL_RAPPEL_CENTER_X = 391.0f;
    public static final float CTRL_NOUVELLE_CENTER_X = 425.4f;
    public static final float CTRL_POINTS_CENTER_X = 487.6f;
    public static final float CTRL_DIFF_CENTER_X = 525.8f;
    public static final float CTRL_FIRST_ROW_Y = 469.8f;
    public static final float CTRL_ROW_HEIGHT = 14.3f;

    // ---- Epreuves facultatives (droite, alimentent le TOTAL DEFINITIF) ----
    public static final float FAC_LANGUE_X = 470.9f, FAC_LANGUE_Y = 417.4f;
    public static final float FAC_ARTS_X = 525.0f, FAC_ARTS_Y = 396.6f;

    // ---- Epreuve facultative + Education Physique (gauche, alimentent le 2ème TOTAL) ----
    public static final float FAC_LANGUE_GAUCHE_X = 196.3f, FAC_LANGUE_GAUCHE_Y = 358.6f;
    public static final float FAC_ARTS_GAUCHE_X = 216.3f, FAC_ARTS_GAUCHE_Y = 339.0f;
    public static final float FAC_EDUCPHYS_GAUCHE_X = 165f, FAC_EDUCPHYS_GAUCHE_Y = 410.6f;

    // ---- Education Physique / Total définitif ----
    // NB : ce gabarit n'a pas de case "TOTAL PROVISOIRE" distincte (contrairement au
    // gabarit A1/A2) : le total après contrôle est affiché directement en TOTAL DEFINITIF.
    public static final float EP_NOTE_X = 89.2f, EP_NOTE_Y = 407.3f;
    public static final float EP_POS_X = 196.3f, EP_POS_Y = 417.4f;
    public static final float EP_NEG_X = 196.3f, EP_NEG_Y = 396.6f;
    public static final float TOTAL_DEFINITIF_Y = 377.0f;

    // ---- Décisions du jury : texte libre sur la ligne "Le candidat a été déclaré ..." ----
    public static final float DEC1_TEXTE_X = 179.2f, DEC1_TEXTE_Y = 287.6f;
    public static final float DEC2_TEXTE_X = 425.7f, DEC2_TEXTE_Y = 338.7f;

    // ---- Pied de page 1er groupe : "Fait à ... le ... 19..." (format court) ----
    public static final float DEC1_LIEU_X = 64.2f, DEC1_JOUR_MOIS_X = 129.2f, DEC1_ANNEE2_X = 229.2f, DEC1_PIED_Y = 226.8f;

    // ---- Pied de page 2eme groupe (format long) ----
    public static final float DEC2_LIEU_X = 310.7f, DEC2_JOUR_MOIS_X = 435.7f, DEC2_ANNEE2_X = 555.7f, DEC2_PIED_Y = 285.4f;
}
