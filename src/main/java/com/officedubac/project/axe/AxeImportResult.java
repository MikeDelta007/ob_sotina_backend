package com.officedubac.project.axe;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AxeImportResult {
    private int axesImportes;
    private int lignesImportees;
    private int lignesIgnorees;
    // Une entrée par ligne non résolue (ni ID ni nom reconnu en base) — numéro d'axe,
    // numéro de ligne dans l'onglet "Détail" et nom tel qu'écrit, pour correction manuelle.
    private List<String> avertissements;
}
