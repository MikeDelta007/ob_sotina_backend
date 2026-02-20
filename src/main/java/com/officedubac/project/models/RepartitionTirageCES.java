package com.officedubac.project.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepartitionTirageCES
{
    @Id
    private String id;

    private Integer session;
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
    private Long svtSE;
    private Long svtSM;
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
    private Long gelec;
    private Long gemec;
    private Long mo;
    private Long ses;
    private Long gcf;
}
