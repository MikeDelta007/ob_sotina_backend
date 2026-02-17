package com.officedubac.project.dto;

import com.officedubac.project.models.Etablissement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class EtablissementSummaryReception
{
    private Etablissement etablissement;
    private long decision0;
    private long decision1;
    private long decision2;
    private Set<String> operators = new HashSet<>();
}
