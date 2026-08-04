package com.officedubac.project.modules.F1_Deuxieme_Partie.model;

import java.util.List;
import java.util.stream.Stream;

import static com.officedubac.project.modules.F1_Deuxieme_Partie.model.Enums.TypeEpreuve.ECRIT;
import static com.officedubac.project.modules.F1_Deuxieme_Partie.model.Enums.TypeEpreuve.ORAL;

/**
 * Référentiel FIXE des matières et coefficients de la Série F1 - 2ème Partie
 * (PREMIERE SESSION), tel qu'imprimé sur le formulaire officiel
 * "RELEVE DE NOTES" de l'Office du Baccalauréat - Université de Dakar.
 */
public final class MatieresF1DeuxiemePartie {

    private MatieresF1DeuxiemePartie() { }

    // ---- Epreuves écrites (total sur 420) ----
    public static final Matiere MATHS             = new Matiere("MATHS",           "Mathématiques",                    4, ECRIT);
    public static final Matiere ELECTRIC_METALLU  = new Matiere("ELECTRIC_METALLU","Electricité - Métallurgie",         2, ECRIT);
    public static final Matiere MECAN             = new Matiere("MECAN",           "Mécanique",                        3, ECRIT);
    public static final Matiere ETUDE_OU_PROJET   = new Matiere("ETUDE_PROJET",    "Etude ou Projet",                  6, ECRIT);
    public static final Matiere A_FABR_E_DOUT     = new Matiere("A_FABR_E_DOUT",   "Analyse de Fabrication, Etude d'outillage", 6, ECRIT);

    public static final List<Matiere> EPREUVES_ECRITES = List.of(
            MATHS, ELECTRIC_METALLU, MECAN, ETUDE_OU_PROJET, A_FABR_E_DOUT
    );

    // barème : (4+2+3+6+6) * 20 = 420
    public static final int BAREME_ECRIT = 420;

    // ---- Epreuves orales (total sur 240) ----
    public static final Matiere AUTOMATISMES    = new Matiere("AUTOM",           "Automatismes",   2, ORAL);
    public static final Matiere TECHNOLOGIE     = new Matiere("TECHNO",          "Technologie",    2, ORAL);
    public static final Matiere LV              = new Matiere("LV",             "Langue Vivante",  2, ORAL);
    public static final Matiere EPREUVE_ATELIER = new Matiere("EPREUVE_ATELIER","Epreuve d'atelier", 6, ORAL);

    public static final List<Matiere> EPREUVES_ORALES = List.of(AUTOMATISMES, TECHNOLOGIE, LV, EPREUVE_ATELIER);

    // barème : (2+2+2+6) * 20 = 240
    public static final int BAREME_ORAL = 240;

    // barème général : 420 + 240 = 660
    public static final int BAREME_GENERAL = BAREME_ECRIT + BAREME_ORAL;

    public static Matiere findByCode(String code) {
        return Stream.concat(EPREUVES_ECRITES.stream(), EPREUVES_ORALES.stream())
                .filter(m -> m.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Matière inconnue pour la série F1 2ème Partie : " + code));
    }
}
