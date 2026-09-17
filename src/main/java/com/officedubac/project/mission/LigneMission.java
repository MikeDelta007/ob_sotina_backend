package com.officedubac.project.mission;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LigneMission {

    private String agentId;
    private String agentNom;

    // true : l'agent dispose déjà de sa propre voiture ; false : voiture choisie dans la liste (parc de l'Office)
    private boolean disponibiliteVoiture;

    private String voitureId;
    private String voitureImmatriculation;
}
