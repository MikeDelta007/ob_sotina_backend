package com.officedubac.project.ticketRestaurant;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TicketRestaurantRequest {
    @NotNull
    private LocalDate dateDebut;
    @NotNull
    private LocalDate dateFin;

    private boolean lundi;
    private boolean mardi;
    private boolean mercredi;
    private boolean jeudi;
    private boolean vendredi;

    @NotEmpty
    private List<String> agentIds;
}
