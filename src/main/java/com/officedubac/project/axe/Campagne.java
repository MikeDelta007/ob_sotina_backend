package com.officedubac.project.axe;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// Une campagne (ex. "Campagne de ramassage malles 2026") a sa propre période globale
// (dateDebut/dateFin) et regroupe des missions ajoutées au fil de l'eau — chacune affecte UN
// axe à une période qui lui est propre (dateDebut/dateFin de la mission, pas forcément égale à
// celle de la campagne), avec ses propres agents et son propre nombre de tickets. Un même axe
// ne peut être affecté à deux missions (de n'importe quelle campagne) dont les périodes se
// chevauchent (cf. CampagneService.verifierChevauchement).
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "pedagogie_campagnes")
public class Campagne {

    @Id
    private String id;

    private String nom;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private boolean actif;

    @Builder.Default
    private List<Mission> missions = List.of();

    private String creePar;

    @CreatedDate
    private LocalDateTime dateCreation;
    @LastModifiedDate
    private LocalDateTime dateModification;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Mission {
        private String id;

        private String axeId;
        // Snapshot au moment de l'affectation
        private Integer axeNumero;
        private String axeItineraire;

        private LocalDate dateDebut;
        private LocalDate dateFin;

        private List<String> agentIds;
        private List<String> agentNoms;
        private Integer nombreTickets;
    }
}
