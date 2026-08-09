package com.officedubac.project.modules.ddeuxiemepartie.model;

import java.util.List;
import java.util.stream.Stream;

import static com.officedubac.project.modules.ddeuxiemepartie.model.Enums.TypeEpreuve.ECRIT;
import static com.officedubac.project.modules.ddeuxiemepartie.model.Enums.TypeEpreuve.ORAL;

/**
 * Référentiel figé des matières, coefficients et barèmes de la série D-2emePartie
 * (D - 2ème Partie), tel qu'imprimé sur le gabarit officiel.
 */
public final class MatieresDDeuxiemePartie {

    private MatieresDDeuxiemePartie() { }

    // ---- Epreuves écrites ----
    public static final Matiere PHILO = new Matiere("PHILO", "Philosophie", 2, 40, ECRIT);
    public static final Matiere MATH = new Matiere("MATH", "Mathématiques", 4, 100, ECRIT);
    public static final Matiere SC_PHYS = new Matiere("SC_PHYS", "Sciences Physiques", 4, 100, ECRIT);
    public static final Matiere SC_NAT = new Matiere("SC_NAT", "Sciences Naturelles", 4, 40, ECRIT);

    public static final List<Matiere> EPREUVES_ECRITES = List.of(PHILO, MATH, SC_PHYS, SC_NAT);

    public static final int BAREME_ECRIT = 280;

    // ---- Epreuves orales ----
    public static final Matiere LV1 = new Matiere("LV1", "Langue Vivante I", 2, 40, ORAL);
    public static final Matiere HIST_GEO = new Matiere("HIST_GEO", "Histoire et Géographie", 2, 40, ORAL);
    public static final Matiere SC_PHYS_NAT_ORAL = new Matiere("SC_PHYS_NAT_ORAL", "Sciences Physiques / Naturelles (oral)", 2, 40, ORAL);

    public static final List<Matiere> EPREUVES_ORALES = List.of(LV1, HIST_GEO, SC_PHYS_NAT_ORAL);

    public static final int BAREME_ORAL = 120;

    public static final int BAREME_GENERAL = BAREME_ECRIT + BAREME_ORAL;

    public static Matiere findByCode(String code) {
        return Stream.concat(EPREUVES_ECRITES.stream(), EPREUVES_ORALES.stream())
                .filter(m -> m.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Matière inconnue pour la série D-2emePartie : " + code));
    }
}
