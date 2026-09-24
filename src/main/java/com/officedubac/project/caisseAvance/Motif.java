package com.officedubac.project.caisseAvance;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ca_motifs")
public class Motif {

    @Id
    private String id;

    private String libelle;   // ex: "Payage autoroute", "Repas", ...
    private boolean actif = true;
    // Si vrai, toute expression de besoin utilisant ce motif exige la confirmation de
    // satisfaction du demandeur avant tout décaissement du mandatement correspondant.
    private boolean requiertSatisfaction = false;
    // Motif réservé à un module (ex. « Ticket restaurant ») : jamais proposé à la création d'une
    // expression de besoin ni dans l'écran de gestion, mais présent au décaissement.
    private boolean systeme = false;

    @CreatedDate
    private LocalDateTime dateCreation;
    @LastModifiedDate
    private LocalDateTime dateModification;
}
