package com.officedubac.project.modules.F1.model;

import java.util.List;

import static com.officedubac.project.modules.F1.model.Enums.TypeEpreuve.ECRIT;
import static com.officedubac.project.modules.F1.model.Enums.TypeEpreuve.ORAL;

/**
 * Référentiel FIXE des matières et coefficients de la Série F1, tel
 * qu'imprimé sur le formulaire officiel "RELEVE DE NOTES" de l'Office du
 * Baccalauréat - UCAD Dakar.
 *
 * Comme A3, B, D et E, le barème complet (580 points) est atteint dès le
 * 1er groupe d'épreuves : le 2eme groupe ne comporte aucune matière écrite
 * propre, il ne fait que reporter ce total et l'ajuster.
 */
public final class MatieresF1 {

    private MatieresF1() { }

    public static final Matiere FRANCAIS_ECRIT   = new Matiere("FR_ECRIT",      "Français (écrit)",              2, ECRIT);
    public static final Matiere FRANCAIS_ORAL    = new Matiere("FR_ORAL",       "Français (oral)",               1, ORAL);
    public static final Matiere MATHEMATIQUES    = new Matiere("MATH",          "Mathématiques",                 4, ECRIT);
    public static final Matiere MECANIQUE        = new Matiere("MECANIQUE",     "Mécanique",                     4, ECRIT);
    public static final Matiere CONSTRUCTION_MECANIQUE = new Matiere("CONSTR_MECA", "Construction Mécanique",    4, ECRIT);
    public static final Matiere ANALYSE_FABRICATION = new Matiere("ANALYSE_FABR", "Analyse de Fabrication",     4, ECRIT);
    public static final Matiere ELECTRICITE_METAL = new Matiere("ELEC_METAL",   "Electricité - Métal",          2, ECRIT);
    public static final Matiere TECHNOLOGIE_AUTOMATISMES = new Matiere("TECHNO_AUTOM", "Technologie - Automatismes", 2, ECRIT);
    public static final Matiere ANGLAIS          = new Matiere("ANGLAIS",       "Anglais",                       2, ORAL);
    public static final Matiere EPREUVE_PRATIQUE = new Matiere("EPREUVE_PRATIQUE", "Epreuve Pratique",           4, ECRIT);

    public static final List<Matiere> PREMIER_GROUPE = List.of(
            FRANCAIS_ECRIT, FRANCAIS_ORAL, MATHEMATIQUES, MECANIQUE, CONSTRUCTION_MECANIQUE,
            ANALYSE_FABRICATION, ELECTRICITE_METAL, TECHNOLOGIE_AUTOMATISMES, ANGLAIS, EPREUVE_PRATIQUE
    );

    // barème : (2+1+4+4+4+4+2+2+2+4) * 20 = 580
    public static final int BAREME_PREMIER_GROUPE = 580;
    public static final int BAREME_TOTAL_DEFINITIF = BAREME_PREMIER_GROUPE; // même barème, pas de 2e groupe écrit

    public static Matiere findByCode(String code) {
        return PREMIER_GROUPE.stream()
                .filter(m -> m.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Matière inconnue pour la série F1 : " + code));
    }
}
