package com.officedubac.project.dto;

import com.officedubac.project.models.GroupeMatiereCGS;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RepartitionTirageCGSDTO
{
    private Long session;
    private String centreEcrit;
    private String academia;
    private String discipline;
    private Long effectif_discipline;
    private Long effectif_centre;
    private List<String> series;

    private Double eff1ere;
    private Double effTle;

}
