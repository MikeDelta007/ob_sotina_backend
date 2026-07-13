package com.officedubac.project.dto;

public record MatiereReferentielDTO(
        String id,
        String libelle,
        Double coefficient,
        Integer ordre
) {}