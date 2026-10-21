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

    // Destination : une ou plusieurs villes
    private List<String> villeIds;
    private List<String> villeNoms;

    private String motifId;
    private String motifLibelle;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    // Lignes façon facture : une par agent, chacune avec son propre moyen de transport (véhicule
    // personnel, véhicule du parc, ou transport en commun)
    private List<LigneMission> lignes;

    private String creeParId;
    private String creeParNom;
    private LocalDateTime dateCreation;

    private boolean annule;
    private String annuleParId;
    private LocalDateTime dateAnnulation;
}
