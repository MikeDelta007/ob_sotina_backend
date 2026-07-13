package com.officedubac.project.dto;

import java.util.List;

public record SerieDetailResponse(
        String id,
        String code,
        String libelle,
        List<MatiereReferentielDTO> matieresGroupe1
) {}