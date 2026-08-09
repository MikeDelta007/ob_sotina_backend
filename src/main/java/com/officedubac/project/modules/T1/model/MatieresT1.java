package com.officedubac.project.modules.T1.model;

import java.util.List;

import static com.officedubac.project.modules.T1.model.GroupeEpreuves.DEUXIEME;
import static com.officedubac.project.modules.T1.model.GroupeEpreuves.PREMIER;
import static com.officedubac.project.modules.T1.model.TypeEpreuve.ECRIT;
import static com.officedubac.project.modules.T1.model.TypeEpreuve.ORAL;

/**
 * Référentiel figé des matières, coefficients et barèmes de la série T1
 * (Sciences et Techniques Industrielles (Mécanique)), tel qu'imprimé sur le gabarit officiel de
 * l'Office du Baccalauréat.
 */
public final class MatieresT1 {

    private MatieresT1() { }

    // ---- 1er groupe d'épreuves ----
    public static final Matiere TECH_EXPR = new Matiere("TECH_EXPR", "Technique d'Expression et de Communication", 3, 60, ECRIT, PREMIER);
    public static final Matiere MATH = new Matiere("MATH", "Mathématiques", 5, 100, ECRIT, PREMIER);
    public static final Matiere MECANIQUE = new Matiere("MECANIQUE", "Mécanique", 4, 80, ECRIT, PREMIER);
    public static final Matiere CONST_MECA = new Matiere("CONST_MECA", "Construction Mécanique", 4, 80, ECRIT, PREMIER);
    public static final Matiere ANAL_FAB_OUTIL = new Matiere("ANAL_FAB_OUTIL", "Analyse de Fabrication / Outillage", 6, 120, ECRIT, PREMIER);
    public static final Matiere ELECTRICITE = new Matiere("ELECTRICITE", "Electricité", 1, 20, ECRIT, PREMIER);
    public static final Matiere METALLURGIE = new Matiere("METALLURGIE", "Métallurgie", 1, 20, ECRIT, PREMIER);
    public static final Matiere SC_PHYS = new Matiere("SC_PHYS", "Sciences Physiques", 2, 40, ECRIT, PREMIER);
    public static final Matiere ANGLAIS = new Matiere("ANGLAIS", "Anglais", 2, 40, ORAL, PREMIER);
    public static final Matiere TECHNO_AUTOM = new Matiere("TECHNO_AUTOM", "Technologie et Automatisme", 2, 40, ECRIT, PREMIER);
    public static final Matiere EPR_PRATIQUE = new Matiere("EPR_PRATIQUE", "Epreuve Pratique d'Atelier", 4, 80, ECRIT, PREMIER);

    public static final List<Matiere> PREMIER_GROUPE = List.of(TECH_EXPR, MATH, MECANIQUE, CONST_MECA, ANAL_FAB_OUTIL, ELECTRICITE, METALLURGIE, SC_PHYS, ANGLAIS, TECHNO_AUTOM, EPR_PRATIQUE);

    public static final int BAREME_PREMIER_GROUPE = 680;

    // ---- 2eme groupe d'épreuves (report du 1er groupe + épreuve de contrôle) ----
    // (aucune matière propre : le 2eme groupe ne comprend que le report et l'épreuve de contrôle)

    public static final List<Matiere> DEUXIEME_GROUPE = List.of();

    public static final int BAREME_DEUXIEME_GROUPE = 0;

    public static final int BAREME_TOTAL_DEFINITIF = BAREME_PREMIER_GROUPE + BAREME_DEUXIEME_GROUPE;

    public static Matiere findByCode(String code) {
        return java.util.stream.Stream.concat(PREMIER_GROUPE.stream(), DEUXIEME_GROUPE.stream())
                .filter(m -> m.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Matière inconnue pour la série T1 : " + code));
    }
}
