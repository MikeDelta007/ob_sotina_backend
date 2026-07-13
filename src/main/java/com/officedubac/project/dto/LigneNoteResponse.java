package com.officedubac.project.dto;


public record LigneNoteResponse(
        Integer groupe,
        String libelleMatiere,
        Double coefficient,
        Double note,
        Double points
) {}