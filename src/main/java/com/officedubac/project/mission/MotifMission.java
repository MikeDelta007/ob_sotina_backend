package com.officedubac.project.mission;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

// Motif prédéfini pour un ordre de mission (ex: Délivrance des diplômes...) — liste propre à ce
// module, indépendante des motifs carburant/restaurant/absence.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "mission_motifs")
public class MotifMission {

    @Id
    private String id;

    private String libelle;

    private boolean actif = true;

    @CreatedDate
    private LocalDateTime dateCreation;
    @LastModifiedDate
    private LocalDateTime dateModification;
}
