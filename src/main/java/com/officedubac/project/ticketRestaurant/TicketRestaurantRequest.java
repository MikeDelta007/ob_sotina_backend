package com.officedubac.project.ticketRestaurant;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TicketRestaurantRequest {
    // Dates cochées (jours ouvrés, aujourd'hui ou plus tard)
    @NotEmpty
    private List<LocalDate> dates;

    @NotEmpty
    private List<String> agentIds;
}
