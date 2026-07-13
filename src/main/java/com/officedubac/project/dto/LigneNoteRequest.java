package com.officedubac.project.dto;


public record LigneNoteRequest(
        Integer groupe,
        String matiereRefId,
        String libelleMatiere,
        Double coefficient,
        Double note
) {}