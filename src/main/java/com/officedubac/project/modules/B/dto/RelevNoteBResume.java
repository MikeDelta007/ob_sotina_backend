package com.officedubac.project.modules.B.dto;

import com.officedubac.project.modules.B.model.Enums.DecisionJury;
import com.officedubac.project.modules.B.model.Enums.Mention;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelevNoteBResume {

    private String id;
    private String numeroTable;
    private String nomPrenom;
    private String juryNumero;
    private Integer annee;
    private Integer totalDefinitif;
    private DecisionJury decision;
    private Mention mention;
    private Instant createdAt;
}
