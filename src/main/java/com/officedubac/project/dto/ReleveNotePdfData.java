package com.officedubac.project.dto;

import java.time.LocalDate;
import java.util.List;

public record ReleveNotePdfData(
        String serieCode,
        String optionLabel,
        String session,
        String juryNumero,
        String annee,
        String nomComplet,
        LocalDate dateNaissance,
        String lieuNaissance,
        String etablissement,
        String indicatif,
        String optionsCandidat,
        List<LigneNoteResponse> lignesGroupe1,
        List<LigneNoteResponse> lignesGroupe2,
        Double totalGroupe1,
        Double surGroupe1,
        Double totalGroupe2,
        Double surGroupe2,
        String mention,
        String decision
) {}