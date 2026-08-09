package com.officedubac.project.modules.G1.model;

import java.util.List;

import static com.officedubac.project.modules.G1.model.GroupeEpreuves.DEUXIEME;
import static com.officedubac.project.modules.G1.model.GroupeEpreuves.PREMIER;
import static com.officedubac.project.modules.G1.model.TypeEpreuve.ECRIT;
import static com.officedubac.project.modules.G1.model.TypeEpreuve.ORAL;

/**
 * Référentiel figé des matières, coefficients et barèmes de la série G1
 * (Techniques Administratives), tel qu'imprimé sur le gabarit officiel de
 * l'Office du Baccalauréat.
 */
public final class MatieresG1 {

    private MatieresG1() { }

    // ---- 1er groupe d'épreuves ----
    public static final Matiere FR_ECRIT = new Matiere("FR_ECRIT", "Français (écrit)", 2, 40, ECRIT, PREMIER);
    public static final Matiere FR_ORAL = new Matiere("FR_ORAL", "Français (oral)", 1, 20, ORAL, PREMIER);
    public static final Matiere PHILO = new Matiere("PHILO", "Philosophie", 2, 40, ECRIT, PREMIER);
    public static final Matiere ECONOMIE = new Matiere("ECONOMIE", "Economie", 2, 40, ECRIT, PREMIER);
    public static final Matiere LV1 = new Matiere("LV1", "Langue Vivante I", 3, 60, ECRIT, PREMIER);
    public static final Matiere ETUDE_CAS = new Matiere("ETUDE_CAS", "Etude de cas", 6, 120, ECRIT, PREMIER);
    public static final Matiere CONN_MONDE = new Matiere("CONN_MONDE", "Connaissance du monde", 2, 40, ECRIT, PREMIER);
    public static final Matiere ORGAN_ADMIN = new Matiere("ORGAN_ADMIN", "Organisation Administrative", 2, 40, ECRIT, PREMIER);
    public static final Matiere LV2 = new Matiere("LV2", "Langue Vivante II", 2, 40, ORAL, PREMIER);

    public static final List<Matiere> PREMIER_GROUPE = List.of(FR_ECRIT, FR_ORAL, PHILO, ECONOMIE, LV1, ETUDE_CAS, CONN_MONDE, ORGAN_ADMIN, LV2);

    public static final int BAREME_PREMIER_GROUPE = 440;

    // ---- 2eme groupe d'épreuves (report du 1er groupe + épreuve de contrôle) ----
    // (aucune matière propre : le 2eme groupe ne comprend que le report et l'épreuve de contrôle)

    public static final List<Matiere> DEUXIEME_GROUPE = List.of();

    public static final int BAREME_DEUXIEME_GROUPE = 0;

    public static final int BAREME_TOTAL_DEFINITIF = BAREME_PREMIER_GROUPE + BAREME_DEUXIEME_GROUPE;

    public static Matiere findByCode(String code) {
        return java.util.stream.Stream.concat(PREMIER_GROUPE.stream(), DEUXIEME_GROUPE.stream())
                .filter(m -> m.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Matière inconnue pour la série G1 : " + code));
    }
}
