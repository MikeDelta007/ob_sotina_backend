package com.officedubac.project.modules.G.model;

import java.util.List;

import static com.officedubac.project.modules.G.model.GroupeEpreuves.DEUXIEME;
import static com.officedubac.project.modules.G.model.GroupeEpreuves.PREMIER;
import static com.officedubac.project.modules.G.model.TypeEpreuve.ECRIT;
import static com.officedubac.project.modules.G.model.TypeEpreuve.ORAL;

/**
 * Référentiel figé des matières, coefficients et barèmes de la série G
 * (Techniques Quantitatives de Gestion et Economie), tel qu'imprimé sur le gabarit officiel de
 * l'Office du Baccalauréat.
 */
public final class MatieresG {

    private MatieresG() { }

    // ---- 1er groupe d'épreuves ----
    public static final Matiere TECH_EXPR = new Matiere("TECH_EXPR", "Techniques d'Expression et de Communication", 3, 60, ECRIT, PREMIER);
    public static final Matiere ANGLAIS = new Matiere("ANGLAIS", "Anglais", 3, 60, ECRIT, PREMIER);
    public static final Matiere PHILO = new Matiere("PHILO", "Philosophie", 2, 40, ECRIT, PREMIER);
    public static final Matiere MATH = new Matiere("MATH", "Mathématiques", 5, 100, ECRIT, PREMIER);
    public static final Matiere ECONOMIE_GEN = new Matiere("ECONOMIE_GEN", "Economie Générale", 6, 120, ECRIT, PREMIER);
    public static final Matiere ETUDE_CAS = new Matiere("ETUDE_CAS", "Etude de cas", 6, 120, ECRIT, PREMIER);
    public static final Matiere CONN_MONDE = new Matiere("CONN_MONDE", "Connaissance du monde", 2, 40, ECRIT, PREMIER);
    public static final Matiere CORRESPONDANCE = new Matiere("CORRESPONDANCE", "Correspondance Commerciale", 2, 40, ECRIT, PREMIER);
    public static final Matiere TRAITEMENT_INFO = new Matiere("TRAITEMENT_INFO", "Traitement Informatique des Gestions", 2, 40, ECRIT, PREMIER);

    public static final List<Matiere> PREMIER_GROUPE = List.of(TECH_EXPR, ANGLAIS, PHILO, MATH, ECONOMIE_GEN, ETUDE_CAS, CONN_MONDE, CORRESPONDANCE, TRAITEMENT_INFO);

    public static final int BAREME_PREMIER_GROUPE = 620;

    // ---- 2eme groupe d'épreuves (report du 1er groupe + épreuve de contrôle) ----
    // (aucune matière propre : le 2eme groupe ne comprend que le report et l'épreuve de contrôle)

    public static final List<Matiere> DEUXIEME_GROUPE = List.of();

    public static final int BAREME_DEUXIEME_GROUPE = 0;

    public static final int BAREME_TOTAL_DEFINITIF = BAREME_PREMIER_GROUPE + BAREME_DEUXIEME_GROUPE;

    public static Matiere findByCode(String code) {
        return java.util.stream.Stream.concat(PREMIER_GROUPE.stream(), DEUXIEME_GROUPE.stream())
                .filter(m -> m.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Matière inconnue pour la série G : " + code));
    }
}
