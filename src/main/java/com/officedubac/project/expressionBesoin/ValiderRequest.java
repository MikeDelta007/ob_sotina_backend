package com.officedubac.project.expressionBesoin;

import lombok.Data;

import java.util.List;

@Data
public class ValiderRequest {
    // Quantités accordées, dans le même ordre que les lignes de l'expression de besoin.
    // Un élément peut être null pour une ligne sans quantité demandée ; il est en
    // revanche obligatoire pour toute ligne où une quantité a été demandée.
    private List<Integer> quantitesAccordees;
}
