package com.officedubac.project.ticketRestaurant;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// Demande de tickets restaurant : un chef de service sélectionne une période, les jours de la
// semaine concernés (Lundi à Vendredi) et une liste d'agents — le montant total est calculé
// automatiquement (nombre de jours ouvrés cochés dans la période × 1500 × nombre d'agents).
// Validée par le Directeur avant que la liste signée puisse être exportée en PDF.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tr_demandes")
public class TicketRestaurant {

    @Id
    private String id;

    // Dates cochées (jours ouvrés) : elles seules définissent la demande, pas de période. Un agent
    // ne peut figurer sur deux demandes non rejetées partageant la même date.
    private List<LocalDate> dates;
    // Première et dernière date cochée (dérivées, pour l'affichage)
    private LocalDate dateDebut;
    private LocalDate dateFin;

    private List<String> agentIds;
    private List<String> agentNoms;   // snapshot, pour affichage/PDF
    private List<String> agentServices; // snapshot du service/division de chaque agent (même ordre)

    // Nombre de dates cochées
    private int nombreJours;
    private static final BigDecimal MONTANT_PAR_JOUR = BigDecimal.valueOf(1500);
    private BigDecimal montantTotal;

    private Statut statut;

    private boolean validationDirecteur;
    private String validateurDirecteur;
    private String validateurDirecteurNom;
    private LocalDateTime dateValidationDirecteur;

    private String motifRejet;
    private String rejetePar;
    private String rejeteParNom;
    private LocalDateTime dateRejet;

    // Expression de besoin créée automatiquement à la validation par le Directeur, pour que
    // le montant fasse l'objet d'un décaissement via le circuit habituel (traitement
    // comptable puis mandatement) — sans repasser par une validation CSA/Directeur, déjà
    // faite ici.
    private String expressionBesoinId;

    private String creePar;
    private String creeParNom;
    private LocalDateTime dateCreation;

    public enum Statut { EN_ATTENTE, VALIDEE, REJETEE }

    public static BigDecimal montantParJour() {
        return MONTANT_PAR_JOUR;
    }
}
