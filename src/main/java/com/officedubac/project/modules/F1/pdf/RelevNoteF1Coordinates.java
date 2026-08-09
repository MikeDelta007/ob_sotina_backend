package com.officedubac.project.modules.F1.pdf;

/**
 * Coordonnées (x, y en points PDF, origine en bas à gauche) mesurées par
 * extraction du texte du gabarit officiel "releve-F1-template.pdf"
 * (page A4 : 595.32 x 841.92 pt). Positions approximatives dans les zones à
 * espacement variable (champs en pointillés) : à ajuster visuellement si besoin.
 */
public final class RelevNoteF1Coordinates {

    private RelevNoteF1Coordinates() { }

    // ---- Session : NORMALE / DE REMPLACEMENT (l'option non choisie est barrée) ----
    public static final float SESSION_NORMALE_X0 = 336.1f, SESSION_NORMALE_X1 = 376.1f, SESSION_NORMALE_Y = 799.6f;
    public static final float SESSION_REMPLACEMENT_X0 = 336.1f, SESSION_REMPLACEMENT_X1 = 418.1f, SESSION_REMPLACEMENT_Y = 787.4f;

    // ---- En-tête ----
    public static final float JURY_NUMERO_X = 541.3f, JURY_NUMERO_Y = 766.1f;
    public static final float ANNEE_X = 551.3f, ANNEE_Y = 741.7f;
    public static final float NUMERO_TABLE_X = 394.5f, NUMERO_TABLE_Y = 726.3f;

    // ---- Identité du candidat ----
    public static final float NOM_PRENOM_X = 44.3f, NOM_PRENOM_Y = 684.1f;
    public static final float DATE_NAISSANCE_X = 74.3f, DATE_NAISSANCE_Y = 658.5f;
    public static final float LIEU_NAISSANCE_X = 224.3f, LIEU_NAISSANCE_Y = 658.5f;
    public static final float ETABLISSEMENT_X = 388.2f, ETABLISSEMENT_Y = 684.1f;
    public static final float INDICATIF_X = 518.2f, INDICATIF_Y = 684.1f;
    public static final float OPTIONS_X = 398.2f, OPTIONS_Y = 658.5f;
    public static final float NATIONALITE_X = 375.2f, NATIONALITE_Y = 633.1f;
    public static final float NOMBRE_DE_FOIS_X = 533.2f, NOMBRE_DE_FOIS_Y = 633.1f;

    // ---- 1er groupe : colonnes centrées "Note sur 20" et "Points obtenus" ----
    public static final float G1_NOTE_CENTER_X = 140.4f;
    public static final float G1_POINTS_CENTER_X = 203.1f;
    public static final float[] G1_ROW_Y = { 554.7f, 540.1f, 527.1f, 512.8f, 498.8f, 484.8f, 470.7f, 456.2f, 441.2f, 426.8f };

    public static final float G1_TOTAL_Y = 368.7f;
    public static final float G2_TOTAL_Y = 311.2f;

    // ---- 2eme groupe : colonnes centrées ----
    public static final float G2_NOTE_CENTER_X = 407.7f;
    public static final float G2_POINTS_CENTER_X = 507.5f;
    public static final float[] G2_ROW_Y = {  };

    public static final float REPORT_1ER_TOTAL_Y = 553.5f;

    // ---- Epreuve de contrôle ----
    public static final float CTRL_MATIERE_X = 268.7f;
    public static final float CTRL_RAPPEL_CENTER_X = 387.3f;
    public static final float CTRL_NOUVELLE_CENTER_X = 422.4f;
    public static final float CTRL_POINTS_CENTER_X = 476.5f;
    public static final float CTRL_DIFF_CENTER_X = 518.5f;
    public static final float CTRL_FIRST_ROW_Y = 483.9f;
    public static final float CTRL_ROW_HEIGHT = 14.3f;

    // ---- Epreuves facultatives (droite, alimentent le TOTAL DEFINITIF) ----
    public static final float FAC_LANGUE_X = 466.4f, FAC_LANGUE_Y = 409.5f;
    public static final float FAC_ARTS_X = 521.0f, FAC_ARTS_Y = 388.7f;

    // ---- Epreuve facultative + Education Physique (gauche, alimentent le 2ème TOTAL) ----
    public static final float FAC_LANGUE_GAUCHE_X = 196.3f, FAC_LANGUE_GAUCHE_Y = 351.0f;
    public static final float FAC_ARTS_GAUCHE_X = 216.3f, FAC_ARTS_GAUCHE_Y = 331.6f;
    public static final float FAC_EDUCPHYS_GAUCHE_X = 165f, FAC_EDUCPHYS_GAUCHE_Y = 402.7f;

    // ---- Education Physique / Total définitif ----
    // NB : ce gabarit n'a pas de case "TOTAL PROVISOIRE" distincte (contrairement au
    // gabarit A1/A2) : le total après contrôle est affiché directement en TOTAL DEFINITIF.
    public static final float EP_NOTE_X = 89.3f, EP_NOTE_Y = 399.5f;
    public static final float EP_POS_X = 196.3f, EP_POS_Y = 409.5f;
    public static final float EP_NEG_X = 196.3f, EP_NEG_Y = 388.7f;
    public static final float TOTAL_DEFINITIF_Y = 369.1f;

    // ---- Décisions du jury : texte libre sur la ligne "Le candidat a été déclaré ..." ----
    public static final float DEC1_TEXTE_X = 179.3f, DEC1_TEXTE_Y = 279.8f;
    public static final float DEC2_TEXTE_X = 415.7f, DEC2_TEXTE_Y = 330.9f;

    // ---- Pied de page 1er groupe : "Fait à ... le ... 19..." (format court) ----
    public static final float DEC1_LIEU_X = 64.3f, DEC1_JOUR_MOIS_X = 129.3f, DEC1_ANNEE2_X = 229.3f, DEC1_PIED_Y = 221.1f;

    // ---- Pied de page 2eme groupe (format long) ----
    public static final float DEC2_LIEU_X = 300.7f, DEC2_JOUR_MOIS_X = 425.7f, DEC2_ANNEE2_X = 545.7f, DEC2_PIED_Y = 279.8f;
}
