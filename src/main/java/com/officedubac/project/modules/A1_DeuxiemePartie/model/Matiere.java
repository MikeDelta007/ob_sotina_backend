package com.officedubac.project.modules.A1_DeuxiemePartie.model;

import static com.officedubac.project.modules.A1_DeuxiemePartie.model.Enums.*;

public class Matiere {

    private final String code;
    private final String libelle;
    private final int coefficient;
    private final int bareme; // note max (20) * coefficient
    private final Enums.TypeEpreuve type;

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
