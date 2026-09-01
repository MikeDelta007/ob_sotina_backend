package com.officedubac.project.expressionBesoin;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    // @JsonProperty explicite : Jackson dérive sinon la clé JSON "AFacturePreformat"
    // (les deux premières lettres après "get" sont majuscules), incompatible avec
    // le frontend qui envoie "aFacturePreformat".
    @JsonProperty("aFacturePreformat")
    @NotNull
    private Boolean aFacturePreformat;
}
