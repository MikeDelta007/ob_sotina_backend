package com.officedubac.project.modules.f1deuxiemepartie.model;

import com.officedubac.project.modules.f1deuxiemepartie.model.Enums.TypeEpreuve;

public class Matiere {

    private final String code;
    private final String libelle;
    private final int coefficient;
    private final int bareme;
    private final TypeEpreuve type;

    public Matiere(String code, String libelle, int coefficient, int bareme, TypeEpreuve type) {
        this.code = code;
        this.libelle = libelle;
        this.coefficient = coefficient;
        this.bareme = bareme;
        this.type = type;
    }

    public String getCode() { return code; }
    public String getLibelle() { return libelle; }
    public int getCoefficient() { return coefficient; }
    public int getBareme() { return bareme; }
    public TypeEpreuve getType() { return type; }
}
