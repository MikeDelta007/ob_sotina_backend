package com.officedubac.project.modules.A3.model;

import java.util.List;

import static com.officedubac.project.modules.A3.model.Enums.TypeEpreuve.ECRIT;
import static com.officedubac.project.modules.A3.model.Enums.TypeEpreuve.ORAL;

/**
 * Référentiel FIXE des matières et coefficients de la Série A3
 * (Lettres - Sciences Humaines), tel qu'imprimé sur le formulaire officiel
 * "RELEVE DE NOTES" de l'Office du Baccalauréat - UCAD Dakar.
 *
 * Contrairement à la série A1, le barème complet (480 points) est atteint
 * dès le 1er groupe d'épreuves : il n'y a pas de matières écrites propres
 * au 2eme groupe. Le 2eme groupe ne fait que reporter ce total et y ajouter
 * les éventuels points de contrôle oral / éducation physique / facultatives.
 */
public final class MatieresA3 {

    private MatieresA3() { }

    public static final Matiere FRANCAIS_ECRIT = new Matiere("FR_ECRIT",  "Français (écrit)",        3, ECRIT);
    public static final Matiere FRANCAIS_ORAL  = new Matiere("FR_ORAL",   "Français (oral)",         2, ORAL);
    public static final Matiere PHILOSOPHIE    = new Matiere("PHILO",     "Philosophie",             4, ECRIT);
    public static final Matiere HIST_GEO       = new Matiere("HIST_GEO",  "Histoire et Géographie",  4, ECRIT);
    public static final Matiere LV1_ECRIT      = new Matiere("LV1_ECRIT", "Langue Vivante I (écrit)", 3, ECRIT);
    public static final Matiere MATHEMATIQUES  = new Matiere("MATH",      "Mathématiques",           3, ECRIT);
    public static final Matiere LV2            = new Matiere("LV2",       "Langue Vivante II",       3, ECRIT);
    public static final Matiere LV1_ORAL       = new Matiere("LV1_ORAL",  "Langue Vivante I (oral)", 2, ORAL);

    public static final List<Matiere> PREMIER_GROUPE = List.of(
            FRANCAIS_ECRIT, FRANCAIS_ORAL, PHILOSOPHIE, HIST_GEO, LV1_ECRIT, MATHEMATIQUES, LV2, LV1_ORAL
    );

    // barème : (3+2+4+4+3+3+3+2) * 20 = 480
    public static final int BAREME_PREMIER_GROUPE = 480;
    public static final int BAREME_TOTAL_DEFINITIF = BAREME_PREMIER_GROUPE; // même barème, pas de 2e groupe écrit

    public static Matiere findByCode(String code) {
        return PREMIER_GROUPE.stream()
                .filter(m -> m.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Matière inconnue pour la série A3 : " + code));
    }
}
