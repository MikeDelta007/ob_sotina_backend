package com.officedubac.project.modules.F2.model;

import java.util.List;

import static com.officedubac.project.modules.F2.model.Enums.TypeEpreuve.ECRIT;
import static com.officedubac.project.modules.F2.model.Enums.TypeEpreuve.ORAL;

/**
 * Référentiel FIXE des matières et coefficients de l'Option F2
 * (Electronique - Electrotechnique, Baccalauréat de Technicien), tel
 * qu'imprimé sur le "CERTIFICAT PROCES-VERBAL D'EXAMEN" de l'Office du
 * Baccalauréat - UCAD Dakar.
 *
 * Comme A3/B/D/E/F1, le barème complet (580) est atteint dès le 1er
 * groupe : le 2eme groupe ne comporte aucune matière écrite propre.
 */
public final class MatieresF2 {

    private MatieresF2() { }

    public static final Matiere FRANCAIS_ECRIT      = new Matiere("FR_ECRIT",       "Français (écrit)",              2, ECRIT);
    public static final Matiere FRANCAIS_ORAL       = new Matiere("FR_ORAL",        "Français (oral)",               1, ORAL);
    public static final Matiere MATHEMATIQUES       = new Matiere("MATH",           "Mathématiques",                 4, ECRIT);
    public static final Matiere ELECTROTECHNIQUE    = new Matiere("ELECTROTECHNI",  "Electrotechnique - Electronique", 4, ECRIT);
    public static final Matiere CONSTR_ELEC_MECA    = new Matiere("CONSTR_ELEC_MECA", "Construction Electrique et Mécanique", 4, ECRIT);
    public static final Matiere SHEMA_AUTOMATISMES  = new Matiere("SHEMA_AUTOMATIS", "Schéma et Automatismes",      4, ECRIT);
    public static final Matiere ETUDE_EQUIPEMENT    = new Matiere("ETUDE_EQUIPEMENT", "Etude d'Equipement",         2, ECRIT);
    public static final Matiere ANGLAIS             = new Matiere("ANGLAIS",        "Anglais",                       2, ORAL);
    public static final Matiere CABLAGE_TECHNO      = new Matiere("CABLAGE_TECHNO", "Câblage et Technologie",       3, ORAL);
    public static final Matiere ESSAIS_MESURES      = new Matiere("ESSAIS_MESURES", "Essais et Mesures",             3, ORAL);

    public static final List<Matiere> PREMIER_GROUPE = List.of(
            FRANCAIS_ECRIT, FRANCAIS_ORAL, MATHEMATIQUES, ELECTROTECHNIQUE, CONSTR_ELEC_MECA,
            SHEMA_AUTOMATISMES, ETUDE_EQUIPEMENT, ANGLAIS, CABLAGE_TECHNO, ESSAIS_MESURES
    );

    // barème : (2+1+4+4+4+4+2+2+3+3) * 20 = 580
    public static final int BAREME_PREMIER_GROUPE = 580;
    public static final int BAREME_TOTAL_DEFINITIF = BAREME_PREMIER_GROUPE; // même barème, pas de 2e groupe écrit

    public static Matiere findByCode(String code) {
        return PREMIER_GROUPE.stream()
                .filter(m -> m.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Matière inconnue pour l'option F2 : " + code));
    }
}
