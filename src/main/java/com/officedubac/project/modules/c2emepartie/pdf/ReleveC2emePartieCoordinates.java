package com.officedubac.project.modules.c2emepartie.pdf;

/**
 * Coordonnées (x, y en points PDF, origine en bas à gauche) mesurées par
 * extraction du texte du gabarit officiel "releve-C-1erePartie-template.pdf"
 * (page A4 : 595.32 x 841.92 pt). Positions approximatives dans les zones à
 * espacement variable (champs en pointillés) : à ajuster visuellement si besoin.
 */
public final class ReleveC2emePartieCoordinates {

    private ReleveC2emePartieCoordinates() { }

    public static final float JURY_NUMERO_X = 526.8f, JURY_NUMERO_Y = 759.0f;
    public static final float NUMERO_TABLE_X = 348.9f, NUMERO_TABLE_Y = 685.9f;

    public static final float NOM_PRENOM_X = 44.2f, NOM_PRENOM_Y = 637.1f;
    public static final float DATE_NAISSANCE_X = 78.2f, DATE_NAISSANCE_Y = 611.6f;
    public static final float LIEU_NAISSANCE_X = 211.2f, LIEU_NAISSANCE_Y = 611.6f;
    // NB : ce gabarit n'imprime pas d'établissement/indicatif/options/nationalité/nombre de fois

    public static final float EC_NOTE_CENTER_X = 108.1f;
    public static final float EC_POINTS_CENTER_X = 213.1f;
    public static final float[] EC_ROW_Y = { 511.6f, 491.0f, 470.5f, 449.8f };
    public static final float EC_TOTAL_Y = 408.6f;

    public static final float OR_NOTE_CENTER_X = 401.7f;
    public static final float OR_POINTS_CENTER_X = 508.2f;
    public static final float[] OR_ROW_Y = { 511.6f, 491.0f, 470.5f };
    public static final float OR_TOTAL_Y = 408.6f;

    public static final float TOTAL_GENERAL_X = 228.7f, TOTAL_GENERAL_Y = 384.2f;

    public static final float DEC_TEXTE_X = 241.0f, DEC_TEXTE_Y = 340.2f;

    public static final float LIEU_DELIVRANCE_X = 128.9f, JOUR_MOIS_X = 208.9f, ANNEE2_X = 268.9f, PIED_Y = 288.0f;
}
