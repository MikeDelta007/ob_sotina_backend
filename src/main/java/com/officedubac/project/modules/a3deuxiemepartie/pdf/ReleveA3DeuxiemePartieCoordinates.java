package com.officedubac.project.modules.a3deuxiemepartie.pdf;

/**
 * Coordonnées (x, y en points PDF, origine en bas à gauche) mesurées par
 * extraction du texte du gabarit officiel "releve-A3-2emePartie-template.pdf"
 * (page A4 : 595.32 x 841.92 pt). Positions approximatives dans les zones à
 * espacement variable (champs en pointillés) : à ajuster visuellement si besoin.
 */
public final class ReleveA3DeuxiemePartieCoordinates {

    private ReleveA3DeuxiemePartieCoordinates() { }

    public static final float JURY_NUMERO_X = 526.8f, JURY_NUMERO_Y = 759.0f;
    public static final float NUMERO_TABLE_X = 348.9f, NUMERO_TABLE_Y = 685.9f;

    public static final float NOM_PRENOM_X = 44.3f, NOM_PRENOM_Y = 637.8f;
    public static final float DATE_NAISSANCE_X = 78.3f, DATE_NAISSANCE_Y = 612.3f;
    public static final float LIEU_NAISSANCE_X = 306.3f, LIEU_NAISSANCE_Y = 612.3f;
    // NB : ce gabarit n'imprime pas d'établissement/indicatif/options/nationalité/nombre de fois

    public static final float EC_NOTE_CENTER_X = 105.5f;
    public static final float EC_POINTS_CENTER_X = 208.0f;
    public static final float[] EC_ROW_Y = { 513.1f, 492.5f, 471.9f, 451.3f };
    public static final float EC_TOTAL_Y = 410.1f;

    public static final float OR_NOTE_CENTER_X = 400.9f;
    public static final float OR_POINTS_CENTER_X = 503.2f;
    public static final float[] OR_ROW_Y = { 513.1f, 492.5f };
    public static final float OR_TOTAL_Y = 410.1f;

    public static final float TOTAL_GENERAL_X = 228.7f, TOTAL_GENERAL_Y = 384.2f;

    public static final float DEC_TEXTE_X = 202.4f, DEC_TEXTE_Y = 362.5f;

    public static final float LIEU_DELIVRANCE_X = 128.9f, JOUR_MOIS_X = 208.9f, ANNEE2_X = 268.9f, PIED_Y = 310.1f;
}
