package com.officedubac.project.expressionBesoin;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RejeterRequest {
    @NotBlank
    private String motif;
}
