package com.officedubac.project.mission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LigneMissionRequest {
    @NotBlank
    private String agentId;

    @NotNull
    private Boolean disponibiliteVoiture;

    // Requis uniquement si disponibiliteVoiture == false (choix dans le parc de l'Office)
    private String voitureId;
}
