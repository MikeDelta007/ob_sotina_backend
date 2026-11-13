package com.officedubac.project.axe;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

// Un axe = un itinéraire géographique desservant une liste d'établissements, chacun
// éventuellement centre secondaire (point de regroupement pour les établissements voisins,
// reliés à lui par un "relais"). Jours/tickets ne sont plus portés par l'axe lui-même : ce sont
// des attributs de chaque mission qui l'emprunte (cf. Mission.SousMission).
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "pedagogie_axes")
public class Axe {

    @Id
    private String id;

    private int numero;
    private String itineraire;
    private boolean actif;

    @Builder.Default
    private List<AxeEtablissement> etablissements = List.of();

    private String creePar;

    @CreatedDate
    private LocalDateTime dateCreation;
    @LastModifiedDate
    private LocalDateTime dateModification;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AxeEtablissement {
        private String etablissementId;
        // Snapshot du nom au moment de l'ajout — évite une jointure pour le simple affichage,
        // et garde un repère lisible même si l'établissement est renommé/supprimé ensuite.
        private String etablissementNom;
        private boolean centreSecondaire;
        private String relais;
    }
}
