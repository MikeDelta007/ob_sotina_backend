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
    private Long frenchLnt;
    private String date1FL;
    private String heure1FL;
    private String date2FL;
    private String heure2FL;


    private Long frenchS;
    private Long frenchSnt;
    private String date1FS;
    private String heure1FS;
    private String date2FS;
    private String heure2FS;

    private Long frenchLA;
    private Long frenchLAnt;
    private String date1FLa;
    private String heure1FLa;
    private String date2FLa;
    private String heure2FLa;

    private Long frenchSA;
    private Long frenchSAnt;
    private String date1FSa;
    private String heure1FSa;
    private String date2FSa;
    private String heure2FSa;

    private Long englishS;
    private Long englishSnt;
    private String date1ES;
    private String heure1ES;
    private String date2ES;
    private String heure2ES;

    private Long mathL;
    private Long mathLnt;
    private String date1ML;
    private String heure1ML;
    private String date2ML;
    private String heure2ML;

    private Long mathSM;
    private Long mathSMnt;
    private String date1MSM;
    private String heure1MSM;
    private String date2MSM;
    private String heure2MSM;

    private Long pcSM;
    private Long pcSMnt;
    private String date1PCSM;
    private String heure1PCSM;
    private String date2PCSM;
    private String heure2PCSM;

    private Long mathSE;
    private Long mathSEnt;
    private String date1MSE;
    private String heure1MSE;
    private String date2MSE;
    private String heure2MSE;

    private Long pcSE;
    private Long pcSEnt;
    private String date1PCSE;
    private String heure1PCSE;
    private String date2PCSE;
    private String heure2PCSE;

    private Long svtSE;
    private Long svtSEnt;
    private String date1SVTSE;
    private String heure1SVTSE;
    private String date2SVTSE;
    private String heure2SVTSE;

    private Long svtSM;
    private Long svtSMnt;
    private String date1SVTSM;
    private String heure1SVTSM;
    private String date2SVTSM;
    private String heure2SVTSM;

    private Long philoL;
    private Long philoLnt;
    private String date1PHILOL;
    private String heure1PHILOL;
    private String date2PHILOL;
    private String heure2PHILOL;

    private Long philoS;
    private Long philoSnt;
    private String date1PHILOS;
    private String heure1PHILOS;
    private String date2PHILOS;
    private String heure2PHILOS;

    private Long hg;
    private Long hgnt;
    private String date1HG;
    private String heure1HG;
    private String date2HG;
    private String heure2HG;

    private Long lla;
    private Long llant;
    private String date1LLA;
    private String heure1LLA;
    private String date2LLA;
    private String heure2LLA;

    private Long allemendLV1;
    private Long allemendLV1nt;
    private String date1ALL1;
    private String heure1ALL1;
    private String date2ALL1;
    private String heure2ALL1;

    private Long allemendLV2;
    private Long allemendLV2nt;
    private String date1ALL2;
    private String heure1ALL2;
    private String date2ALL2;
    private String heure2ALL2;

    private Long anglaisLV1;
    private Long anglaisLV1nt;
    private String date1ANG1;
    private String heure1ANG1;
    private String date2ANG1;
    private String heure2ANG1;

    private Long anglaisLV2;
    private Long anglaisLV2nt;
    private String date1ANG2;
    private String heure1ANG2;
    private String date2ANG2;
    private String heure2ANG2;

    private Long arabeModerneLV1;
    private Long arabeModerneLV1nt;
    private String date1AM1;
    private String heure1AM1;
    private String date2AM1;
    private String heure2AM1;

    private Long arabeModerneLV2;
    private Long arabeModerneLV2nt;
    private String date1AM2;
    private String heure1AM2;
    private String date2AM2;
    private String heure2AM2;

    private Long economie;
    private Long economient;
    private String date1ECO;
    private String heure1ECO;
    private String date2ECO;
    private String heure2ECO;

    private Long espagnolLV1;
    private Long espagnolLV1nt;
    private String date1ESP1;
    private String heure1ESP1;
    private String date2ESP1;
    private String heure2ESP1;

    private Long espagnolLV2;
    private Long espagnolLV2nt;
    private String date1ESP2;
    private String heure1ESP2;
    private String date2ESP2;
    private String heure2ESP2;

    private Long italien;
    private Long italiennt;
    private String date1ITA;
    private String heure1ITA;
    private String date2ITA;
    private String heure2ITA;

    private Long latin;
    private Long latinnt;
    private String date1LAT;
    private String heure1LAT;
    private String date2LAT;
    private String heure2LAT;

    private Long portugaisLV1;
    private Long portugaisLV1nt;
    private String date1PORT1;
    private String heure1PORT1;
    private String date2PORT1;
    private String heure2PORT1;

    private Long portugaisLV2;
    private Long portugaisLV2nt;
    private String date1PORT2;
    private String heure1PORT2;
    private String date2PORT2;
    private String heure2PORT2;

    private Long russe;
    private Long russent;
    private String date1RUS;
    private String heure1RUS;
    private String date2RUS;
    private String heure2RUS;


    private Long pcL;
    private Long pcLnt;
    private String date1PCL;
    private String heure1PCL;
    private String date2PCL;
    private String heure2PCL;

    private Long svtL;
    private Long svtLnt;
    private String date1SVTL;
    private String heure1SVTL;
    private String date2SVTL;
    private String heure2SVTL;

    private Long gelec;
    private Long gelecnt;
    private String date1GE;
    private String heure1GE;
    private String date2GE;
    private String heure2GE;

    private Long gemec;
    private Long gemecnt;
    private String date1GM;
    private String heure1GM;
    private String date2GM;
    private String heure2GM;

    private Long mo;
    private Long mont;
    private String date1MO;
    private String heure1MO;
    private String date2MO;
    private String heure2MO;

    private Long ses;
    private Long sesnt;
    private String date1SES;
    private String heure1SES;
    private String date2SES;
    private String heure2SES;

    private Long gcf;
    private Long gcfnt;
    private String date1GCF;
    private String heure1GCF;
    private String date2GCF;
    private String heure2GCF;
}
