package com.officedubac.project.ticketCarburant;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

// Demande de tickets carburant (fiche papier "DEMANDE DE CARBURANT") : un trajet (ville de
// départ/arrivée), un nombre de tickets demandé, validée par le CSA puis le Directeur — c'est
// lui qui tranche le nombre de tickets réellement accordé, comme sur la fiche papier.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tc_demandes")
public class TicketCarburant {

    @Id
    private String id;

    private String numeroFiche;

    private String divisionLibelle;
    private String motifId;
    private String motifLibelle;
    private LocalDate date;

    private String villeDepartId;
    private String villeDepartNom;
    private String villeArriveeId;
    private String villeArriveeNom;

    private int nombreTicketsDemande;
    // Renseigné uniquement par le Directeur, lors de sa validation
    private Integer nombreTicketsAccorde;

    private Statut statut;

    // ── Validation CSA ──
    private boolean validationCsa;
    private String validateurCsa;
    private String validateurCsaNom;
    private LocalDateTime dateValidationCsa;

    // ── Rejet CSA : n'interrompt pas la chaîne, le Directeur tranche toujours
    // définitivement (c'est lui qui accorde le nombre de tickets) ──
    private boolean rejetCsa;
    private String motifRejetCsa;
    private String rejeteParCsaNom;
    private LocalDateTime dateRejetCsa;

    // ── Validation Directeur (toujours requise) ──
    private boolean validationDirecteur;
    private String validateurDirecteur;
    private String validateurDirecteurNom;
    private LocalDateTime dateValidationDirecteur;

    // ── Rejet définitif (celui du Directeur) ──
    private String motifRejet;
    private String rejetePar;
    private String rejeteParNom;
    private LocalDateTime dateRejet;

    private String creePar;
    private String creeParNom;
    private LocalDateTime dateCreation;

    public enum Statut { EN_ATTENTE, VALIDEE, REJETEE }
}
