package com.officedubac.project.modules.D_Deuxieme_Partie.model;

import java.util.List;
import java.util.stream.Stream;

import static com.officedubac.project.modules.D_Deuxieme_Partie.model.Enums.TypeEpreuve.ECRIT;
import static com.officedubac.project.modules.D_Deuxieme_Partie.model.Enums.TypeEpreuve.ORAL;

/**
 * Référentiel FIXE des matières et coefficients de la Série D - 2ème Partie
 * (PREMIERE SESSION), tel qu'imprimé sur le formulaire officiel
 * "RELEVE DE NOTES" de l'Office du Baccalauréat - Université de Dakar.
 *
 * Particularité (comme C 2ème Partie) : une ligne "EPS" (Education Physique
 * et Sportive) intervient comme un ajustement (+/-) au total général plutôt
 * que comme une matière notée — cf. TOTAL GENERAL : ECRIT + ORAL + EPS = /400.
 *
 * Particularité propre à ce formulaire : la 3ème épreuve orale est un choix
 * unique "Sc. Phy. / Sc. Nat." (le candidat passe l'oral de l'une des deux
 * matières, au choix), modélisé ici sous un seul code.
 */
public final class MatieresDDeuxiemePartie {

    private MatieresDDeuxiemePartie() { }

    // ---- Epreuves écrites (total sur 280) ----
    public static final Matiere PHILOSOPHIE        = new Matiere("PHILO",   "Philosophie",         2, ECRIT);
    public static final Matiere MATHEMATIQUES       = new Matiere("MATH",    "Mathématiques",       4, ECRIT);
    public static final Matiere SCIENCES_PHYSIQUES  = new Matiere("SC_PHY",  "Sciences Physiques",  4, ECRIT);
    public static final Matiere SCIENCES_NATURELLES = new Matiere("SC_NAT",  "Sciences Naturelles", 4, ECRIT);

    public static final List<Matiere> EPREUVES_ECRITES = List.of(
            PHILOSOPHIE, MATHEMATIQUES, SCIENCES_PHYSIQUES, SCIENCES_NATURELLES
    );

    // barème : (2+4+4+4) * 20 = 280
    public static final int BAREME_ECRIT = 280;

    // ---- Epreuves orales (total sur 120) ----
    public static final Matiere LV1                 = new Matiere("LV1",           "Langue Vivante 1",              2, ORAL);
    public static final Matiere HIST_GEO             = new Matiere("HIST_GEO",     "Histoire et Géographie",        2, ORAL);
    public static final Matiere SC_PHY_OU_NAT_ORAL   = new Matiere("SC_PHY_OU_NAT", "Sciences Physiques ou Naturelles (au choix)", 2, ORAL);

    public static final List<Matiere> EPREUVES_ORALES = List.of(LV1, HIST_GEO, SC_PHY_OU_NAT_ORAL);

    // barème : (2+2+2) * 20 = 120
    public static final int BAREME_ORAL = 120;

    // barème général (hors ajustement EPS) : 280 + 120 = 400
    public static final int BAREME_GENERAL = BAREME_ECRIT + BAREME_ORAL;

    public static Matiere findByCode(String code) {
        return Stream.concat(EPREUVES_ECRITES.stream(), EPREUVES_ORALES.stream())
                .filter(m -> m.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Matière inconnue pour la série D 2ème Partie : " + code));
    }
}
