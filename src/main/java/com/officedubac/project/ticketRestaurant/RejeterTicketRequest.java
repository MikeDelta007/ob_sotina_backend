package com.officedubac.project.ticketRestaurant;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RejeterTicketRequest {
    @NotBlank
    private String motif;
}
