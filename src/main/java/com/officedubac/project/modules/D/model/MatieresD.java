package com.officedubac.project.modules.D.model;

import java.util.List;

import static com.officedubac.project.modules.D.model.Enums.TypeEpreuve.ECRIT;
import static com.officedubac.project.modules.D.model.Enums.TypeEpreuve.ORAL;

/**
 * Référentiel FIXE des matières et coefficients de la Série D, tel
 * qu'imprimé sur le formulaire officiel "RELEVE DE NOTES" de l'Office du
 * Baccalauréat - UCAD Dakar.
 *
 * Comme A3 et B, le barème complet (460 points) est atteint dès le 1er
 * groupe d'épreuves : le 2eme groupe ne comporte aucune matière écrite
 * propre, il ne fait que reporter ce total et l'ajuster.
 */
public final class MatieresD {

    private MatieresD() { }

    public static final Matiere FRANCAIS_ECRIT = new Matiere("FR_ECRIT",  "Français (écrit)",       2, ECRIT);
    public static final Matiere FRANCAIS_ORAL  = new Matiere("FR_ORAL",   "Français (oral)",        1, ORAL);
    public static final Matiere PHILOSOPHIE    = new Matiere("PHILO",     "Philosophie",            2, ECRIT);
    public static final Matiere MATHEMATIQUES  = new Matiere("MATH",      "Mathématiques",          4, ECRIT);
    public static final Matiere SCIENCES_PHYSIQUES  = new Matiere("SC_PHY", "Sciences Physiques",   5, ECRIT);
    public static final Matiere SCIENCES_NATURELLES = new Matiere("SC_NAT", "Sciences Naturelles",  5, ECRIT);
    public static final Matiere HIST_GEO       = new Matiere("HIST_GEO",  "Histoire et Géographie", 2, ECRIT);
    public static final Matiere LV             = new Matiere("LV",        "Langue Vivante",         2, ECRIT);

    public static final List<Matiere> PREMIER_GROUPE = List.of(
            FRANCAIS_ECRIT, FRANCAIS_ORAL, PHILOSOPHIE, MATHEMATIQUES,
            SCIENCES_PHYSIQUES, SCIENCES_NATURELLES, HIST_GEO, LV
    );

    // barème : (2+1+2+4+5+5+2+2) * 20 = 460
    public static final int BAREME_PREMIER_GROUPE = 460;
    public static final int BAREME_TOTAL_DEFINITIF = BAREME_PREMIER_GROUPE; // même barème, pas de 2e groupe écrit

    public static Matiere findByCode(String code) {
        return PREMIER_GROUPE.stream()
                .filter(m -> m.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Matière inconnue pour la série D : " + code));
    }
}
