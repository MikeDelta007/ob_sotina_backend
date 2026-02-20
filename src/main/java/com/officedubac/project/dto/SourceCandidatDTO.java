package com.officedubac.project.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;

@Data
public class SourceCandidatDTO
{
        private String firstname;//0
        private String lastname;//1
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate date_birth;//2
        private String place_birth;//3
        private String nationality;//4
        private Integer age;//5
        private Integer tableNum;//6
        private Integer session;//7
        private Integer jury;//8
        private String serie;//9
        private String gender;//10
        private String etablissement;//11
        private String centreEcritPrincipal;//12
        private String centreEcritSecondaire;//12
        private String centreExamen;//13
        private String matiere1;//14
        private String matiere2;//15
        private String matiere3;//16
        private String eprFacListA;//17
        private String eprFacListB;//18
        private String acaEtab;//19
        private String acaCentEcrit;//19
}
