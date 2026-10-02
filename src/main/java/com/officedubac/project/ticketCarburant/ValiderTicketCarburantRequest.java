package com.officedubac.project.ticketCarburant;

import lombok.Data;

@Data
public class ValiderTicketCarburantRequest {
    // Requis uniquement pour la validation du Directeur : le nombre de tickets qu'il accorde
    private Integer nombreTicketsAccorde;
}
