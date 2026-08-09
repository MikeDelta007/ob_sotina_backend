package com.officedubac.project.modules.A2.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description figée d'une épreuve du barème Option A1 (UCAD - Office du Baccalauréat).
 * Ces objets sont des constantes : ils ne sont jamais persistés individuellement,
 * seul leur "code" est stocké dans les notes saisies (voir NoteEpreuve).
 */
@Data
@AllArgsConstructor
public class Matiere {

    private final String code;
    private final String libelle;
    private final int coefficient;
    private final int bareme; // points max = note max (20) * coefficient
    private final TypeEpreuve type;
    private final GroupeEpreuves groupe;

    public Matiere(String code, String libelle, int coefficient, TypeEpreuve type, GroupeEpreuves groupe) {
        this.code = code;
        this.libelle = libelle;
        this.coefficient = coefficient;
        this.bareme = coefficient * 20;
        this.type = type;
        this.groupe = groupe;
    }
}
