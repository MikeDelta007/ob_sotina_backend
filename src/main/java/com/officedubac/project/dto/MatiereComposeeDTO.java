package com.officedubac.project.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class MatiereComposeeDTO
{
    private String code;
    private String nom;
    private Set<String> series;

    private Double premierGroupe;
    private Double secondGroupe;

    private String champ;

}
