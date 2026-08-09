package com.officedubac.project.modules.a2deuxiemepartie.model;

import java.util.List;
import java.util.stream.Stream;

import static com.officedubac.project.modules.a2deuxiemepartie.model.Enums.TypeEpreuve.ECRIT;
import static com.officedubac.project.modules.a2deuxiemepartie.model.Enums.TypeEpreuve.ORAL;

/**
 * Référentiel FIXE des matières et coefficients de la Série A2 - 2ème Partie
 * (DEUXIEME SESSION), tel qu'imprimé sur le nouveau gabarit officiel
 * "RELEVE DE NOTES" de l'Office du Baccalauréat.
 */
public final class MatieresA2DeuxiemePartie {

    private MatieresA2DeuxiemePartie() { }

    // ---- Epreuves écrites (total sur 220) ----
    public static final Matiere PHILOSOPHIE = new Matiere("PHILO",      "Philosophie",             5, ECRIT);
    public static final Matiere LV1_ECRIT   = new Matiere("LV1_ECRIT",  "Langue Vivante 1 (écrit)", 2, ECRIT);
    public static final Matiere HIST_GEO    = new Matiere("HIST_GEO",   "Histoire et Géographie",  2, ECRIT);
    public static final Matiere LATIN_ARABE = new Matiere("LAT_AR",     "Latin - Arabe",           2, ECRIT);

    public static final List<Matiere> EPREUVES_ECRITES = List.of(PHILOSOPHIE, LV1_ECRIT, HIST_GEO, LATIN_ARABE);

    public static final int BAREME_ECRIT = 220;

    // ---- Epreuves orales (total sur 120) ----
    public static final Matiere LV1_ORAL      = new Matiere("LV1_ORAL", "Langue Vivante 1 (oral)", 2, ORAL);
    public static final Matiere LV2_ORAL      = new Matiere("LV2_ORAL", "Langue Vivante 2 (oral)", 2, ORAL);
    public static final Matiere MATHEMATIQUES = new Matiere("MATH",     "Mathématiques",           2, ORAL);

    public static final List<Matiere> EPREUVES_ORALES = List.of(LV1_ORAL, LV2_ORAL, MATHEMATIQUES);

    public static final int BAREME_ORAL = 120;

    public static final int BAREME_GENERAL = BAREME_ECRIT + BAREME_ORAL;

    public static Matiere findByCode(String code) {
        return Stream.concat(EPREUVES_ECRITES.stream(), EPREUVES_ORALES.stream())
                .filter(m -> m.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Matière inconnue pour la série A2 2ème Partie : " + code));
    }
}
