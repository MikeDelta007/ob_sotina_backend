package com.officedubac.project.ticketRestaurant;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

// Motif prédéfini pour une demande de ticket restaurant — liste propre à ce module, indépendante
// des motifs de dépense (caisseAvance.Motif), d'absence (absence.MotifAbsence) et de ticket
// carburant (ticketCarburant.MotifCarburant).
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tr_motifs")
public class MotifTicketRestaurant {

    @Id
    private String id;

    private String libelle;

    // Rôles/droits (profil principal ou supplémentaire) concernés par ce motif : un compte ne le
    // voit à la création que s'il a l'un de ces rôles. Vide ou nul = visible de tous.
    private List<String> roles;

    private boolean actif = true;

    @CreatedDate
    private LocalDateTime dateCreation;
    @LastModifiedDate
    private LocalDateTime dateModification;
}
