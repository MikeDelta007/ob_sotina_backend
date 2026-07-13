package com.officedubac.project.dto;

import java.util.List;


public record GabaritDTO(
        String serieId,
        String code,
        String libelle,
        List<String> champsEntete,
        List<MatiereReferentielDTO> matieresGroupe1
) {}
