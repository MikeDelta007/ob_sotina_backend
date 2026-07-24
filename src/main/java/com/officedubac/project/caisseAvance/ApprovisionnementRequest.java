package com.officedubac.project.caisseAvance;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ApprovisionnementRequest {
    @NotNull @Positive
    private BigDecimal montant;
    @NotNull
    private LocalDate date;
    private String description;
}
