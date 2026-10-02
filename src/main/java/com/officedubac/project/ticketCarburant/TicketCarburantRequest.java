package com.officedubac.project.ticketCarburant;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TicketCarburantRequest {
    @NotBlank
    private String motifId;
    @NotNull
    private LocalDate date;
    @NotBlank
    private String villeDepartId;
    @NotBlank
    private String villeArriveeId;
    @NotNull @Min(1)
    private Integer nombreTicketsDemande;
}
