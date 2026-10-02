package com.officedubac.project.ticketCarburant;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RejeterTicketCarburantRequest {
    @NotBlank
    private String motif;
}
