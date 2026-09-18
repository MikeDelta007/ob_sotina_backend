package com.officedubac.project.expressionBesoin;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ExpressionBesoinRequest {

    @NotEmpty @Valid
    private List<LigneRequest> lignes;

    // @JsonProperty explicite : Jackson dérive sinon la clé JSON "AFacturePreformat"
    // (les deux premières lettres après "get" sont majuscules), incompatible avec
    // le frontend qui envoie "aFacturePreformat".
    @JsonProperty("aFacturePreformat")
    private Boolean aFacturePreformat;

    @Data
    public static class LigneRequest {
        @NotBlank
        private String motifId;
        private String motifLibelle;

        // Optionnelle : certaines désignations ne sont pas quantitatives
        @Positive
        private Integer quantite;

        @NotNull @Positive
        private BigDecimal prixUnitaire;
    }
}
