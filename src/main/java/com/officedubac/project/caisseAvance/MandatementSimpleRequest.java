package com.officedubac.project.caisseAvance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class MandatementSimpleRequest {

    @NotNull @Positive
    private BigDecimal montant;

    @NotBlank
    private String motifId;

    private String motifLibelle;

    @NotNull
    private Mandatement.TypePaiement typePaiement = Mandatement.TypePaiement.TOTALITE;

    // Requis si typePaiement = AVANCE
    private BigDecimal montantAvance;

    private String description;

    private String beneficiaire;
    private String numeroCni;
    private String numeroCheque;

    // Optionnel : expression de besoin traitée dont ce mandatement est issu
    private String expressionBesoinId;
}
