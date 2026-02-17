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
public class RepartitionTirageCSDTO
{
    private Integer jury;
    private String centreEcrit;
    private String academia;

    private Long effectif;
    private Long frenchL;
    private Long frenchS;
    private Long frenchLA;
    private Long frenchSA;

    private Long englishS;
    private Long mathL;
    private Long mathSM;
    private Long pcSM;
    private Long mathSE;
    private Long pcSE;
    private Long svt;
    private Long philoL;
    private Long philoS;
    private Long hg;
    private Long lla;
    private Long allemendLV1;
    private Long allemendLV2;
    private Long anglaisLV1;
    private Long anglaisLV2;
    private Long arabeModerneLV1;
    private Long arabeModerneLV2;
    private Long economie;
    private Long espagnolLV1;
    private Long espagnolLV2;
    private Long italien;
    private Long latin;
    private Long portugaisLV1;
    private Long portugaisLV2;
    private Long russe;
    private Long pcL;
    private Long svtL;
}
