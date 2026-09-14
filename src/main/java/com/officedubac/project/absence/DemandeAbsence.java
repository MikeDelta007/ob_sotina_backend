package com.officedubac.project.absence;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "absence_demandes")
public class DemandeAbsence {

    @Id
    private String id;

    private String demandeurId;
    private String demandeurNom;
    // Snapshot de l'id de la division du demandeur au moment de la création (pour router vers le bon chef)
    private String divisionId;

    // Nombre de jours ouvrés/calendaires demandés (dateFin - dateDebut + 1), calculé à la création
    private int nombreJours;

    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String motif;

    private StatutAbsence statut;

    private boolean validationChef;
    private String validateurChef;
    private LocalDateTime dateValidationChef;

    private boolean validationCsa;
    private String validateurCsa;
    private LocalDateTime dateValidationCsa;

    private boolean validationDirecteur;
    private String validateurDirecteur;
    private LocalDateTime dateValidationDirecteur;

    private String motifRejet;
    private String rejetePar;
    private LocalDateTime dateRejet;

    private String creePar;
    private LocalDateTime dateCreation;
}
