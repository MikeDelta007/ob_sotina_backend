package com.officedubac.project.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class MatiereComposeeCGSDTO
{
    private String code;
    private String nom;
    private List<String> series;

    private Double premiere;
    private Double terminale;

}
