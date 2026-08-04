package com.officedubac.project.modules.B.model;

import java.util.List;

import static com.officedubac.project.modules.B.model.Enums.TypeEpreuve.ECRIT;
import static com.officedubac.project.modules.B.model.Enums.TypeEpreuve.ORAL;

/**
 * Référentiel FIXE des matières et coefficients de la Série B
 * (Sciences Economiques et Sociales), tel qu'imprimé sur le formulaire
 * officiel "RELEVE DE NOTES" de l'Office du Baccalauréat - UCAD Dakar.
 *
 * Comme pour la série A3, le barème complet (500 points) est atteint dès
 * le 1er groupe d'épreuves : le 2eme groupe ne comporte aucune matière
 * écrite propre, il ne fait que reporter ce total et l'ajuster.
 */
public final class MatieresB {

    private MatieresB() { }

    public static final Matiere FRANCAIS_ECRIT = new Matiere("FR_ECRIT",  "Français (écrit)",              3, ECRIT);
    public static final Matiere FRANCAIS_ORAL  = new Matiere("FR_ORAL",   "Français (oral)",               1, ORAL);
    public static final Matiere PHILOSOPHIE    = new Matiere("PHILO",     "Philosophie",                   3, ECRIT);
    public static final Matiere HIST_GEO       = new Matiere("HIST_GEO",  "Histoire et Géographie",        3, ECRIT);
    public static final Matiere SCIENCES_ECO   = new Matiere("SCI_ECO",   "Sciences Economiques et Sociales", 5, ECRIT);
    public static final Matiere MATHEMATIQUES  = new Matiere("MATH",      "Mathématiques",                 5, ECRIT);
    public static final Matiere LV1            = new Matiere("LV1",       "Langue Vivante I",              3, ECRIT);
    public static final Matiere LV2            = new Matiere("LV2",       "Langue Vivante II",             2, ORAL);

    public static final List<Matiere> PREMIER_GROUPE = List.of(
            FRANCAIS_ECRIT, FRANCAIS_ORAL, PHILOSOPHIE, HIST_GEO, SCIENCES_ECO, MATHEMATIQUES, LV1, LV2
    );

    // barème : (3+1+3+3+5+5+3+2) * 20 = 500
    public static final int BAREME_PREMIER_GROUPE = 500;
    public static final int BAREME_TOTAL_DEFINITIF = BAREME_PREMIER_GROUPE; // même barème, pas de 2e groupe écrit

    public static Matiere findByCode(String code) {
        return PREMIER_GROUPE.stream()
                .filter(m -> m.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Matière inconnue pour la série B : " + code));
    }
}
