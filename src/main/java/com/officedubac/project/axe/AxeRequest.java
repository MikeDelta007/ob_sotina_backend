package com.officedubac.project.axe;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AxeRequest {
    @NotNull
    private Integer numero;
    @NotBlank
    private String itineraire;
    @Valid
    private List<LigneRequest> etablissements;

    @Data
    public static class LigneRequest {
        @NotBlank
        private String etablissementId;
        private boolean centreSecondaire;
        private String relais;
    }
}
