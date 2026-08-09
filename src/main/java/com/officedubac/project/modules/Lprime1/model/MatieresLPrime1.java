package com.officedubac.project.modules.Lprime1.model;

import java.util.List;

import static com.officedubac.project.modules.Lprime1.model.GroupeEpreuves.DEUXIEME;
import static com.officedubac.project.modules.Lprime1.model.GroupeEpreuves.PREMIER;
import static com.officedubac.project.modules.Lprime1.model.TypeEpreuve.ECRIT;
import static com.officedubac.project.modules.Lprime1.model.TypeEpreuve.ORAL;

/**
 * Référentiel figé des matières, coefficients et barèmes de la série L'1
 * (Langues et Civilisations Modernes), tel qu'imprimé sur le gabarit officiel de
 * l'Office du Baccalauréat.
 */
public final class MatieresLPrime1 {

    private MatieresLPrime1() { }

    // ---- 1er groupe d'épreuves ----
    public static final Matiere FRANCAIS = new Matiere("FRANCAIS", "Français", 6, 120, ECRIT, PREMIER);
    public static final Matiere PHILO = new Matiere("PHILO", "Philosophie", 4, 80, ECRIT, PREMIER);
    public static final Matiere HIST_GEO = new Matiere("HIST_GEO", "Histoire et Géographie", 2, 40, ECRIT, PREMIER);
    public static final Matiere LV1_ECRIT = new Matiere("LV1_ECRIT", "Langue Vivante I (écrit)", 4, 80, ECRIT, PREMIER);
    public static final Matiere MATH = new Matiere("MATH", "Mathématiques", 2, 40, ECRIT, PREMIER);
    public static final Matiere LV2 = new Matiere("LV2", "Langue Vivante II", 4, 80, ECRIT, PREMIER);
    public static final Matiere LV1_ORAL = new Matiere("LV1_ORAL", "Langue Vivante I (oral)", 2, 40, ORAL, PREMIER);

    public static final List<Matiere> PREMIER_GROUPE = List.of(FRANCAIS, PHILO, HIST_GEO, LV1_ECRIT, MATH, LV2, LV1_ORAL);

    public static final int BAREME_PREMIER_GROUPE = 480;

    // ---- 2eme groupe d'épreuves (report du 1er groupe + épreuve de contrôle) ----
    // (aucune matière propre : le 2eme groupe ne comprend que le report et l'épreuve de contrôle)

    public static final List<Matiere> DEUXIEME_GROUPE = List.of();

    public static final int BAREME_DEUXIEME_GROUPE = 0;

    public static final int BAREME_TOTAL_DEFINITIF = BAREME_PREMIER_GROUPE + BAREME_DEUXIEME_GROUPE;

    public static Matiere findByCode(String code) {
        return java.util.stream.Stream.concat(PREMIER_GROUPE.stream(), DEUXIEME_GROUPE.stream())
                .filter(m -> m.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Matière inconnue pour la série L'1 : " + code));
    }
}
