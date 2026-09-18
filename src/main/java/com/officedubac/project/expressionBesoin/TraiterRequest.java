package com.officedubac.project.expressionBesoin;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TraiterRequest {
    @NotNull @Positive
    private BigDecimal montantReel;
}
