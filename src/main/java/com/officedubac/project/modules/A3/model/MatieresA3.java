package com.officedubac.project.modules.A3.model;

import java.util.List;

import static com.officedubac.project.modules.A3.model.GroupeEpreuves.DEUXIEME;
import static com.officedubac.project.modules.A3.model.GroupeEpreuves.PREMIER;
import static com.officedubac.project.modules.A3.model.TypeEpreuve.ECRIT;
import static com.officedubac.project.modules.A3.model.TypeEpreuve.ORAL;

/**
 * Référentiel figé des matières, coefficients et barèmes de la série A3
 * (Lettres - Sciences Humaines), tel qu'imprimé sur le gabarit officiel de
 * l'Office du Baccalauréat.
 */
public final class MatieresA3 {

    private MatieresA3() { }

    // ---- 1er groupe d'épreuves ----
    public static final Matiere FR_ECRIT = new Matiere("FR_ECRIT", "Français (écrit)", 3, 60, ECRIT, PREMIER);
    public static final Matiere FR_ORAL = new Matiere("FR_ORAL", "Français (oral)", 2, 40, ORAL, PREMIER);
    public static final Matiere PHILO = new Matiere("PHILO", "Philosophie", 4, 80, ECRIT, PREMIER);
    public static final Matiere HIST_GEO = new Matiere("HIST_GEO", "Histoire et Géographie", 4, 80, ECRIT, PREMIER);
    public static final Matiere LV1_ECRIT = new Matiere("LV1_ECRIT", "Langue Vivante I (écrit)", 3, 60, ECRIT, PREMIER);
    public static final Matiere MATH = new Matiere("MATH", "Mathématiques", 3, 60, ECRIT, PREMIER);
    public static final Matiere LV2_ECRIT = new Matiere("LV2_ECRIT", "Langue Vivante II (écrit)", 3, 60, ECRIT, PREMIER);
    public static final Matiere LV1_ORAL = new Matiere("LV1_ORAL", "Langue Vivante I (oral)", 2, 40, ORAL, PREMIER);

    public static final List<Matiere> PREMIER_GROUPE = List.of(FR_ECRIT, FR_ORAL, PHILO, HIST_GEO, LV1_ECRIT, MATH, LV2_ECRIT, LV1_ORAL);

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
                .orElseThrow(() -> new IllegalArgumentException("Matière inconnue pour la série A3 : " + code));
    }
}
