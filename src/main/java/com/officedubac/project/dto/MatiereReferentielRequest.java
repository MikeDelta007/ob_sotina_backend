package com.officedubac.project.dto;

public record MatiereReferentielRequest(
        String id,          // optionnel : null si nouvelle matière, rempli si modification
        String libelle,
        Double coefficient,
        Integer ordre
) {}