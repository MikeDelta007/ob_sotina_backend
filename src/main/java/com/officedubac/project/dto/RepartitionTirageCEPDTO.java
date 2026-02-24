package com.officedubac.project.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    // 🔥 dynamique piloté par les règles
    private Map<String, Integer> matieres;

}

