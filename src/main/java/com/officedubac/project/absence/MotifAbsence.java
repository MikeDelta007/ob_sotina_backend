package com.officedubac.project.absence;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

// Motif prédéfini pour une demande d'autorisation d'absence (ex: Baptême, Mariage...) —
// n'a rien à voir avec caisseAvance.Motif (motifs de décaissement).
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "absence_motifs")
public class MotifAbsence {

    @Id
    private String id;

    private String libelle;

    private boolean actif = true;

    @CreatedDate
    private LocalDateTime dateCreation;
    @LastModifiedDate
    private LocalDateTime dateModification;
}
