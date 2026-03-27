package com.officedubac.project.dto;


import com.officedubac.project.models.GroupeMatiere;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RepartitionTirageCEPDTO
{
    private Integer jury;
    private Integer session;
    private String centreEcrit;
    private String academia;
    private Long effectif;
    private List<String> series;
    private Boolean cp;
    private Boolean cs;
    private Map<String, GroupeMatiere> matieres;

}

