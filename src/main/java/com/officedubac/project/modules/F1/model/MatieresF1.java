package com.officedubac.project.modules.F1.model;

import java.util.List;

import static com.officedubac.project.modules.F1.model.GroupeEpreuves.DEUXIEME;
import static com.officedubac.project.modules.F1.model.GroupeEpreuves.PREMIER;
import static com.officedubac.project.modules.F1.model.TypeEpreuve.ECRIT;
import static com.officedubac.project.modules.F1.model.TypeEpreuve.ORAL;

/**
 * Référentiel figé des matières, coefficients et barèmes de la série F1
 * (Sciences et Techniques Industrielles (Mécanique)), tel qu'imprimé sur le gabarit officiel de
 * l'Office du Baccalauréat.
 */
public final class MatieresF1 {

    private MatieresF1() { }

    // ---- 1er groupe d'épreuves ----
    public static final Matiere FR_ECRIT = new Matiere("FR_ECRIT", "Français (écrit)", 2, 40, ECRIT, PREMIER);
    public static final Matiere FR_ORAL = new Matiere("FR_ORAL", "Français (oral)", 1, 20, ORAL, PREMIER);
    public static final Matiere MATH = new Matiere("MATH", "Mathématiques", 4, 80, ECRIT, PREMIER);
    public static final Matiere MECANIQUE = new Matiere("MECANIQUE", "Mécanique", 4, 80, ECRIT, PREMIER);
    public static final Matiere CONST_MECA = new Matiere("CONST_MECA", "Construction Mécanique", 4, 80, ECRIT, PREMIER);
    public static final Matiere ANALYSE_FAB = new Matiere("ANALYSE_FAB", "Analyse de Fabrication", 4, 80, ECRIT, PREMIER);
    public static final Matiere ELEC_METAL = new Matiere("ELEC_METAL", "Electricité - Métallurgie", 2, 40, ECRIT, PREMIER);
    public static final Matiere TECHNO_AUTOM = new Matiere("TECHNO_AUTOM", "Technologie Automatisée", 2, 40, ECRIT, PREMIER);
    public static final Matiere ANGLAIS = new Matiere("ANGLAIS", "Anglais", 2, 40, ORAL, PREMIER);
    public static final Matiere EPR_PRATIQUE = new Matiere("EPR_PRATIQUE", "Epreuve Pratique", 4, 80, ECRIT, PREMIER);

    public static final List<Matiere> PREMIER_GROUPE = List.of(FR_ECRIT, FR_ORAL, MATH, MECANIQUE, CONST_MECA, ANALYSE_FAB, ELEC_METAL, TECHNO_AUTOM, ANGLAIS, EPR_PRATIQUE);

    public static final int BAREME_PREMIER_GROUPE = 580;

    // ---- 2eme groupe d'épreuves (report du 1er groupe + épreuve de contrôle) ----
    // (aucune matière propre : le 2eme groupe ne comprend que le report et l'épreuve de contrôle)

    public static final List<Matiere> DEUXIEME_GROUPE = List.of();

    public static final int BAREME_DEUXIEME_GROUPE = 0;

    public static final int BAREME_TOTAL_DEFINITIF = BAREME_PREMIER_GROUPE + BAREME_DEUXIEME_GROUPE;

    public static Matiere findByCode(String code) {
        return java.util.stream.Stream.concat(PREMIER_GROUPE.stream(), DEUXIEME_GROUPE.stream())
                .filter(m -> m.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Matière inconnue pour la série F1 : " + code));
    }
}
