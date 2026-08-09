package com.officedubac.project.modules.c2emepartie.model;

import java.util.List;
import java.util.stream.Stream;

import static com.officedubac.project.modules.c2emepartie.model.Enums.TypeEpreuve.ECRIT;
import static com.officedubac.project.modules.c2emepartie.model.Enums.TypeEpreuve.ORAL;

/**
 * Référentiel figé des matières, coefficients et barèmes de la série C-2emePartie
 * (C - 2ème Partie), tel qu'imprimé sur le gabarit officiel.
 */
public final class MatieresC2emePartie {

    private MatieresC2emePartie() { }

    // ---- Epreuves écrites ----
    public static final Matiere FRANCAIS = new Matiere("FRANCAIS", "Français", 2, 40, ECRIT);
    public static final Matiere MATH = new Matiere("MATH", "Mathématiques", 5, 100, ECRIT);
    public static final Matiere SC_PHYS = new Matiere("SC_PHYS", "Sciences Physiques", 5, 100, ECRIT);
    public static final Matiere SC_NAT = new Matiere("SC_NAT", "Sciences Naturelles", 2, 40, ECRIT);

    public static final List<Matiere> EPREUVES_ECRITES = List.of(FRANCAIS, MATH, SC_PHYS, SC_NAT);

    public static final int BAREME_ECRIT = 280;

    // ---- Epreuves orales ----
    public static final Matiere LV1 = new Matiere("LV1", "Langue Vivante I", 2, 40, ORAL);
    public static final Matiere HIST_GEO = new Matiere("HIST_GEO", "Histoire et Géographie", 2, 40, ORAL);
    public static final Matiere MATH_ORAL = new Matiere("MATH_ORAL", "Mathématiques (oral)", 2, 40, ORAL);

    public static final List<Matiere> EPREUVES_ORALES = List.of(LV1, HIST_GEO, MATH_ORAL);

    public static final int BAREME_ORAL = 120;

    public static final int BAREME_GENERAL = BAREME_ECRIT + BAREME_ORAL;

    public static Matiere findByCode(String code) {
        return Stream.concat(EPREUVES_ECRITES.stream(), EPREUVES_ORALES.stream())
                .filter(m -> m.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Matière inconnue pour la série C-2emePartie : " + code));
    }
}
