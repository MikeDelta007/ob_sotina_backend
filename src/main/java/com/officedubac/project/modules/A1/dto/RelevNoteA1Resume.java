package com.officedubac.project.modules.A1.dto;

import com.officedubac.project.modules.A1.model.Enums.DecisionJury;
import com.officedubac.project.modules.A1.model.Enums.Mention;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Projection légère d'un RelevNoteA1, pour l'affichage en liste/tableau
 * côté frontend (on évite de renvoyer notesPremierGroupe, epreuvesDeControle,
 * etc. qui ne servent pas à cet écran).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelevNoteA1Resume {
    private String id;
    private String nomPrenom;
    private String juryNumero;
    private Integer annee;
    private Integer totalDefinitif;
    private DecisionJury decision;
    private Mention mention;
    private Instant createdAt;
}
