package com.officedubac.project.modules.D.model;

/**
 * Description figée d'une épreuve (constante, jamais persistée individuellement ;
 * seul son "code" est stocké dans les notes saisies, voir NoteEpreuve).
 * Le barème est celui imprimé sur le gabarit officiel (pas toujours égal à
 * coefficient x 20 : certaines épreuves pratiques ont un barème propre).
 */
public class Matiere {

    private final String code;
    private final String libelle;
    private final int coefficient;
    private final int bareme;
    private final TypeEpreuve type;
    private final GroupeEpreuves groupe;

    public Matiere(String code, String libelle, int coefficient, int bareme, TypeEpreuve type, GroupeEpreuves groupe) {
        this.code = code;
        this.libelle = libelle;
        this.coefficient = coefficient;
        this.bareme = bareme;
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
