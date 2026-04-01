package com.officedubac.project.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RepartitionCompleteFDTO
{
    private String centre;
    private String academie;
    private String localite;
    private int session;
    private int nbJury;
    private Long effectif;
    private Boolean cp;
    private Boolean cs;
    private Long fd;
    private Long ic;
    private Long fb;
}
