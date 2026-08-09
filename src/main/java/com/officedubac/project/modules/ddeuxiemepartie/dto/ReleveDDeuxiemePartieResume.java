package com.officedubac.project.modules.ddeuxiemepartie.dto;

import com.officedubac.project.modules.ddeuxiemepartie.model.Enums.DecisionJury;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReleveDDeuxiemePartieResume {
    private String id;
    private String numeroTable;
    private String nomPrenom;
    private String juryNumero;
    private Integer totalGeneral;
    private DecisionJury decision;
    private Instant createdAt;
}
