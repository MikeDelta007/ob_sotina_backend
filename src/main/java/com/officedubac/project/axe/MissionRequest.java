package com.officedubac.project.axe;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

// Ajout/modification d'une mission au sein d'une campagne déjà créée (POST/PUT
// /campagnes/{id}/missions...).
@Data
public class MissionRequest {
    @NotBlank
    private String axeId;
    @NotNull
    private LocalDate dateDebut;
    @NotNull
    private LocalDate dateFin;
    private List<String> agentIds;
    @NotNull @Min(1)
    private Integer nombreTickets;
}
