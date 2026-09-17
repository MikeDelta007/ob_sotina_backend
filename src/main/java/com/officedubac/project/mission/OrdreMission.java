package com.officedubac.project.mission;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "mission_ordres")
public class OrdreMission {

    @Id
    private String id;

    // Destination : une ou plusieurs régions
    private List<String> regionIds;
    private List<String> regionNoms;

    private String motif;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    // Lignes façon facture : une par agent, chacune avec sa propre voiture (et éventuellement son chauffeur dédié)
    private List<LigneMission> lignes;

    private String creeParId;
    private String creeParNom;
    private LocalDateTime dateCreation;

    private boolean annule;
    private String annuleParId;
    private LocalDateTime dateAnnulation;
}
