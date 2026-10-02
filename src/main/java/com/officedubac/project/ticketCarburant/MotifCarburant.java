package com.officedubac.project.ticketCarburant;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

// Motif prédéfini pour une demande de ticket carburant (ex: Mission, Remise de diplômes...) —
// liste propre à ce module, indépendante des motifs de dépense (caisseAvance.Motif) et des
// motifs d'absence (absence.MotifAbsence).
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tc_motifs")
public class MotifCarburant {

    @Id
    private String id;

    private String libelle;

    private boolean actif = true;

    @CreatedDate
    private LocalDateTime dateCreation;
    @LastModifiedDate
    private LocalDateTime dateModification;
}
