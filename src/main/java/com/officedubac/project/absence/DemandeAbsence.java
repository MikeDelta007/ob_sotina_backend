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

    // CONGE (décompte le solde de congés) ou AUTORISATION (ponctuelle, ne décompte rien)
    private TypeAbsence type;

    // Nombre de jours ouvrés/calendaires demandés (dateFin - dateDebut + 1), calculé à la création
    private int nombreJours;

    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String motif;

    private StatutAbsence statut;

    // Étape chef : la chaîne avance vers le CSA que le chef valide ou rejette — un rejet
    // intermédiaire n'est qu'un avis, il n'arrête plus le circuit.
    private boolean validationChef;
    private boolean rejetChef;
    private String validateurChef;
    // Nom complet snapshotté au moment de l'action (affiché à la place du login)
    private String validateurChefNom;
    private String motifRejetChef;
    private LocalDateTime dateTraitementChef;

    // Étape CSA : même principe, la chaîne avance toujours vers le Directeur
    private boolean validationCsa;
    private boolean rejetCsa;
    private String validateurCsa;
    private String validateurCsaNom;
    private String motifRejetCsa;
    private LocalDateTime dateTraitementCsa;

    // Étape Directeur : seule décision finale — valide (VALIDEE) ou rejette (REJETEE) pour de bon
    private boolean validationDirecteur;
    private String validateurDirecteur;
    private String validateurDirecteurNom;
    private LocalDateTime dateValidationDirecteur;

    private String motifRejet;
    private String rejetePar;
    private String rejeteParNom;
    private LocalDateTime dateRejet;

    private String creePar;
    private LocalDateTime dateCreation;
}
