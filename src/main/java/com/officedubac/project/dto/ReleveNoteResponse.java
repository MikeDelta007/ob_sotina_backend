package com.officedubac.project.dto;

import java.util.List;

public record ReleveNoteResponse(
        String id,
        String nom,
        String prenom,
        String serieCode,
        Double moyenneGenerale,
        String mention,
        List<LigneNoteResponse> lignesGroupe1,
        List<LigneNoteResponse> lignesGroupe2
) {}
