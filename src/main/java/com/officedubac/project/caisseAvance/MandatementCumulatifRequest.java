package com.officedubac.project.caisseAvance;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class MandatementCumulatifRequest {

    @NotEmpty @Valid
    private List<Ligne> lignes;

    @NotNull
    private Mandatement.TypePaiement typePaiement = Mandatement.TypePaiement.TOTALITE;

    // Requis si typePaiement = AVANCE
    private BigDecimal montantAvanceGlobal;

    private String description;

    @Data
    public static class Ligne {
        @NotNull @Positive
        private BigDecimal montant;
        @NotNull
        private String motifId;
        private String motifLibelle;
    }
}
