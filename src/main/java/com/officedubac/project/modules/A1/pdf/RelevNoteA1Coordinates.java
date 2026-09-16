package com.officedubac.project.modules.A1.pdf;
public final class RelevNoteA1Coordinates {

    private RelevNoteA1Coordinates() { }
    // ---- Session : NORMALE / DE REMPLACEMENT (l'option non choisie est barrée) ----
    // Y = milieu de la hauteur du texte (et non la ligne de base), pour que le
    // trait traverse le mot au lieu de souligner son bas.
    public static final float SESSION_NORMALE_X0 = 334f, SESSION_NORMALE_X1 = 391f, SESSION_NORMALE_Y = 783f;
    public static final float SESSION_REMPLACEMENT_X0 = 334f, SESSION_REMPLACEMENT_X1 = 441f, SESSION_REMPLACEMENT_Y = 773f;

    // ---- En-tête ----
    public static final float NUMERO_TABLE_X = 385f, NUMERO_TABLE_Y = 706f;
    public static final float JURY_NUMERO_X = 538f, JURY_NUMERO_Y = 749f;
    public static final float ANNEE_X       = 538f, ANNEE_Y       = 724f;

    // ---- Identité du candidat ----
    public static final float NOM_PRENOM_X       = 30f,  NOM_PRENOM_Y       = 667f;
    public static final float DATE_NAISSANCE_X   = 75f,  DATE_NAISSANCE_Y   = 640f;
    public static final float LIEU_NAISSANCE_X   = 210f, LIEU_NAISSANCE_Y   = 640f;
    public static final float ETABLISSEMENT_X    = 392f, ETABLISSEMENT_Y    = 667f;
    public static final float INDICATIF_X        = 497f, INDICATIF_Y        = 667f;
    public static final float OPTIONS_X          = 400f, OPTIONS_Y          = 640f;
    public static final float NATIONALITE_X      = 372f, NATIONALITE_Y      = 616f;
    public static final float NOMBRE_DE_FOIS_X   = 535f, NOMBRE_DE_FOIS_Y   = 616f;

    // ---- 1er groupe : colonnes centrées "Note sur 20" et "Points obtenus" ----
    public static final float G1_NOTE_CENTER_X   = 141f;
    public static final float G1_POINTS_CENTER_X = 207f;

    public static final float G1_FR_ECRIT_Y  = 535f;
    public static final float G1_FR_ORAL_Y   = 520.3f;
    public static final float G1_PHILO_Y     = 507.4f;
    public static final float G1_LATGREC_Y   = 493.1f;
    public static final float G1_HISTGEO_Y   = 479f;
    public static final float G1_LV_Y        = 462.7f;

    public static final float G1_TOTAL_Y = 444f;  // "1er TOTAL"
    public static final float G2_TOTAL_Y = 323f; // "2ème TOTAL"

    // ---- 2eme groupe : colonnes centrées ----
    public static final float G2_NOTE_CENTER_X   = 416f;
    public static final float G2_POINTS_CENTER_X = 519f;

    public static final float REPORT_1ER_TOTAL_Y = 535f;
    public static final float G2_GRECLATIN_Y     = 520.1f;
    public static final float G2_MATHS_Y          = 505f;

    // ---- Epreuves orales de contrôle ----
    public static final float CTRL_MATIERE_X        = 272f;
    public static final float CTRL_RAPPEL_CENTER_X  = 383f;
    public static final float CTRL_NOUVELLE_CENTER_X = 416f;
    public static final float CTRL_POINTS_CENTER_X   = 480f;
    public static final float CTRL_DIFF_CENTER_X     = 519f;
    public static final float CTRL_FIRST_ROW_Y = 444f;
    public static final float CTRL_ROW_HEIGHT  = 15f;

    // ---- Epreuves facultatives (droite, alimentent le TOTAL DEFINITIF) ----
    public static final float FAC_LANGUE_X = 519f, FAC_LANGUE_Y = 405f;
    public static final float FAC_ARTS_X   = 519f, FAC_ARTS_Y   = 388f;

