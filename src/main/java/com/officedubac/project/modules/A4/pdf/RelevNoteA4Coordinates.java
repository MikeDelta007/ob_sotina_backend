package com.officedubac.project.modules.A4.pdf;

/**
 * Coordonnées (x, y en points PDF, origine en bas à gauche) mesurées par
 * extraction du texte du gabarit officiel "releve-A4-template.pdf"
 * (page A4 : 595.32 x 841.92 pt). Positions approximatives dans les zones à
 * espacement variable (champs en pointillés) : à ajuster visuellement si besoin.
 */
public final class RelevNoteA4Coordinates {

    private RelevNoteA4Coordinates() { }

    // ---- Session : NORMALE / DE REMPLACEMENT (l'option non choisie est barrée) ----
    public static final float SESSION_NORMALE_X0 = 287.1f, SESSION_NORMALE_X1 = 327.1f, SESSION_NORMALE_Y = 799.6f;
    public static final float SESSION_REMPLACEMENT_X0 = 287.1f, SESSION_REMPLACEMENT_X1 = 369.1f, SESSION_REMPLACEMENT_Y = 787.4f;

    // ---- En-tête ----
    public static final float JURY_NUMERO_X = 405.1f, JURY_NUMERO_Y = 736.5f;
    public static final float ANNEE_X = 97.5f, ANNEE_Y = 665.7f;
    public static final float NUMERO_TABLE_X = 80f, NUMERO_TABLE_Y = 0f;

    // ---- Identité du candidat ----
    public static final float NOM_PRENOM_X = 110.0f, NOM_PRENOM_Y = 649.4f;
    public static final float DATE_NAISSANCE_X = 140.0f, DATE_NAISSANCE_Y = 636.5f;
    public static final float LIEU_NAISSANCE_X = 290.0f, LIEU_NAISSANCE_Y = 636.5f;
    public static final float ETABLISSEMENT_X = 260.4f, ETABLISSEMENT_Y = 651.8f;
    public static final float INDICATIF_X = 390.4f, INDICATIF_Y = 651.8f;
    public static final float OPTIONS_X = 128.5f, OPTIONS_Y = 613.8f;
    public static final float NATIONALITE_X = 228.4f, NATIONALITE_Y = 632.0f;
    public static final float NOMBRE_DE_FOIS_X = 205.0f, NOMBRE_DE_FOIS_Y = 632.0f;

    // ---- 1er groupe : colonnes centrées "Note sur 20" et "Points obtenus" ----
    public static final float G1_NOTE_CENTER_X = 136.3f;
    public static final float G1_POINTS_CENTER_X = 199.0f;
    public static final float[] G1_ROW_Y = { 492.0f, 477.4f, 464.5f, 450.2f, 435.2f, 418.9f };

    public static final float G1_TOTAL_Y = 400.9f;
    public static final float G2_TOTAL_Y = 280.6f;

    // ---- 2eme groupe : colonnes centrées ----
    public static final float G2_NOTE_CENTER_X = 412.2f;
    public static final float G2_POINTS_CENTER_X = 511.8f;
    public static final float[] G2_ROW_Y = { 477.4f };

    public static final float REPORT_1ER_TOTAL_Y = 490.8f;

    // ---- Epreuve de contrôle ----
    public static final float CTRL_MATIERE_X = 272.5f;
    public static final float CTRL_RAPPEL_CENTER_X = 391.0f;
    public static final float CTRL_NOUVELLE_CENTER_X = 422.9f;
    public static final float CTRL_POINTS_CENTER_X = 481.5f;
    public static final float CTRL_DIFF_CENTER_X = 522.8f;
    public static final float CTRL_FIRST_ROW_Y = 417.0f;
    public static final float CTRL_ROW_HEIGHT = 14.3f;

    // ---- Epreuves facultatives (droite, alimentent le TOTAL DEFINITIF) ----
    public static final float FAC_LANGUE_X = 470.7f, FAC_LANGUE_Y = 363.0f;
    public static final float FAC_ARTS_X = 524.7f, FAC_ARTS_Y = 342.3f;

    // ---- Epreuve facultative + Education Physique (gauche, alimentent le 2ème TOTAL) ----
    public static final float FAC_LANGUE_GAUCHE_X = 192.9f, FAC_LANGUE_GAUCHE_Y = 311.7f;
    public static final float FAC_ARTS_GAUCHE_X = 212.9f, FAC_ARTS_GAUCHE_Y = 297.6f;
    public static final float FAC_EDUCPHYS_GAUCHE_X = 165f, FAC_EDUCPHYS_GAUCHE_Y = 317.1f;

    // ---- Education Physique / Total définitif ----
    // NB : ce gabarit n'a pas de case "TOTAL PROVISOIRE" distincte (contrairement au
    // gabarit A1/A2) : le total après contrôle est affiché directement en TOTAL DEFINITIF.
    public static final float EP_NOTE_X = 329.5f, EP_NOTE_Y = 300.2f;
    public static final float EP_POS_X = 426.8f, EP_POS_Y = 311.3f;
    public static final float EP_NEG_X = 426.8f, EP_NEG_Y = 297.3f;
    public static final float TOTAL_DEFINITIF_Y = 281.2f;

    // ---- Décisions du jury : texte libre sur la ligne "Le candidat a été déclaré ..." ----
    public static final float DEC1_TEXTE_X = 175.7f, DEC1_TEXTE_Y = 256.6f;
    public static final float DEC2_TEXTE_X = 419.5f, DEC2_TEXTE_Y = 256.6f;

    // ---- Pied de page 1er groupe : "Fait à ... le ... 19..." (format court) ----
    public static final float DEC1_LIEU_X = 60.7f, DEC1_JOUR_MOIS_X = 125.7f, DEC1_ANNEE2_X = 225.7f, DEC1_PIED_Y = 215.7f;

    // ---- Pied de page 2eme groupe (format long) ----
    public static final float DEC2_LIEU_X = 304.5f, DEC2_JOUR_MOIS_X = 429.5f, DEC2_ANNEE2_X = 549.5f, DEC2_PIED_Y = 215.7f;
}
