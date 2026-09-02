package com.officedubac.project.caisseAvance;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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

    // N° CNI : un seul décaissement pour tout le cumulatif
    private String numeroCni;

    @Data
    public static class Ligne {
        @NotNull @Positive
        private BigDecimal montant;
        @NotNull
        private String motifId;
        private String motifLibelle;

        // Chaque facture a son propre bénéficiaire (comme autant de mandatements simples)
        private String beneficiaire;
        // Expression de besoin traitée dont cette facture est issue : obligatoire,
        // chaque facture d'un cumulatif est comme un mandatement simple.
        @NotBlank
        private String expressionBesoinId;
    }
}
