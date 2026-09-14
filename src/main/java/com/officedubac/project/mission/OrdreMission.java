package com.officedubac.project.mission;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "mission_ordres")
public class OrdreMission {

    @Id
    private String id;

    private String agentId;
    private String agentNom;
    private String destination;
    private String motif;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    private String creeParId;
    private String creeParNom;
    private LocalDateTime dateCreation;

    private boolean annule;
    private String annuleParId;
    private LocalDateTime dateAnnulation;
}
