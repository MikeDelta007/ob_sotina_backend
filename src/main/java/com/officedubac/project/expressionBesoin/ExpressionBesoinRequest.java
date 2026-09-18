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

    // Optionnelle : certaines désignations ne sont pas quantitatives
    @Positive
    private Integer quantite;

    @NotNull @Positive
    private BigDecimal prixUnitaire;

    // @JsonProperty explicite : Jackson dérive sinon la clé JSON "AFacturePreformat"
    // (les deux premières lettres après "get" sont majuscules), incompatible avec
    // le frontend qui envoie "aFacturePreformat".
    @JsonProperty("aFacturePreformat")
    private Boolean aFacturePreformat;

    // ── Bénéficiaire ──
    // Si true, le créateur est lui-même le bénéficiaire. Sinon, beneficiaireId (un agent
    // de sa division, cf. /personnel/mes-agents) est requis.
    private boolean beneficiaireMoiMeme;
    private String beneficiaireId;
}
