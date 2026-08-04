package com.officedubac.project.modules.A3_DeuxiemePartie.model;

import java.util.List;
import java.util.stream.Stream;

import static com.officedubac.project.modules.A3_DeuxiemePartie.model.Enums.TypeEpreuve.ECRIT;
import static com.officedubac.project.modules.A3_DeuxiemePartie.model.Enums.TypeEpreuve.ORAL;

/**
 * Référentiel FIXE des matières et coefficients de la Série A3 - 2ème Partie
 * (PREMIERE SESSION), tel qu'imprimé sur le formulaire officiel
 * "RELEVE DE NOTES" de l'Office du Baccalauréat - Université de Dakar.
 */
public final class MatieresA3DeuxiemePartie {

    private MatieresA3DeuxiemePartie() { }

    // ---- Epreuves écrites (total sur 240) ----
    public static final Matiere PHILOSOPHIE = new Matiere("PHILO",    "Philosophie",             4, ECRIT);
    public static final Matiere LV1_ECRIT   = new Matiere("LV1_ECRIT", "Langue Vivante 1 (écrit)", 3, ECRIT);
    public static final Matiere HIST_GEO    = new Matiere("HIST_GEO", "Histoire et Géographie",  3, ECRIT);
    public static final Matiere LV2_ECRIT   = new Matiere("LV2_ECRIT", "Langue Vivante 2 (écrit)", 2, ECRIT);

    public static final List<Matiere> EPREUVES_ECRITES = List.of(PHILOSOPHIE, LV1_ECRIT, HIST_GEO, LV2_ECRIT);

    // barème : (4+3+3+2) * 20 = 240
    public static final int BAREME_ECRIT = 240;

    // ---- Epreuves orales (total sur 80) ----
    public static final Matiere LV1_ORAL      = new Matiere("LV1_ORAL", "Langue Vivante 1 (oral)", 2, ORAL);
    public static final Matiere MATHEMATIQUES = new Matiere("MATH",     "Mathématiques",           2, ORAL);

    public static final List<Matiere> EPREUVES_ORALES = List.of(LV1_ORAL, MATHEMATIQUES);

    // barème : (2+2) * 20 = 80
    public static final int BAREME_ORAL = 80;

    // barème général : 240 + 80 = 320
    public static final int BAREME_GENERAL = BAREME_ECRIT + BAREME_ORAL;

    public static Matiere findByCode(String code) {
        return Stream.concat(EPREUVES_ECRITES.stream(), EPREUVES_ORALES.stream())
                .filter(m -> m.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Matière inconnue pour la série A3 2ème Partie : " + code));
    }
}
