package com.officedubac.project.modules.F7.model;

import java.util.List;

import static com.officedubac.project.modules.F7.model.GroupeEpreuves.DEUXIEME;
import static com.officedubac.project.modules.F7.model.GroupeEpreuves.PREMIER;
import static com.officedubac.project.modules.F7.model.TypeEpreuve.ECRIT;
import static com.officedubac.project.modules.F7.model.TypeEpreuve.ORAL;

/**
 * Référentiel figé des matières, coefficients et barèmes de la série F7
 * (Sciences Biologiques), tel qu'imprimé sur le gabarit officiel de
 * l'Office du Baccalauréat.
 */
public final class MatieresF7 {

    private MatieresF7() { }

    // ---- 1er groupe d'épreuves ----
    public static final Matiere FR_ECRIT = new Matiere("FR_ECRIT", "Français (écrit)", 2, 40, ECRIT, PREMIER);
    public static final Matiere FR_ORAL = new Matiere("FR_ORAL", "Français (oral)", 1, 20, ORAL, PREMIER);
    public static final Matiere MATH = new Matiere("MATH", "Mathématiques", 2, 40, ECRIT, PREMIER);
    public static final Matiere BIOLOGIE = new Matiere("BIOLOGIE", "Biologie", 4, 80, ECRIT, PREMIER);
    public static final Matiere BIOCHIMIE = new Matiere("BIOCHIMIE", "Biochimie", 4, 80, ECRIT, PREMIER);
    public static final Matiere MICROBIO = new Matiere("MICROBIO", "Microbiologie et Immunologie", 5, 100, ECRIT, PREMIER);
    public static final Matiere PHYSIOLOGIE = new Matiere("PHYSIOLOGIE", "Physiologie", 3, 60, ECRIT, PREMIER);
    public static final Matiere LV = new Matiere("LV", "Langue Vivante", 2, 40, ORAL, PREMIER);
    public static final Matiere TP_BIOLOGIE = new Matiere("TP_BIOLOGIE", "Travaux Pratiques de Biologie", 6, 60, ECRIT, PREMIER);
    public static final Matiere TP_BIOCHIMIE = new Matiere("TP_BIOCHIMIE", "Travaux Pratiques de Biochimie", 4, 80, ECRIT, PREMIER);

    public static final List<Matiere> PREMIER_GROUPE = List.of(FR_ECRIT, FR_ORAL, MATH, BIOLOGIE, BIOCHIMIE, MICROBIO, PHYSIOLOGIE, LV, TP_BIOLOGIE, TP_BIOCHIMIE);

    public static final int BAREME_PREMIER_GROUPE = 600;

    // ---- 2eme groupe d'épreuves (report du 1er groupe + épreuve de contrôle) ----
    // (aucune matière propre : le 2eme groupe ne comprend que le report et l'épreuve de contrôle)

    public static final List<Matiere> DEUXIEME_GROUPE = List.of();

    public static final int BAREME_DEUXIEME_GROUPE = 0;

    public static final int BAREME_TOTAL_DEFINITIF = BAREME_PREMIER_GROUPE + BAREME_DEUXIEME_GROUPE;

    public static Matiere findByCode(String code) {
        return java.util.stream.Stream.concat(PREMIER_GROUPE.stream(), DEUXIEME_GROUPE.stream())
                .filter(m -> m.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Matière inconnue pour la série F7 : " + code));
    }
}
