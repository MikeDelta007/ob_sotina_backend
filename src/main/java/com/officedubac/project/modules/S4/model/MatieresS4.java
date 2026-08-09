package com.officedubac.project.modules.S4.model;

import java.util.List;

import static com.officedubac.project.modules.S4.model.GroupeEpreuves.DEUXIEME;
import static com.officedubac.project.modules.S4.model.GroupeEpreuves.PREMIER;
import static com.officedubac.project.modules.S4.model.TypeEpreuve.ECRIT;
import static com.officedubac.project.modules.S4.model.TypeEpreuve.ORAL;

/**
 * Référentiel figé des matières, coefficients et barèmes de la série S4
 * (Sciences Agricoles (Zootechnique)), tel qu'imprimé sur le gabarit officiel de
 * l'Office du Baccalauréat.
 */
public final class MatieresS4 {

    private MatieresS4() { }

    // ---- 1er groupe d'épreuves ----
    public static final Matiere PHILO = new Matiere("PHILO", "Philosophie", 2, 40, ECRIT, PREMIER);
    public static final Matiere MATH = new Matiere("MATH", "Mathématiques", 5, 100, ECRIT, PREMIER);
    public static final Matiere SC_PHYS = new Matiere("SC_PHYS", "Sciences Physiques", 5, 100, ECRIT, PREMIER);
    public static final Matiere SVT = new Matiere("SVT", "Sciences de la Vie et de la Terre", 5, 100, ECRIT, PREMIER);
    public static final Matiere FRANCAIS = new Matiere("FRANCAIS", "Français", 3, 60, ECRIT, PREMIER);
    public static final Matiere HIST_GEO = new Matiere("HIST_GEO", "Histoire et Géographie", 2, 40, ECRIT, PREMIER);
    public static final Matiere ANGLAIS = new Matiere("ANGLAIS", "Anglais", 2, 40, ORAL, PREMIER);
    public static final Matiere ECOLOGIE = new Matiere("ECOLOGIE", "Ecologie / Environnement", 2, 40, ECRIT, PREMIER);
    public static final Matiere ZOOTECHNIQUE = new Matiere("ZOOTECHNIQUE", "Zootechnique", 2, 40, ECRIT, PREMIER);
    public static final Matiere PHYTOTECHNIQUE = new Matiere("PHYTOTECHNIQUE", "Phytotechnique", 6, 120, ECRIT, PREMIER);

    public static final List<Matiere> PREMIER_GROUPE = List.of(PHILO, MATH, SC_PHYS, SVT, FRANCAIS, HIST_GEO, ANGLAIS, ECOLOGIE, ZOOTECHNIQUE, PHYTOTECHNIQUE);

    public static final int BAREME_PREMIER_GROUPE = 680;

    // ---- 2eme groupe d'épreuves (report du 1er groupe + épreuve de contrôle) ----
    // (aucune matière propre : le 2eme groupe ne comprend que le report et l'épreuve de contrôle)

    public static final List<Matiere> DEUXIEME_GROUPE = List.of();

    public static final int BAREME_DEUXIEME_GROUPE = 0;

    public static final int BAREME_TOTAL_DEFINITIF = BAREME_PREMIER_GROUPE + BAREME_DEUXIEME_GROUPE;

    public static Matiere findByCode(String code) {
        return java.util.stream.Stream.concat(PREMIER_GROUPE.stream(), DEUXIEME_GROUPE.stream())
                .filter(m -> m.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Matière inconnue pour la série S4 : " + code));
    }
}
