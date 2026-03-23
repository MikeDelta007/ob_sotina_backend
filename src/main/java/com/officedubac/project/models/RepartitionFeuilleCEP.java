package com.officedubac.project.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "repartition_feuille_CEP")
public class RepartitionFeuilleCEP
{
    @Id
    private String id;
    private Integer session;
    private String centreEcrit;
    private String academia;
    private String centreExamen;
    private Long effectif;
    private Boolean cs;
    private Boolean cp;
    private Long F6;
    private Long Lprime;
    private Long L1A;
    private Long L1B;
    private Long L2;
    private Long LA;
    private Long LAR;
    private Long S1;
    private Long S1A;
    private Long S2;
    private Long S2A;
    private Long S3;
    private Long S4;
    private Long S5;
    private Long STEG;
    private Long STIDD;
    private Long T1;
    private Long T2;
    private Long feuille_double;
    private Long feuille_intercalaire;
    private Long feuille_brouillon;
    private Long numOrdre;

}
