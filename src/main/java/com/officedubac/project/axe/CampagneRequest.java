package com.officedubac.project.axe;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

// Création/modification de la campagne elle-même (son en-tête) — les missions s'ajoutent
// séparément à une campagne déjà créée, voir MissionRequest.
@Data
public class CampagneRequest {
    @NotBlank
    private String nom;
    @NotNull
    private LocalDate dateDebut;
    @NotNull
    private LocalDate dateFin;
}
