package com.officedubac.project.modules.L1A.model;

import java.util.List;

import static com.officedubac.project.modules.L1A.model.GroupeEpreuves.DEUXIEME;
import static com.officedubac.project.modules.L1A.model.GroupeEpreuves.PREMIER;
import static com.officedubac.project.modules.L1A.model.TypeEpreuve.ECRIT;
import static com.officedubac.project.modules.L1A.model.TypeEpreuve.ORAL;

/**
 * Référentiel figé des matières, coefficients et barèmes de la série L1A
 * (Lettres Classiques (Latin-Grec)), tel qu'imprimé sur le gabarit officiel de
 * l'Office du Baccalauréat.
 */
public final class MatieresL1A {

    private MatieresL1A() { }

    // ---- 1er groupe d'épreuves ----
    public static final Matiere FRANCAIS = new Matiere("FRANCAIS", "Français", 6, 120, ECRIT, PREMIER);
    public static final Matiere PHILO = new Matiere("PHILO", "Philosophie", 6, 120, ECRIT, PREMIER);
    public static final Matiere HIST_GEO = new Matiere("HIST_GEO", "Histoire et Géographie", 2, 40, ECRIT, PREMIER);
    public static final Matiere LV1 = new Matiere("LV1", "Langue Vivante I", 2, 40, ECRIT, PREMIER);
    public static final Matiere MATH = new Matiere("MATH", "Mathématiques", 2, 40, ECRIT, PREMIER);
    public static final Matiere GREC = new Matiere("GREC", "Grec", 5, 100, ECRIT, PREMIER);
    public static final Matiere LATIN_ARABE = new Matiere("LATIN_ARABE", "Latin ou Arabe Classique", 5, 100, ECRIT, PREMIER);

    public static final List<Matiere> PREMIER_GROUPE = List.of(FRANCAIS, PHILO, HIST_GEO, LV1, MATH, GREC, LATIN_ARABE);

    public static final int BAREME_PREMIER_GROUPE = 560;

    // ---- 2eme groupe d'épreuves (report du 1er groupe + épreuve de contrôle) ----
    // (aucune matière propre : le 2eme groupe ne comprend que le report et l'épreuve de contrôle)

    public static final List<Matiere> DEUXIEME_GROUPE = List.of();

    public static final int BAREME_DEUXIEME_GROUPE = 0;

    public static final int BAREME_TOTAL_DEFINITIF = BAREME_PREMIER_GROUPE + BAREME_DEUXIEME_GROUPE;

    public static Matiere findByCode(String code) {
        return java.util.stream.Stream.concat(PREMIER_GROUPE.stream(), DEUXIEME_GROUPE.stream())
                .filter(m -> m.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Matière inconnue pour la série L1A : " + code));
    }
}
