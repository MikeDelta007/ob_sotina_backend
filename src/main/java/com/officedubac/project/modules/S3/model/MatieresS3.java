package com.officedubac.project.modules.S3.model;

import java.util.List;

import static com.officedubac.project.modules.S3.model.GroupeEpreuves.DEUXIEME;
import static com.officedubac.project.modules.S3.model.GroupeEpreuves.PREMIER;
import static com.officedubac.project.modules.S3.model.TypeEpreuve.ECRIT;
import static com.officedubac.project.modules.S3.model.TypeEpreuve.ORAL;

/**
 * Référentiel figé des matières, coefficients et barèmes de la série S3
 * (Sciences et Techniques), tel qu'imprimé sur le gabarit officiel de
 * l'Office du Baccalauréat.
 */
public final class MatieresS3 {

    private MatieresS3() { }

    // ---- 1er groupe d'épreuves ----
    public static final Matiere FRANCAIS = new Matiere("FRANCAIS", "Français", 3, 60, ECRIT, PREMIER);
    public static final Matiere PHILO = new Matiere("PHILO", "Philosophie", 2, 40, ECRIT, PREMIER);
    public static final Matiere MATH = new Matiere("MATH", "Mathématiques", 8, 160, ECRIT, PREMIER);
    public static final Matiere SC_PHYS = new Matiere("SC_PHYS", "Sciences Physiques", 8, 160, ECRIT, PREMIER);
    public static final Matiere CONST_MECA = new Matiere("CONST_MECA", "Construction Mécanique", 8, 160, ECRIT, PREMIER);
    public static final Matiere ANAL_FAB_AUTO = new Matiere("ANAL_FAB_AUTO", "Analyse de Fabrication / Technologie Automatisée", 2, 40, ECRIT, PREMIER);
    public static final Matiere ANGLAIS = new Matiere("ANGLAIS", "Anglais", 2, 40, ORAL, PREMIER);
    public static final Matiere EPR_PRATIQUE = new Matiere("EPR_PRATIQUE", "Epreuve Pratique d'Atelier", 3, 60, ECRIT, PREMIER);

    public static final List<Matiere> PREMIER_GROUPE = List.of(FRANCAIS, PHILO, MATH, SC_PHYS, CONST_MECA, ANAL_FAB_AUTO, ANGLAIS, EPR_PRATIQUE);

    public static final int BAREME_PREMIER_GROUPE = 720;

    // ---- 2eme groupe d'épreuves (report du 1er groupe + épreuve de contrôle) ----
    // (aucune matière propre : le 2eme groupe ne comprend que le report et l'épreuve de contrôle)

    public static final List<Matiere> DEUXIEME_GROUPE = List.of();

    public static final int BAREME_DEUXIEME_GROUPE = 0;

    public static final int BAREME_TOTAL_DEFINITIF = BAREME_PREMIER_GROUPE + BAREME_DEUXIEME_GROUPE;

    public static Matiere findByCode(String code) {
        return java.util.stream.Stream.concat(PREMIER_GROUPE.stream(), DEUXIEME_GROUPE.stream())
                .filter(m -> m.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Matière inconnue pour la série S3 : " + code));
    }
}
