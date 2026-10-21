package com.officedubac.project.mission;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LigneMission {

    private String agentId;
    private String agentNom;
    private String agentQualite;

    private MoyenTransport moyenTransport;

    // Renseignés seulement si moyenTransport != TRANSPORT_COMMUN
    private String voitureId;
    private String voitureImmatriculation;
}
