package com.officedubac.project.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class MatiereComposeeCGSDTO
{
    private String discipline;
    private Double premiere;
    private Double terminale;

}
