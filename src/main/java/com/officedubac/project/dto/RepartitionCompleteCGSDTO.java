package com.officedubac.project.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class RepartitionCompleteCGSDTO
{
    private String centre;
    private String academie;
    private Long session;
    private int jury;
    private Long effectif;
    private List<String> series;
    private List<MatiereComposeeCGSDTO> matieres;
}
