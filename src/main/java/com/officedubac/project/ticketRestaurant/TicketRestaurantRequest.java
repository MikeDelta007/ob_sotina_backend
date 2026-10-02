package com.officedubac.project.ticketRestaurant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class TicketRestaurantRequest {
    @NotBlank
    private String motifId;

    // Pas de date à saisir : un ticket restaurant couvre toujours la journée en cours.
    @NotEmpty
    private List<String> agentIds;
}
