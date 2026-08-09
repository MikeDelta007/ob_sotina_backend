package com.officedubac.project.modules.a1deuxiemepartie.model;

import java.util.List;
import java.util.stream.Stream;

import static com.officedubac.project.modules.a1deuxiemepartie.model.Enums.TypeEpreuve.ECRIT;
import static com.officedubac.project.modules.a1deuxiemepartie.model.Enums.TypeEpreuve.ORAL;

/**
 * Référentiel FIXE des matières et coefficients de la Série A1 - 2ème Partie
 * (DEUXIEME SESSION), tel qu'imprimé sur le nouveau gabarit officiel
 * "RELEVE DE NOTES" de l'Office du Baccalauréat.
 */
public final class MatieresA1DeuxiemePartie {

    private MatieresA1DeuxiemePartie() { }

    // ---- Epreuves écrites (total sur 220) ----
    public static final Matiere PHILOSOPHIE = new Matiere("PHILO",   "Philosophie",     5, ECRIT);
    public static final Matiere LATIN_ARABE = new Matiere("LAT_AR",  "Latin - Arabe",   2, ECRIT);
    public static final Matiere GREC        = new Matiere("GREC",    "Grec",            2, ECRIT);
    public static final Matiere LANGUE_VIVANTE = new Matiere("LV",   "Langue Vivante",  2, ECRIT);

    public static final List<Matiere> EPREUVES_ECRITES = List.of(PHILOSOPHIE, LATIN_ARABE, GREC, LANGUE_VIVANTE);

    public static final int BAREME_ECRIT = 220;

    // ---- Epreuves orales (total sur 120) ----
    public static final Matiere LATIN_GREC_ARABE_ORAL = new Matiere("LAT_GR_AR_ORAL", "Latin - Grec ou Arabe", 2, ORAL);
    public static final Matiere HIST_GEO               = new Matiere("HIST_GEO",       "Histoire et Géographie", 2, ORAL);
    public static final Matiere MATHEMATIQUES           = new Matiere("MATH",           "Mathématiques", 2, ORAL);

    public static final List<Matiere> EPREUVES_ORALES = List.of(LATIN_GREC_ARABE_ORAL, HIST_GEO, MATHEMATIQUES);

    public static final int BAREME_ORAL = 120;

    public static final int BAREME_GENERAL = BAREME_ECRIT + BAREME_ORAL;

    public static Matiere findByCode(String code) {
        return Stream.concat(EPREUVES_ECRITES.stream(), EPREUVES_ORALES.stream())
                .filter(m -> m.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Matière inconnue pour la série A1 2ème Partie : " + code));
    }
}
