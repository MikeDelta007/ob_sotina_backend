package com.officedubac.project.modules.A2.pdf;

public final class RelevNoteA2Coordinates {

    private RelevNoteA2Coordinates() { }

    // ---- En-tête ----
    // Seul "N° de table" a une case imprimée dédiée ; Jury n°/Année sont
    // écrits plus bas, dans l'encadré identité (voir JURY_NUMERO_X/ANNEE_X).
    public static final float NUMERO_TABLE_X = 455f, NUMERO_TABLE_Y = 719f;

    // ---- Session : ligne "Session : ______" à remplir (pas de rature sur ce gabarit) ----
    public static final float SESSION_TEXT_X = 316f, SESSION_TEXT_Y = 675f;

    // ---- Identité du candidat : unique encadré vierge (270.6,554.3)-(576.3,654.6) ----
    public static final float NOM_PRENOM_X       = 278f, NOM_PRENOM_Y       = 624f;
    public static final float DATE_NAISSANCE_X   = 278f, DATE_NAISSANCE_Y   = 607f;
    public static final float LIEU_NAISSANCE_X   = 400f, LIEU_NAISSANCE_Y   = 607f;
    public static final float ETABLISSEMENT_X    = 400f, ETABLISSEMENT_Y    = 624f;
    public static final float INDICATIF_X        = 470f, INDICATIF_Y        = 590f;
    public static final float OPTIONS_X          = 278f, OPTIONS_Y          = 573f;
    public static final float NATIONALITE_X      = 450f, NATIONALITE_Y      = 607f;
    public static final float NOMBRE_DE_FOIS_X   = 420f, NOMBRE_DE_FOIS_Y   = 556f;
    // 6ème ligne de l'encadré : Jury n° / Année (pas de case dédiée ailleurs sur ce gabarit)
    public static final float JURY_NUMERO_X      = 278f, JURY_NUMERO_Y      = 539f;
    public static final float ANNEE_X            = 400f, ANNEE_Y            = 675f;

    // ---- 1er groupe : colonnes centrées "Note sur 20" et "Points obtenus" ----
    public static final float G1_NOTE_CENTER_X   = 144f;
    public static final float G1_POINTS_CENTER_X = 210f;

    public static final float G1_FR_ECRIT_Y  = 477f;
    public static final float G1_FR_ORAL_Y   = 463f;
    public static final float G1_PHILO_Y     = 450f;
    public static final float G1_LATGREC_Y   = 434f;
    public static final float G1_HISTGEO_Y   = 417f;
    public static final float G1_LV_Y        = 401f;

    public static final float G1_TOTAL_Y = 300f; // "1er TOTAL"
    public static final float G2_TOTAL_Y = 250f; // "2ème TOTAL"

    // ---- 2eme groupe : colonnes centrées ----
    public static final float G2_NOTE_CENTER_X   = 419f;
    public static final float G2_POINTS_CENTER_X = 522f;

    public static final float REPORT_1ER_TOTAL_Y = 487f;
    public static final float G2_GRECLATIN_Y     = 473f; // "Langue Vivante" sur ce gabarit
    public static final float G2_MATHS_Y          = 460f;

    // ---- Epreuves orales de contrôle ----
    public static final float CTRL_MATIERE_X        = 276f;
    public static final float CTRL_RAPPEL_CENTER_X  = 386f;
    public static final float CTRL_NOUVELLE_CENTER_X = 419f;
    public static final float CTRL_POINTS_CENTER_X   = 482f;
    public static final float CTRL_DIFF_CENTER_X     = 522f;
    public static final float CTRL_FIRST_ROW_Y = 393f;
    public static final float CTRL_ROW_HEIGHT  = 22f;

    // ---- Epreuves facultatives (droite, alimentent le TOTAL DEFINITIF) ----
    public static final float FAC_LANGUE_X = 440f, FAC_LANGUE_Y = 290f;
    public static final float FAC_ARTS_X   = 470f, FAC_ARTS_Y   = 275f;

    // ---- Epreuve facultative + Education Physique (gauche, alimentent le 2ème TOTAL) ----
    public static final float FAC_EDUCPHYS_GAUCHE_X = 160f, FAC_EDUCPHYS_GAUCHE_Y = 300f;
    public static final float FAC_LANGUE_GAUCHE_X   = 155f, FAC_LANGUE_GAUCHE_Y   = 287f;
    public static final float FAC_ARTS_GAUCHE_X     = 163f, FAC_ARTS_GAUCHE_Y     = 273f;

    // ---- Total provisoire / Education Physique (droite) / Total définitif ----
    public static final float TOTAL_PROVISOIRE_X = 522f, TOTAL_PROVISOIRE_Y = 260f;

    public static final float EP_NOTE_X = 322f, EP_NOTE_Y = 275f;
    public static final float EP_POS_X  = 416f, EP_POS_Y  = 287f;
    public static final float EP_NEG_X  = 419f, EP_NEG_Y  = 272f;

    public static final float TOTAL_DEFINITIF_Y = 250f;

    // ---- Décisions du jury : 1er groupe (cases à cocher réelles) ----
    public static final float DEC1_MENTION_X = 157f, DEC1_MENTION_Y = 237f;
    public static final float DEC1_COCHE_ADMIS_CX    = 34.75f, DEC1_COCHE_ADMIS_CY    = 239.5f;
    public static final float DEC1_COCHE_AUTORISE_CX = 34.75f, DEC1_COCHE_AUTORISE_CY = 229.4f;
    public static final float DEC1_COCHE_AJOURNE_CX  = 34.75f, DEC1_COCHE_AJOURNE_CY  = 219.2f;

    // ---- Pied de page 1er groupe ----
    public static final float DEC1_LIEU_X = 55f,  DEC1_LIEU_Y = 206f;
    public static final float DEC1_JOUR_MOIS_X = 146f, DEC1_JOUR_MOIS_Y = 206f;
    public static final float DEC1_ANNEE2_X = 240f, DEC1_ANNEE2_Y = 206f; // 2 derniers chiffres, après "19"

    // ---- Tampon "DAKAR, le [date du jour]" — sous "Cachet obligatoire ...
    // Président du Jury" (1er groupe) ----
    public static final float DEC1_GENERE_X = 45f, DEC1_GENERE_Y = 156f;

    // ---- Décisions du jury : 2eme groupe (cases à cocher réelles) ----
    // NB : la mention du 2ème groupe est pré-imprimée ("ADMIS avec la mention
    // PASSABLE") sur ce gabarit — on ne doit rien écrire par-dessus.
    public static final float DEC2_COCHE_ADMIS_CX   = 269.65f, DEC2_COCHE_ADMIS_CY   = 239.5f;
    public static final float DEC2_COCHE_AJOURNE_CX = 269.65f, DEC2_COCHE_AJOURNE_CY = 229.4f;

    // ---- Pied de page 2eme groupe ----
    public static final float DEC2_LIEU_X = 298f, DEC2_LIEU_Y = 201f;
    public static final float DEC2_JOUR_MOIS_X = 397f, DEC2_JOUR_MOIS_Y = 201f;
    public static final float DEC2_ANNEE2_X = 543f, DEC2_ANNEE2_Y = 201f;

    // ---- Tampon "DAKAR, le [date du jour]" — sous "Cachet obligatoire."
    // (2ème groupe) ----
    public static final float DEC2_GENERE_X = 298f, DEC2_GENERE_Y = 142f;
}
