package com.officedubac.project.absence;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DemandeAbsenceRequest {
    @NotNull
    private TypeAbsence type;
    @NotNull
    private LocalDate dateDebut;
    @NotNull
    private LocalDate dateFin;
    // Obligatoire uniquement pour une AUTORISATION (vérifié en service) — une demande de CONGE
    // n'a pas besoin de motif.
    private String motif;
}
