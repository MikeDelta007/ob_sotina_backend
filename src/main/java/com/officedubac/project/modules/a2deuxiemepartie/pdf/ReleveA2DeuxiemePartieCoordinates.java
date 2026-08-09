package com.officedubac.project.modules.a2deuxiemepartie.pdf;

/**
 * Coordonnées (x, y en points PDF, origine en bas à gauche) du gabarit
 * "RELEVE DE NOTES - Série A2 - 2ème PARTIE - DEUXIEME SESSION" (vectoriel,
 * QR code, page A4 : 595.32 x 841.92 pt).
 *
 * IMPORTANT — origine de ces valeurs : contrairement aux autres modules,
 * elles n'ont pas été mesurées sur un gabarit vierge mais RECONSTRUITES à
 * partir des positions réelles des libellés de fond visibles dans un PDF
 * déjà généré (fourni par l'utilisateur), en ignorant les données saisies
 * qui y étaient mal positionnées. La case ADMIS/AJOURNE a été vérifiée par
 * les coordonnées exactes de ses bordures vectorielles (rectangle de la
 * cellule à cocher), pas estimée.
 *
 * Ce gabarit est identique à celui d'A1 2ème Partie, décalé uniformément
 * de +2,0 pt en Y (X inchangés) — confirmé par recoupement sur une
 * dizaine de libellés indépendants (en-tête, candidat, grille, décision,
 * pied de page).
 */
public final class ReleveA2DeuxiemePartieCoordinates {

    private ReleveA2DeuxiemePartieCoordinates() { }

    // ---- En-tête ----
    public static final float JURY_NUMERO_X = 509f, JURY_NUMERO_Y = 761.4f;
    public static final float NUMERO_TABLE_X = 352f, NUMERO_TABLE_Y = 688.4f;

    // ---- Identité du candidat ----
    public static final float NOM_PRENOM_X       = 46f,  NOM_PRENOM_Y       = 639.2f;
    public static final float DATE_NAISSANCE_X   = 72f,  DATE_NAISSANCE_Y   = 613f;
    public static final float LIEU_NAISSANCE_X   = 210f, LIEU_NAISSANCE_Y   = 613f;
    public static final float ETABLISSEMENT_X    = 386f, ETABLISSEMENT_Y    = 639.4f;
    public static final float INDICATIF_X        = 493f, INDICATIF_Y        = 639.4f;
    public static final float OPTIONS_X          = 395f, OPTIONS_Y          = 613f;
    public static final float NATIONALITE_X      = 372f, NATIONALITE_Y      = 587.5f;
    public static final float NOMBRE_DE_FOIS_X   = 535f, NOMBRE_DE_FOIS_Y   = 587.5f;

    // ---- Epreuves écrites : colonnes centrées "Note/20" et "Pts obte." ----
    public static final float EC_NOTE_CENTER_X   = 118f;
    public static final float EC_POINTS_CENTER_X = 217f;

    public static final float EC_PHILO_Y   = 510.4f;
    public static final float EC_LV1_Y     = 489.8f;
    public static final float EC_HISTGEO_Y = 469.3f;
    public static final float EC_LATAR_Y   = 448.6f;

    public static final float EC_TOTAL_Y = 409.5f;

    // ---- Epreuves orales : colonnes centrées ----
    public static final float OR_NOTE_CENTER_X   = 413f;
    public static final float OR_POINTS_CENTER_X = 512f;

    public static final float OR_LV1_Y   = 510.4f;
    public static final float OR_LV2_Y   = 489.8f;
    public static final float OR_MATH_Y  = 469.3f;

    public static final float OR_TOTAL_Y = 409.5f;

    // ---- Total général (grande ligne) ----
    public static final float TOTAL_GENERAL_X = 466f, TOTAL_GENERAL_Y = 384.7f;

    // ---- Décision : tableau ADMIS | AJOURNE (cellule vérifiée par ses bordures exactes) ----
    public static final float COCHE_ADMIS_CX   = 267.45f, COCHE_ADMIS_CY   = 331.5f;
    public static final float COCHE_AJOURNE_CX = 327.95f, COCHE_AJOURNE_CY = 331.5f;

    // ---- Pied de page ----
    public static final float LIEU_DELIVRANCE_X = 129f, LIEU_DELIVRANCE_Y = 288.7f;
    public static final float JOUR_MOIS_X        = 270f, JOUR_MOIS_Y       = 288.7f;
    public static final float ANNEE_2_CHIFFRES_X = 480f, ANNEE_2_CHIFFRES_Y = 288.7f;

    // NB : le nom du Président du Jury n'est pas imprimé — la ligne
    // "Prénom (s), Nom et signature du Président du Jury" est l'instruction
    // de signature manuscrite elle-même (même choix que sur A1/A1 2ème Partie).
}
