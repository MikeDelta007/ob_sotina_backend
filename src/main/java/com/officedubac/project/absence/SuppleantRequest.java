package com.officedubac.project.absence;

import lombok.Data;

@Data
public class SuppleantRequest {
    // Vide/null pour retirer le suppléant déjà désigné
    private String suppleantId;
}
