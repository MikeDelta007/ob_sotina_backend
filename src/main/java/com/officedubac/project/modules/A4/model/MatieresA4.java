package com.officedubac.project.modules.A4.model;

import java.util.List;

import static com.officedubac.project.modules.A4.model.Enums.TypeEpreuve.ECRIT;
import static com.officedubac.project.modules.A4.model.Enums.TypeEpreuve.ORAL;

/**
 * Référentiel FIXE des matières et coefficients de la Série A4, tel
 * qu'imprimé sur le formulaire officiel "RELEVE DE NOTES" de l'Office du
 * Baccalauréat - Université de Dakar.
 */
public final class MatieresA4 {

    private MatieresA4() { }

    // ---- 1er groupe d'épreuves (barème 340) ----
    public static final Matiere FRANCAIS_ECRIT = new Matiere("FR_ECRIT",  "Français (écrit)",       3, ECRIT);
    public static final Matiere FRANCAIS_ORAL  = new Matiere("FR_ORAL",   "Français (oral)",        1, ORAL);
    public static final Matiere PHILOSOPHIE    = new Matiere("PHILO",     "Philosophie",            4, ECRIT);
    public static final Matiere LV1_ECRIT      = new Matiere("LV1_ECRIT", "1ère Langue Vivante (écrit)", 3, ECRIT);
    public static final Matiere HIST_GEO       = new Matiere("HIST_GEO",  "Histoire et Géographie", 3, ECRIT);
    public static final Matiere MATH_ORAL      = new Matiere("MATH",      "Mathématiques (oral)",   3, ORAL);

    public static final List<Matiere> PREMIER_GROUPE = List.of(
            FRANCAIS_ECRIT, FRANCAIS_ORAL, PHILOSOPHIE, LV1_ECRIT, HIST_GEO, MATH_ORAL
    );

    // barème : (3+1+4+3+3+3) * 20 = 340
    public static final int BAREME_PREMIER_GROUPE = 340;

    // ---- 2eme groupe d'épreuves (report 340 + cette épreuve = 400) ----
    public static final Matiere LV2 = new Matiere("LV2", "2ème Langue Vivante", 3, ECRIT);

    public static final List<Matiere> DEUXIEME_GROUPE = List.of(LV2);

    // barème : 3 * 20 = 60
    public static final int BAREME_DEUXIEME_GROUPE = 60;

    // barème définitif : 340 + 60 = 400
    public static final int BAREME_TOTAL_DEFINITIF = BAREME_PREMIER_GROUPE + BAREME_DEUXIEME_GROUPE;

    public static Matiere findByCode(String code) {
        return java.util.stream.Stream.concat(PREMIER_GROUPE.stream(), DEUXIEME_GROUPE.stream())
                .filter(m -> m.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Matière inconnue pour la série A4 : " + code));
    }
}
