package com.officedubac.project.modules.a3deuxiemepartie.model;

import java.util.List;
import java.util.stream.Stream;

import static com.officedubac.project.modules.a3deuxiemepartie.model.Enums.TypeEpreuve.ECRIT;
import static com.officedubac.project.modules.a3deuxiemepartie.model.Enums.TypeEpreuve.ORAL;

/**
 * Référentiel figé des matières, coefficients et barèmes de la série A3-2emePartie
 * (A3 - 2ème Partie), tel qu'imprimé sur le gabarit officiel.
 */
public final class MatieresA3DeuxiemePartie {

    private MatieresA3DeuxiemePartie() { }

    // ---- Epreuves écrites ----
    public static final Matiere PHILO = new Matiere("PHILO", "Philosophie", 4, 80, ECRIT);
    public static final Matiere LV1_ECRIT = new Matiere("LV1_ECRIT", "Langue Vivante I (écrit)", 3, 60, ECRIT);
    public static final Matiere HIST_GEO = new Matiere("HIST_GEO", "Histoire et Géographie", 3, 60, ECRIT);
    public static final Matiere LV2 = new Matiere("LV2", "Langue Vivante II", 2, 40, ECRIT);

    public static final List<Matiere> EPREUVES_ECRITES = List.of(PHILO, LV1_ECRIT, HIST_GEO, LV2);

    public static final int BAREME_ECRIT = 240;

    // ---- Epreuves orales ----
    public static final Matiere LV1_ORAL = new Matiere("LV1_ORAL", "Langue Vivante I (oral)", 2, 40, ORAL);
    public static final Matiere MATH = new Matiere("MATH", "Mathématiques", 2, 40, ORAL);

    public static final List<Matiere> EPREUVES_ORALES = List.of(LV1_ORAL, MATH);

    public static final int BAREME_ORAL = 80;

    public static final int BAREME_GENERAL = BAREME_ECRIT + BAREME_ORAL;

    public static Matiere findByCode(String code) {
        return Stream.concat(EPREUVES_ECRITES.stream(), EPREUVES_ORALES.stream())
                .filter(m -> m.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Matière inconnue pour la série A3-2emePartie : " + code));
    }
}
