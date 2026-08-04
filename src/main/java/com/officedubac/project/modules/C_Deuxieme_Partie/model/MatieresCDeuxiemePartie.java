package com.officedubac.project.modules.C_Deuxieme_Partie.model;

import java.util.List;
import java.util.stream.Stream;

import static com.officedubac.project.modules.C_Deuxieme_Partie.model.Enums.TypeEpreuve.ECRIT;
import static com.officedubac.project.modules.C_Deuxieme_Partie.model.Enums.TypeEpreuve.ORAL;

/**
 * Référentiel FIXE des matières et coefficients de la Série C - 2ème Partie
 * (PREMIERE SESSION), tel qu'imprimé sur le formulaire officiel
 * "RELEVE DE NOTES" de l'Office du Baccalauréat - Université de Dakar.
 *
 * Particularité de ce formulaire : une ligne "EPS" (Education Physique et
 * Sportive) intervient en plus de l'écrit et de l'oral, comme un ajustement
 * (+/-) au total général plutôt que comme une matière notée avec barème
 * propre — cf. TOTAL GENERAL : ECRIT + ORAL + EPS = /400.
 */
public final class MatieresCDeuxiemePartie {

    private MatieresCDeuxiemePartie() { }

    // ---- Epreuves écrites (total sur 280) ----
    public static final Matiere PHILOSOPHIE     = new Matiere("PHILO",       "Philosophie",           2, ECRIT);
    public static final Matiere MATHEMATIQUES_ECRIT = new Matiere("MATH_ECRIT", "Mathématiques (écrit)", 5, ECRIT);
    public static final Matiere SCIENCES_PHYSIQUES  = new Matiere("SC_PHY",   "Sciences Physiques",    5, ECRIT);
    public static final Matiere SCIENCES_NATURELLES = new Matiere("SC_NAT",   "Sciences Naturelles",   2, ECRIT);

    public static final List<Matiere> EPREUVES_ECRITES = List.of(
            PHILOSOPHIE, MATHEMATIQUES_ECRIT, SCIENCES_PHYSIQUES, SCIENCES_NATURELLES
    );

    // barème : (2+5+5+2) * 20 = 280
    public static final int BAREME_ECRIT = 280;

    // ---- Epreuves orales (total sur 120) ----
    public static final Matiere LV1                 = new Matiere("LV1",         "Langue Vivante 1",      2, ORAL);
    public static final Matiere HIST_GEO             = new Matiere("HIST_GEO",   "Histoire et Géographie", 2, ORAL);
    public static final Matiere MATHEMATIQUES_ORAL   = new Matiere("MATH_ORAL",  "Mathématiques (oral)",  2, ORAL);

    public static final List<Matiere> EPREUVES_ORALES = List.of(LV1, HIST_GEO, MATHEMATIQUES_ORAL);

    // barème : (2+2+2) * 20 = 120
    public static final int BAREME_ORAL = 120;

    // barème général (hors ajustement EPS) : 280 + 120 = 400
    public static final int BAREME_GENERAL = BAREME_ECRIT + BAREME_ORAL;

    public static Matiere findByCode(String code) {
        return Stream.concat(EPREUVES_ECRITES.stream(), EPREUVES_ORALES.stream())
                .filter(m -> m.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Matière inconnue pour la série C 2ème Partie : " + code));
    }
}
