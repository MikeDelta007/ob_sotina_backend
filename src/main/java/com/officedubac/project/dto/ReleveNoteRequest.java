package com.officedubac.project.dto;

import java.time.LocalDate;
import java.util.List;


public record ReleveNoteRequest(
        String serieId,
        String nom,
        String prenom,
        LocalDate dateNaissance,
        String lieuNaissance,
        String centreExamen,
        String numeroTable,
        String session,
        String annee,
        List<LigneNoteRequest> lignesGroupe1,
        List<LigneNoteRequest> lignesGroupe2
) {}