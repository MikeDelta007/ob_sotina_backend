package com.officedubac.project.personnel;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "personnel_voitures")
public class Voiture {

    @Id
    private String id;

    private String immatriculation;
    private String marque;
    @Builder.Default
    private int capacite = 4;
    @Builder.Default
    private boolean actif = true;

    // Propriétaire du véhicule : l'Office, un agent précis, ou une personne externe précise
    @Builder.Default
    private ProprietaireVoiture proprietaireType = ProprietaireVoiture.OFFICE;

    // Rempli uniquement si proprietaireType == AGENT (référence un User)
    private String proprietaireAgentId;
    private String proprietaireAgentNom;

    // Rempli uniquement si proprietaireType == EXTERNE (référence un Personnel externe)
    private String proprietairePersonnelId;
    private String proprietairePersonnelNom;
}