    // ---- Epreuve facultative + Education Physique (gauche, alimentent le 2ème TOTAL) ----
    public static final float FAC_EDUCPHYS_GAUCHE_X = 205f, FAC_EDUCPHYS_GAUCHE_Y = 367f;
    public static final float FAC_LANGUE_GAUCHE_X   = 205f, FAC_LANGUE_GAUCHE_Y   = 353f;
    public static final float FAC_ARTS_GAUCHE_X     = 205f, FAC_ARTS_GAUCHE_Y     = 339f;

    // ---- Total provisoire / Education Physique (droite) / Total définitif ----
    public static final float TOTAL_PROVISOIRE_X = 519f, TOTAL_PROVISOIRE_Y = 367f;

    public static final float EP_NOTE_X = 330f, EP_NOTE_Y = 343f;
    public static final float EP_POS_X  = 515f, EP_POS_Y  = 353f;
    public static final float EP_NEG_X  = 515f, EP_NEG_Y  = 339f;

    public static final float TOTAL_DEFINITIF_Y = 323;

    // ---- Décisions du jury : 1er groupe (cases à cocher réelles) ----
    public static final float DEC1_MENTION_X = 156f, DEC1_MENTION_Y = 276f;
    public static final float DEC1_COCHE_ADMIS_CX    = 34.75f, DEC1_COCHE_ADMIS_CY    = 280f;
    public static final float DEC1_COCHE_AUTORISE_CX = 34.75f, DEC1_COCHE_AUTORISE_CY = 268f;
    public static final float DEC1_COCHE_AJOURNE_CX  = 34.75f, DEC1_COCHE_AJOURNE_CY  = 256f;

    // ---- Pied de page 1er groupe ----
    public static final float DEC1_LIEU_X = 65f,  DEC1_LIEU_Y = 234f;
    public static final float DEC1_JOUR_MOIS_X = 158f, DEC1_JOUR_MOIS_Y = 234f;
    public static final float DEC1_ANNEE2_X = 245f, DEC1_ANNEE2_Y = 234f; // 2 derniers chiffres, après "19"
    // NB : pas de champ Président du Jury sur ce pied de page — la ligne
    // "Cachet obligatoire Prénom (s), Nom et signature du Président du Jury."
    // est l'instruction de signature manuscrite elle-même, il n'y a pas de
    // ligne blanche dédiée à un nom pré-imprimé sur ce gabarit.

    // ---- Tampon "DAKAR, le [date du jour]" — imprimé juste en dessous de
    // "Cachet obligatoire Prénom (s), Nom et signature du Président du Jury."
    // (1er groupe), uniquement si le candidat s'est arrêté au 1er groupe. ----
    public static final float DEC1_GENERE_X = 45f, DEC1_GENERE_Y = 175f;

    // ---- Décisions du jury : 2eme groupe (cases à cocher réelles) ----
    public static final float DEC2_MENTION_X = 428f, DEC2_MENTION_Y = 283f;
    public static final float DEC2_COCHE_ADMIS_CX   = 269.65f, DEC2_COCHE_ADMIS_CY   = 280f;
    public static final float DEC2_COCHE_AJOURNE_CX = 269.65f, DEC2_COCHE_AJOURNE_CY = 268f;

    // ---- Pied de page 2eme groupe ----
    public static final float DEC2_LIEU_X = 291f, DEC2_LIEU_Y = 250f;
    public static final float DEC2_JOUR_MOIS_X = 405f, DEC2_JOUR_MOIS_Y = 250f;
    public static final float DEC2_ANNEE2_X = 552f, DEC2_ANNEE2_Y = 250f;
    // NB : pas de champ Président du Jury sur ce pied de page non plus,
    // même raison que le 1er groupe (voir ci-dessus).

    // ---- Tampon "DAKAR, le [date du jour]" — imprimé juste en dessous de
    // "Cachet obligatoire." (2ème groupe), uniquement si le candidat est
    // passé par le 2ème groupe. ----
    public static final float DEC2_GENERE_X = 291f, DEC2_GENERE_Y = 188f;
}
