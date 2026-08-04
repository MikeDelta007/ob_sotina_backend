package com.officedubac.project.modules.A1.model;

/**
 * Description figée d'une épreuve du barème Option A1 (UCAD - Office du Baccalauréat).
 * Ces objets sont des constantes : ils ne sont jamais persistés individuellement,
 * seul leur "code" est stocké dans les notes saisies (voir NoteEpreuve).
 */
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

    public String getCode() { return code; }
    public String getLibelle() { return libelle; }
    public int getCoefficient() { return coefficient; }
    public int getBareme() { return bareme; }
    public TypeEpreuve getType() { return type; }
    public GroupeEpreuves getGroupe() { return groupe; }
}
