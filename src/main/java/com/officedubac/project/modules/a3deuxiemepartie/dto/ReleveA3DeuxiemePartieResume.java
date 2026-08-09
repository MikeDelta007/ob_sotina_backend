package com.officedubac.project.modules.a3deuxiemepartie.dto;

import com.officedubac.project.modules.a3deuxiemepartie.model.Enums.DecisionJury;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReleveA3DeuxiemePartieResume {
    private String id;
    private String numeroTable;
    private String nomPrenom;
    private String juryNumero;
    private Integer totalGeneral;
    private DecisionJury decision;
    private Instant createdAt;
}
