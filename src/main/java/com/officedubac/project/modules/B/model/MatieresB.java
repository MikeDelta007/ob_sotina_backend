package com.officedubac.project.modules.B.model;

import java.util.List;

import static com.officedubac.project.modules.B.model.GroupeEpreuves.DEUXIEME;
import static com.officedubac.project.modules.B.model.GroupeEpreuves.PREMIER;
import static com.officedubac.project.modules.B.model.TypeEpreuve.ECRIT;
import static com.officedubac.project.modules.B.model.TypeEpreuve.ORAL;

/**
 * Référentiel figé des matières, coefficients et barèmes de la série B
 * (Sciences Economiques et Sociales), tel qu'imprimé sur le gabarit officiel de
 * l'Office du Baccalauréat.
 */
public final class MatieresB {

    private MatieresB() { }

    // ---- 1er groupe d'épreuves ----
    public static final Matiere FR_ECRIT = new Matiere("FR_ECRIT", "Français (écrit)", 3, 60, ECRIT, PREMIER);
    public static final Matiere FR_ORAL = new Matiere("FR_ORAL", "Français (oral)", 1, 20, ORAL, PREMIER);
    public static final Matiere PHILO = new Matiere("PHILO", "Philosophie", 3, 60, ECRIT, PREMIER);
    public static final Matiere HIST_GEO = new Matiere("HIST_GEO", "Histoire et Géographie", 3, 60, ECRIT, PREMIER);
    public static final Matiere SC_ECO_SOC = new Matiere("SC_ECO_SOC", "Sciences Economiques et Sociales", 5, 100, ECRIT, PREMIER);
    public static final Matiere MATH = new Matiere("MATH", "Mathématiques", 5, 100, ECRIT, PREMIER);
    public static final Matiere LV1 = new Matiere("LV1", "Langue Vivante I", 3, 60, ECRIT, PREMIER);
    public static final Matiere LV2 = new Matiere("LV2", "Langue Vivante II", 2, 40, ORAL, PREMIER);

    public static final List<Matiere> PREMIER_GROUPE = List.of(FR_ECRIT, FR_ORAL, PHILO, HIST_GEO, SC_ECO_SOC, MATH, LV1, LV2);

    public static final int BAREME_PREMIER_GROUPE = 500;

    // ---- 2eme groupe d'épreuves (report du 1er groupe + épreuve de contrôle) ----
    // (aucune matière propre : le 2eme groupe ne comprend que le report et l'épreuve de contrôle)

    public static final List<Matiere> DEUXIEME_GROUPE = List.of();

    public static final int BAREME_DEUXIEME_GROUPE = 0;

    public static final int BAREME_TOTAL_DEFINITIF = BAREME_PREMIER_GROUPE + BAREME_DEUXIEME_GROUPE;

    public static Matiere findByCode(String code) {
        return java.util.stream.Stream.concat(PREMIER_GROUPE.stream(), DEUXIEME_GROUPE.stream())
                .filter(m -> m.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Matière inconnue pour la série B : " + code));
    }
}
