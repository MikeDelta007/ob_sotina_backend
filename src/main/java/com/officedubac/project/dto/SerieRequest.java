package com.officedubac.project.dto;

import java.util.List;

public record SerieRequest(
        String code,
        String libelle,
        List<MatiereReferentielRequest> matieresGroupe1
) {}