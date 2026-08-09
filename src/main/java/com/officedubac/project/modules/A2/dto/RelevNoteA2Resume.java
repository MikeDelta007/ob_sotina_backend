package com.officedubac.project.modules.A2.dto;

import com.officedubac.project.modules.A2.model.Enums.DecisionJury;
import com.officedubac.project.modules.A2.model.Enums.Mention;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Vue résumée d'un relevé A2 pour les listes paginées (évite de renvoyer
 * toute la grille de notes quand seul un aperçu est nécessaire).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelevNoteA2Resume {

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