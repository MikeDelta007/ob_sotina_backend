package com.officedubac.project.expressionBesoin;

import lombok.Data;

@Data
public class ValiderRequest {
    // Obligatoire uniquement si l'expression de besoin porte une quantité demandée.
    private Integer quantiteAccordee;
}
