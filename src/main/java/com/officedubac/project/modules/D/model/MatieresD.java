package com.officedubac.project.modules.D.model;

import java.util.List;

import static com.officedubac.project.modules.D.model.GroupeEpreuves.DEUXIEME;
import static com.officedubac.project.modules.D.model.GroupeEpreuves.PREMIER;
import static com.officedubac.project.modules.D.model.TypeEpreuve.ECRIT;
import static com.officedubac.project.modules.D.model.TypeEpreuve.ORAL;

/**
 * Référentiel figé des matières, coefficients et barèmes de la série D
 * (Mathématiques et Sciences de la Nature), tel qu'imprimé sur le gabarit officiel de
 * l'Office du Baccalauréat.
 */
public final class MatieresD {

    private MatieresD() { }

    // ---- 1er groupe d'épreuves ----
    public static final Matiere FR_ECRIT = new Matiere("FR_ECRIT", "Français (écrit)", 2, 40, ECRIT, PREMIER);
    public static final Matiere FR_ORAL = new Matiere("FR_ORAL", "Français (oral)", 1, 20, ORAL, PREMIER);
    public static final Matiere PHILO = new Matiere("PHILO", "Philosophie", 2, 40, ECRIT, PREMIER);
    public static final Matiere MATH = new Matiere("MATH", "Mathématiques", 4, 80, ECRIT, PREMIER);
    public static final Matiere SC_PHYS = new Matiere("SC_PHYS", "Sciences Physiques", 5, 100, ECRIT, PREMIER);
    public static final Matiere SC_NAT = new Matiere("SC_NAT", "Sciences Naturelles", 5, 100, ECRIT, PREMIER);
    public static final Matiere HIST_GEO = new Matiere("HIST_GEO", "Histoire et Géographie", 2, 40, ECRIT, PREMIER);
    public static final Matiere LV = new Matiere("LV", "Langue Vivante", 2, 40, ORAL, PREMIER);

    public static final List<Matiere> PREMIER_GROUPE = List.of(FR_ECRIT, FR_ORAL, PHILO, MATH, SC_PHYS, SC_NAT, HIST_GEO, LV);

    public static final int BAREME_PREMIER_GROUPE = 460;

    // ---- 2eme groupe d'épreuves (report du 1er groupe + épreuve de contrôle) ----
    // (aucune matière propre : le 2eme groupe ne comprend que le report et l'épreuve de contrôle)

    public static final List<Matiere> DEUXIEME_GROUPE = List.of();

    public static final int BAREME_DEUXIEME_GROUPE = 0;

    public static final int BAREME_TOTAL_DEFINITIF = BAREME_PREMIER_GROUPE + BAREME_DEUXIEME_GROUPE;

    public static Matiere findByCode(String code) {
        return java.util.stream.Stream.concat(PREMIER_GROUPE.stream(), DEUXIEME_GROUPE.stream())
                .filter(m -> m.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Matière inconnue pour la série D : " + code));
    }
}
