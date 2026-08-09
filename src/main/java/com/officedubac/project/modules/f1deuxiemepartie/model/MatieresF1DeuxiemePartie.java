package com.officedubac.project.modules.f1deuxiemepartie.model;

import java.util.List;
import java.util.stream.Stream;

import static com.officedubac.project.modules.f1deuxiemepartie.model.Enums.TypeEpreuve.ECRIT;
import static com.officedubac.project.modules.f1deuxiemepartie.model.Enums.TypeEpreuve.ORAL;

/**
 * Référentiel figé des matières, coefficients et barèmes de la série F1-2emePartie
 * (F1 - 2ème Partie), tel qu'imprimé sur le gabarit officiel.
 */
public final class MatieresF1DeuxiemePartie {

    private MatieresF1DeuxiemePartie() { }

    // ---- Epreuves écrites ----
    public static final Matiere MATHS = new Matiere("MATHS", "Mathématiques", 4, 80, ECRIT);
    public static final Matiere ELECTRICITE_METAL = new Matiere("ELECTRICITE_METAL", "Electricité - Métallurgie", 2, 60, ECRIT);
    public static final Matiere MECANIQUE = new Matiere("MECANIQUE", "Mécanique", 3, 60, ECRIT);
    public static final Matiere ETUDE_PROJET = new Matiere("ETUDE_PROJET", "Etude ou Projet", 6, 40, ECRIT);
    public static final Matiere ANALYSE_FAB_OUTIL = new Matiere("ANALYSE_FAB_OUTIL", "Analyse de Fabrication / Etude d'outillage", 6, 180, ECRIT);

    public static final List<Matiere> EPREUVES_ECRITES = List.of(MATHS, ELECTRICITE_METAL, MECANIQUE, ETUDE_PROJET, ANALYSE_FAB_OUTIL);

    public static final int BAREME_ECRIT = 420;

    // ---- Epreuves orales ----
    public static final Matiere AUTOMATISME = new Matiere("AUTOMATISME", "Automatisme", 2, 40, ORAL);
    public static final Matiere TECHNOLOGIE = new Matiere("TECHNOLOGIE", "Technologie", 2, 40, ORAL);
    public static final Matiere LV = new Matiere("LV", "Langue Vivante", 2, 40, ORAL);
    public static final Matiere EPREUVE_ATELIER = new Matiere("EPREUVE_ATELIER", "Epreuve d'atelier", 6, 120, ORAL);

    public static final List<Matiere> EPREUVES_ORALES = List.of(AUTOMATISME, TECHNOLOGIE, LV, EPREUVE_ATELIER);

    public static final int BAREME_ORAL = 240;

    public static final int BAREME_GENERAL = BAREME_ECRIT + BAREME_ORAL;

    public static Matiere findByCode(String code) {
        return Stream.concat(EPREUVES_ECRITES.stream(), EPREUVES_ORALES.stream())
                .filter(m -> m.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Matière inconnue pour la série F1-2emePartie : " + code));
    }
}
