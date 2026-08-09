package com.officedubac.project.modules.A4.model;

import java.util.List;

import static com.officedubac.project.modules.A4.model.GroupeEpreuves.DEUXIEME;
import static com.officedubac.project.modules.A4.model.GroupeEpreuves.PREMIER;
import static com.officedubac.project.modules.A4.model.TypeEpreuve.ECRIT;
import static com.officedubac.project.modules.A4.model.TypeEpreuve.ORAL;

/**
 * Référentiel figé des matières, coefficients et barèmes de la série A4
 * (Lettres - Langues Vivantes), tel qu'imprimé sur le gabarit officiel de
 * l'Office du Baccalauréat.
 */
public final class MatieresA4 {

    private MatieresA4() { }

    // ---- 1er groupe d'épreuves ----
    public static final Matiere FR_ECRIT = new Matiere("FR_ECRIT", "Français (écrit)", 3, 60, ECRIT, PREMIER);
    public static final Matiere FR_ORAL = new Matiere("FR_ORAL", "Français (oral)", 1, 20, ORAL, PREMIER);
    public static final Matiere PHILO = new Matiere("PHILO", "Philosophie", 4, 80, ECRIT, PREMIER);
    public static final Matiere LV1_ECRIT = new Matiere("LV1_ECRIT", "Langue Vivante I (écrit)", 3, 60, ECRIT, PREMIER);
    public static final Matiere HIST_GEO = new Matiere("HIST_GEO", "Histoire et Géographie", 3, 60, ECRIT, PREMIER);
    public static final Matiere MATH = new Matiere("MATH", "Mathématiques", 3, 60, ECRIT, PREMIER);

    public static final List<Matiere> PREMIER_GROUPE = List.of(FR_ECRIT, FR_ORAL, PHILO, LV1_ECRIT, HIST_GEO, MATH);

    public static final int BAREME_PREMIER_GROUPE = 340;

    // ---- 2eme groupe d'épreuves (report du 1er groupe + épreuve de contrôle + matières propres) ----
    public static final Matiere LANGUE_VIVANTE = new Matiere("LANGUE_VIVANTE", "Langue Vivante (2eme groupe)", 3, 60, ORAL, DEUXIEME);

    public static final List<Matiere> DEUXIEME_GROUPE = List.of(LANGUE_VIVANTE);

    public static final int BAREME_DEUXIEME_GROUPE = 60;

    public static final int BAREME_TOTAL_DEFINITIF = BAREME_PREMIER_GROUPE + BAREME_DEUXIEME_GROUPE;

    public static Matiere findByCode(String code) {
        return java.util.stream.Stream.concat(PREMIER_GROUPE.stream(), DEUXIEME_GROUPE.stream())
                .filter(m -> m.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Matière inconnue pour la série A4 : " + code));
    }
}
