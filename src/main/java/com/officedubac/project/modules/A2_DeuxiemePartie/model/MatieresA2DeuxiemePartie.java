package com.officedubac.project.modules.A2_DeuxiemePartie.model;

import java.util.List;
import java.util.stream.Stream;

import static com.officedubac.project.modules.A2_DeuxiemePartie.model.Enums.TypeEpreuve.ECRIT;
import static com.officedubac.project.modules.A2_DeuxiemePartie.model.Enums.TypeEpreuve.ORAL;

/**
 * Référentiel FIXE des matières et coefficients de la Série A2 - 2ème Partie
 * (DEUXIEME SESSION), tel qu'imprimé sur le formulaire officiel
 * "RELEVE DE NOTES" de l'Office du Baccalauréat - Université de Dakar.
 *
 * NB: le "L.V. 1" apparaît à la fois en épreuve écrite et en épreuve orale
 * sur le formulaire (deux évaluations distinctes de la même langue) ;
 * les codes sont donc bien différenciés : LV1_ECRIT / LV1_ORAL.
 */
public final class MatieresA2DeuxiemePartie {

    private MatieresA2DeuxiemePartie() { }

    // ---- Epreuves écrites (total sur 220) ----
    public static final Matiere PHILOSOPHIE = new Matiere("PHILO",      "Philosophie",           5, ECRIT);
    public static final Matiere LV1_ECRIT   = new Matiere("LV1_ECRIT",  "Langue Vivante 1 (écrit)", 2, ECRIT);
    public static final Matiere HIST_GEO    = new Matiere("HIST_GEO",   "Histoire et Géographie", 2, ECRIT);
    public static final Matiere LATIN_ARABE = new Matiere("LAT_AR",     "Latin ou Arabe",         2, ECRIT);

    public static final List<Matiere> EPREUVES_ECRITES = List.of(PHILOSOPHIE, LV1_ECRIT, HIST_GEO, LATIN_ARABE);

    // barème : (5+2+2+2) * 20 = 220
    public static final int BAREME_ECRIT = 220;

    // ---- Epreuves orales (total sur 120) ----
    public static final Matiere LV1_ORAL       = new Matiere("LV1_ORAL", "Langue Vivante 1 (oral)", 2, ORAL);
    public static final Matiere LV2_ORAL       = new Matiere("LV2_ORAL", "Langue Vivante 2 (oral)", 2, ORAL);
    public static final Matiere MATHEMATIQUES  = new Matiere("MATH",     "Mathématiques",           2, ORAL);

    public static final List<Matiere> EPREUVES_ORALES = List.of(LV1_ORAL, LV2_ORAL, MATHEMATIQUES);

    // barème : (2+2+2) * 20 = 120
    public static final int BAREME_ORAL = 120;

    // barème général : 220 + 120 = 340
    public static final int BAREME_GENERAL = BAREME_ECRIT + BAREME_ORAL;

    public static Matiere findByCode(String code) {
        return Stream.concat(EPREUVES_ECRITES.stream(), EPREUVES_ORALES.stream())
                .filter(m -> m.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Matière inconnue pour la série A2 2ème Partie : " + code));
    }
}
