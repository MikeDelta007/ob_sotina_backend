package com.officedubac.project.expressionBesoin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ExpressionBesoinRequest {

    @NotBlank
    private String motifId;
    private String motifLibelle;

    @NotNull @Positive
    private BigDecimal montantInitial;
}
