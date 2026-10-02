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

    // Optionnel : id du User pour qui la demande est créée (un agent de sa division pour un
    // chef de service, n'importe quel agent pour le CSA/Directeur/Assistante Directeur). Absent
    // ou égal à son propre id : la demande est pour le créateur lui-même.
    private String beneficiaireId;
}
