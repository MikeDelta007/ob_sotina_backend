package com.officedubac.project.mission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class OrdreMissionRequest {
    @NotBlank
    private String agentId;
    @NotBlank
    private String destination;
    @NotBlank
    private String motif;
    @NotNull
    private LocalDate dateDebut;
    @NotNull
    private LocalDate dateFin;
}
