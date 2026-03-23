package com.officedubac.project.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RepartitionCompleteDTO
{
    private String centre;
    private String academie;
    private int session;
    private int jury;

    private List<MatiereComposeeDTO> matieres;
}
