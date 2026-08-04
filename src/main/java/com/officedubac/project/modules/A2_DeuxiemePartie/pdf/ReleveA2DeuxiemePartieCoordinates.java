package com.officedubac.project.modules.A2_DeuxiemePartie.pdf;

/**
 * Coordonnées (x, y en points PDF, origine en bas à gauche) mesurées sur le
 * formulaire officiel "RELEVE DE NOTES - Série A2 - 2ème PARTIE - DEUXIEME
 * SESSION" (page A4 : 595.276 x 841.890 pt). Le formulaire scanné occupe une
 * zone plus restreinte de la page (marges importantes) que le formulaire A1 —
 * ces coordonnées sont donc propres à ce document et ne doivent pas être
 * réutilisées pour un autre scan.
 */
public final class ReleveA2DeuxiemePartieCoordinates {

    private ReleveA2DeuxiemePartieCoordinates() { }

    // ---- En-tête ----
    public static final float JURY_NUMERO_X = 335f, JURY_NUMERO_Y = 587f;

    // ---- Identité du candidat ----
    public static final float NOM_PRENOM_X     = 130f, NOM_PRENOM_Y     = 535f;
    public static final float DATE_NAISSANCE_X = 152f, DATE_NAISSANCE_Y = 502f;
    public static final float LIEU_NAISSANCE_X = 125f, LIEU_NAISSANCE_Y = 486f;
    public static final float ETABLISSEMENT_X  = 288f, ETABLISSEMENT_Y  = 527f;
    public static final float INDICATIF_X      = 340f, INDICATIF_Y      = 527f;
    public static final float OPTIONS_X        = 298f, OPTIONS_Y        = 509f;
    public static final float N_X              = 280f, N_Y             = 479f;
    public static final float F_X              = 337f, F_Y             = 479f;

    // ---- Epreuves écrites : colonnes Note/20 (x) et Pts obte. (x) ----
    public static final float EC_NOTE_X   = 160f;
    public static final float EC_POINTS_X = 205f;

    public static final float EC_PHILO_Y   = 407f;
    public static final float EC_LV1_Y     = 385f;
    public static final float EC_HISTGEO_Y = 363f;
    public static final float EC_LATAR_Y   = 340f;

    public static final float EC_TOTAL_POINTS_X = 205f, EC_TOTAL_Y = 296f;

    // ---- Epreuves orales : colonnes Note/20 (x) et Pts obte (x) ----
    public static final float OR_NOTE_X   = 289f;
    public static final float OR_POINTS_X = 335f;

    public static final float OR_LV1_Y   = 407f;
    public static final float OR_LV2_Y   = 385f;
    public static final float OR_MATHS_Y = 363f;

    public static final float OR_TOTAL_POINTS_X = 315f, OR_TOTAL_Y = 296f;

    // ---- Total général ----
    public static final float TOTAL_GENERAL_X = 322f, TOTAL_GENERAL_Y = 272f;

    // ---- Décision (ADMIS / AJOURNE) : coche à gauche du mot choisi ----
    public static final float COCHE_ADMIS_X   = 222f, COCHE_ADMIS_Y   = 249f;
    public static final float COCHE_AJOURNE_X = 270f, COCHE_AJOURNE_Y = 249f;

    // ---- Pied de page ----
    public static final float LIEU_DELIVRANCE_X = 197f, LIEU_DELIVRANCE_Y = 228f;
    public static final float DATE_DELIVRANCE_X = 282f, DATE_DELIVRANCE_Y = 228f;
    public static final float PRESIDENT_JURY_X  = 181f, PRESIDENT_JURY_Y  = 203f;
}
