package com.officedubac.project.modules.E.model;

import com.officedubac.project.modules.E.model.Enums.TypeEpreuve;

public class Matiere {

    private final String code;
    private final String libelle;
    private final int coefficient;
    private final int bareme;
    private final TypeEpreuve type;

    public Matiere(String code, String libelle, int coefficient, TypeEpreuve type) {
        this.code = code;
        this.libelle = libelle;
        this.coefficient = coefficient;
        this.bareme = coefficient * 20;
        this.type = type;
    }

    public String getCode() { return code; }
    public String getLibelle() { return libelle; }
    public int getCoefficient() { return coefficient; }
    public int getBareme() { return bareme; }
    public TypeEpreuve getType() { return type; }
}
