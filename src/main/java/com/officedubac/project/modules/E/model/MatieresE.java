package com.officedubac.project.modules.E.model;

import java.util.List;

import static com.officedubac.project.modules.E.model.Enums.TypeEpreuve.ECRIT;
import static com.officedubac.project.modules.E.model.Enums.TypeEpreuve.ORAL;

/**
 * Référentiel FIXE des matières et coefficients de la Série E, tel
 * qu'imprimé sur le formulaire officiel "RELEVE DE NOTES" de l'Office du
 * Baccalauréat - UCAD Dakar.
 *
 * Comme A3, B et D, le barème complet (640 points) est atteint dès le 1er
 * groupe d'épreuves : le 2eme groupe ne comporte aucune matière écrite
 * propre, il ne fait que reporter ce total et l'ajuster.
 */
public final class MatieresE {

    private MatieresE() { }

    public static final Matiere FRANCAIS_ECRIT   = new Matiere("FR_ECRIT",   "Français (écrit)",              2, ECRIT);
    public static final Matiere FRANCAIS_ORAL    = new Matiere("FR_ORAL",    "Français (oral)",               1, ORAL);
    public static final Matiere PHILOSOPHIE      = new Matiere("PHILO",      "Philosophie",                   2, ECRIT);
    public static final Matiere MATHEMATIQUES    = new Matiere("MATH",       "Mathématiques",                 7, ECRIT);
    public static final Matiere SCIENCES_PHYSIQUES = new Matiere("SC_PHY",   "Sciences Physiques",            7, ECRIT);
    public static final Matiere CONSTRUCTION_MECANIQUE = new Matiere("CONSTR_MECA", "Construction Mécanique", 6, ECRIT);
    public static final Matiere ANALYSE_FABRICATION_TECHNO = new Matiere("AN_FABR_TECHNO", "Analyse de Fabrication, Technologie et Automatismes", 2, ECRIT);
    public static final Matiere TECHNIQUE_PRATIQUE = new Matiere("TECH_PRATIQUE", "Technique Pratique",       3, ECRIT);
    public static final Matiere LV                = new Matiere("LV",        "Langue Vivante",                2, ECRIT);

    public static final List<Matiere> PREMIER_GROUPE = List.of(
            FRANCAIS_ECRIT, FRANCAIS_ORAL, PHILOSOPHIE, MATHEMATIQUES, SCIENCES_PHYSIQUES,
            CONSTRUCTION_MECANIQUE, ANALYSE_FABRICATION_TECHNO, TECHNIQUE_PRATIQUE, LV
    );

    // barème : (2+1+2+7+7+6+2+3+2) * 20 = 640
    public static final int BAREME_PREMIER_GROUPE = 640;
    public static final int BAREME_TOTAL_DEFINITIF = BAREME_PREMIER_GROUPE; // même barème, pas de 2e groupe écrit

    public static Matiere findByCode(String code) {
        return PREMIER_GROUPE.stream()
                .filter(m -> m.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Matière inconnue pour la série E : " + code));
    }
}
