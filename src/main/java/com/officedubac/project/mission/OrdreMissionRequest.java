package com.officedubac.project.mission;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class OrdreMissionRequest {
    @NotEmpty(message = "Sélectionnez au moins une ville de destination")
    private List<String> villeIds;

    @NotBlank(message = "Sélectionnez un motif")
    private String motifId;
    @NotNull
    private LocalDate dateDebut;
    @NotNull
    private LocalDate dateFin;

    @NotEmpty(message = "La mission doit avoir au moins une ligne (agent + véhicule)")
    @Valid
    private List<LigneMissionRequest> lignes;
}
