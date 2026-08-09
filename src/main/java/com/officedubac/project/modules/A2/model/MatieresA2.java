package com.officedubac.project.modules.A2.model;

import java.util.List;

import static com.officedubac.project.modules.A2.model.GroupeEpreuves.DEUXIEME;
import static com.officedubac.project.modules.A2.model.GroupeEpreuves.PREMIER;
import static com.officedubac.project.modules.A2.model.TypeEpreuve.ECRIT;
import static com.officedubac.project.modules.A2.model.TypeEpreuve.ORAL;

public final class MatieresA2 {

    private MatieresA2() { }

    // ---- 1er groupe d'épreuves (total sur 320) ----
    public static final Matiere FRANCAIS_ECRIT   = new Matiere("FR_ECRIT",   "Français (écrit)",        3, ECRIT, PREMIER);
    public static final Matiere FRANCAIS_ORAL    = new Matiere("FR_ORAL",    "Français (oral)",         1, ORAL,  PREMIER);
    public static final Matiere PHILOSOPHIE      = new Matiere("PHILO",     "Philosophie (écrit)",      4, ECRIT, PREMIER);
    public static final Matiere LATIN_GREC_1     = new Matiere("LAT_GREC1", "Latin ou Grec (écrit)",    3, ECRIT, PREMIER);
    public static final Matiere HIST_GEO         = new Matiere("HIST_GEO",  "Histoire et Géographie (oral)", 3, ORAL, PREMIER);
    public static final Matiere LANGUE_VIVANTE   = new Matiere("LV",        "Langue Vivante (oral)",    2, ORAL,  PREMIER);

    public static final List<Matiere> PREMIER_GROUPE = List.of(
            FRANCAIS_ECRIT, FRANCAIS_ORAL, PHILOSOPHIE, LATIN_GREC_1, HIST_GEO, LANGUE_VIVANTE
    );

    // barème théorique du 1er groupe : (3+1+4+3+3+2) * 20 = 320
    public static final int BAREME_PREMIER_GROUPE = 320;

    // ---- 2eme groupe d'épreuves (report 320 + ces épreuves = total définitif 400) ----
    public static final Matiere GREC_LATIN_2     = new Matiere("LAT_GREC2", "Grec ou Latin",  2, ECRIT, DEUXIEME);
    public static final Matiere MATHEMATIQUES    = new Matiere("MATH",      "Mathématiques",  2, ECRIT, DEUXIEME);

    public static final List<Matiere> DEUXIEME_GROUPE = List.of(GREC_LATIN_2, MATHEMATIQUES);

    // barème théorique du 2eme groupe : (2+2) * 20 = 40
    public static final int BAREME_DEUXIEME_GROUPE = 40;

    // barème définitif : 320 (report) + 40 (2eme groupe) = 400
    public static final int BAREME_TOTAL_DEFINITIF = BAREME_PREMIER_GROUPE + BAREME_DEUXIEME_GROUPE;

    public static Matiere findByCode(String code) {
        return java.util.stream.Stream.concat(PREMIER_GROUPE.stream(), DEUXIEME_GROUPE.stream())
                .filter(m -> m.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Matière inconnue pour l'option A1 : " + code));
    }
}
